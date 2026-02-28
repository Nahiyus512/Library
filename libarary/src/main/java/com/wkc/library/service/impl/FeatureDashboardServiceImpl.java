package com.wkc.library.service.impl;

import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardBookItem;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardFunnelResponse;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardOverviewResponse;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardSourceItem;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardTrendPoint;
import com.wkc.library.entity.feature.FeaturePageEvent;
import com.wkc.library.service.FeatureDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeatureDashboardServiceImpl implements FeatureDashboardService {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public FeatureDashboardOverviewResponse getOverview(String from, String to) {
        DateRange range = parseRange(from, to);
        List<FeaturePageEvent> events = queryEvents(range.start, range.endExclusive, null, null);
        List<FeaturePageEvent> acceptEvents = queryEvents(range.start, new Date(range.endExclusive.getTime() + 24L * 3600 * 1000), "recommend_accept", null);

        FeatureDashboardOverviewResponse response = new FeatureDashboardOverviewResponse();
        Set<Integer> enterUv = distinctUsers(events, "enter");
        Set<Integer> readingDecisionEnterUv = events.stream()
                .filter(e -> "enter".equals(e.getEventType()))
                .filter(e -> StringUtils.hasText(e.getSource()) && e.getSource().startsWith("reading_decision"))
                .map(FeaturePageEvent::getUserId)
                .filter(this::validUserId)
                .collect(Collectors.toSet());
        Set<Integer> readingDecisionUv = events.stream()
                .filter(e -> StringUtils.hasText(e.getSource()) && e.getSource().startsWith("reading_decision"))
                .map(FeaturePageEvent::getUserId)
                .filter(this::validUserId)
                .collect(Collectors.toSet());
        Set<String> enterSessions = distinctSessions(events, "enter");
        Set<String> completeSessions = distinctSessionsByTypes(events, eventTypeSet("rate", "checkpoint"));
        Set<String> rateSessions = distinctSessions(events, "rate");
        List<FeaturePageEvent> rateEvents = filterByType(events, "rate");
        List<FeaturePageEvent> exitEvents = filterByType(events, "exit");

        response.setEnterUv(enterUv.size());
        response.setEnterRate(safeRate(readingDecisionEnterUv.size(), readingDecisionUv.size()));
        response.setCompleteRate(safeRate(completeSessions.size(), enterSessions.size()));
        response.setRateSubmitRate(safeRate(rateSessions.size(), enterSessions.size()));
        response.setAvgDurationMs(avgDuration(exitEvents));
        response.setLike2Rate(safeRate((int) rateEvents.stream().filter(e -> Integer.valueOf(2).equals(e.getLikeLevel())).count(), rateEvents.size()));
        response.setAcceptRate24h(calculateAcceptRate24h(rateEvents, acceptEvents));
        return response;
    }

    @Override
    public List<FeatureDashboardTrendPoint> getTrend(String from, String to, String granularity) {
        DateRange range = parseRange(from, to);
        if (!"day".equalsIgnoreCase(granularity)) {
            throw new IllegalArgumentException("仅支持 day 粒度");
        }

        List<FeaturePageEvent> events = queryEvents(range.start, range.endExclusive, null, null);
        List<FeaturePageEvent> acceptEvents = queryEvents(range.start, new Date(range.endExclusive.getTime() + 24L * 3600 * 1000), "recommend_accept", null);
        Map<String, List<FeaturePageEvent>> dayEvents = events.stream()
                .collect(Collectors.groupingBy(e -> formatDay(e.getCreatedAt())));

        List<FeatureDashboardTrendPoint> result = new ArrayList<FeatureDashboardTrendPoint>();
        LocalDate day = range.startLocalDate;
        while (!day.isAfter(range.endLocalDate)) {
            String key = day.format(DAY_FORMATTER);
            List<FeaturePageEvent> bucket = dayEvents.getOrDefault(key, Collections.emptyList());
            List<FeaturePageEvent> dayRateEvents = filterByType(bucket, "rate");

            FeatureDashboardTrendPoint point = new FeatureDashboardTrendPoint();
            point.setDate(key);
            point.setEnterUv(distinctUsers(bucket, "enter").size());
            point.setCompleteRate(safeRate(distinctSessionsByTypes(bucket, eventTypeSet("rate", "checkpoint")).size(), distinctSessions(bucket, "enter").size()));
            point.setRateSubmitRate(safeRate(distinctSessions(bucket, "rate").size(), distinctSessions(bucket, "enter").size()));
            point.setAcceptRate24h(calculateAcceptRate24h(dayRateEvents, acceptEvents));
            result.add(point);
            day = day.plusDays(1);
        }
        return result;
    }

    @Override
    public List<FeatureDashboardBookItem> getBooks(String from, String to, String sort, Integer limit) {
        DateRange range = parseRange(from, to);
        int safeLimit = limit == null ? 20 : Math.max(1, Math.min(limit, 100));
        List<FeaturePageEvent> events = queryEvents(range.start, range.endExclusive, null, null);
        List<FeaturePageEvent> acceptEvents = queryEvents(range.start, new Date(range.endExclusive.getTime() + 24L * 3600 * 1000), "recommend_accept", null);

        Map<String, List<FeaturePageEvent>> grouped = events.stream()
                .filter(e -> e.getBookId() != null || StringUtils.hasText(e.getBookName()))
                .collect(Collectors.groupingBy(this::bookGroupKey));

        List<FeatureDashboardBookItem> rows = new ArrayList<FeatureDashboardBookItem>();
        for (List<FeaturePageEvent> bucket : grouped.values()) {
            FeaturePageEvent sample = bucket.get(0);
            List<FeaturePageEvent> rateEvents = filterByType(bucket, "rate");
            FeatureDashboardBookItem item = new FeatureDashboardBookItem();
            item.setBookId(sample.getBookId());
            item.setBookName(sample.getBookName());
            item.setEnterUv(distinctUsers(bucket, "enter").size());
            item.setEnterPv((int) bucket.stream().filter(e -> "enter".equals(e.getEventType())).count());
            item.setCompleteRate(safeRate(distinctSessionsByTypes(bucket, eventTypeSet("rate", "checkpoint")).size(), distinctSessions(bucket, "enter").size()));
            item.setAvgDurationMs(avgDuration(filterByType(bucket, "exit")));
            item.setLike0Count((int) rateEvents.stream().filter(e -> Integer.valueOf(0).equals(e.getLikeLevel())).count());
            item.setLike1Count((int) rateEvents.stream().filter(e -> Integer.valueOf(1).equals(e.getLikeLevel())).count());
            item.setLike2Count((int) rateEvents.stream().filter(e -> Integer.valueOf(2).equals(e.getLikeLevel())).count());
            item.setAcceptRate(calculateAcceptRate24h(rateEvents, acceptEvents));
            rows.add(item);
        }

        rows.sort(bookComparator(sort));
        if (rows.size() > safeLimit) {
            return rows.subList(0, safeLimit);
        }
        return rows;
    }

    @Override
    public FeatureDashboardFunnelResponse getFunnel(Integer bookId, String from, String to) {
        DateRange range = parseRange(from, to);
        List<FeaturePageEvent> events = queryEvents(range.start, range.endExclusive, null, bookId);

        FeatureDashboardFunnelResponse response = new FeatureDashboardFunnelResponse();
        response.setEnter(distinctSessions(events, "enter").size());
        response.setCheckpoint(distinctSessions(events, "checkpoint").size());
        response.setRate(distinctSessions(events, "rate").size());
        response.setRecommendAccept(distinctUsers(events, "recommend_accept").size());
        return response;
    }

    @Override
    public List<FeatureDashboardSourceItem> getSourceDistribution(String from, String to) {
        DateRange range = parseRange(from, to);
        List<FeaturePageEvent> events = queryEvents(range.start, range.endExclusive, null, null);
        List<FeaturePageEvent> acceptEvents = queryEvents(range.start, new Date(range.endExclusive.getTime() + 24L * 3600 * 1000), "recommend_accept", null);

        Map<String, List<FeaturePageEvent>> grouped = events.stream()
                .filter(e -> StringUtils.hasText(e.getSource()))
                .collect(Collectors.groupingBy(FeaturePageEvent::getSource));

        int totalEnterPv = (int) events.stream().filter(e -> "enter".equals(e.getEventType())).count();
        List<FeatureDashboardSourceItem> rows = new ArrayList<FeatureDashboardSourceItem>();
        for (Map.Entry<String, List<FeaturePageEvent>> entry : grouped.entrySet()) {
            String source = entry.getKey();
            List<FeaturePageEvent> bucket = entry.getValue();
            List<FeaturePageEvent> rateEvents = filterByType(bucket, "rate");

            FeatureDashboardSourceItem item = new FeatureDashboardSourceItem();
            item.setSource(source);
            item.setEnterPv((int) bucket.stream().filter(e -> "enter".equals(e.getEventType())).count());
            item.setTrafficShare(safeRate(item.getEnterPv(), totalEnterPv));
            item.setCompleteRate(safeRate(distinctSessionsByTypes(bucket, eventTypeSet("rate", "checkpoint")).size(), distinctSessions(bucket, "enter").size()));
            item.setAcceptRate(calculateAcceptRate24h(rateEvents, acceptEvents));
            rows.add(item);
        }

        rows.sort(Comparator.comparing(FeatureDashboardSourceItem::getEnterPv, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
        return rows;
    }

    private Comparator<FeatureDashboardBookItem> bookComparator(String sort) {
        String sortKey = StringUtils.hasText(sort) ? sort : "acceptRate";
        Map<String, Comparator<FeatureDashboardBookItem>> mapping = new HashMap<String, Comparator<FeatureDashboardBookItem>>();
        mapping.put("enterUv", Comparator.comparing(FeatureDashboardBookItem::getEnterUv, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
        mapping.put("enterPv", Comparator.comparing(FeatureDashboardBookItem::getEnterPv, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
        mapping.put("completeRate", Comparator.comparing(FeatureDashboardBookItem::getCompleteRate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
        mapping.put("avgDurationMs", Comparator.comparing(FeatureDashboardBookItem::getAvgDurationMs, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
        mapping.put("acceptRate", Comparator.comparing(FeatureDashboardBookItem::getAcceptRate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());

        return mapping.getOrDefault(sortKey, mapping.get("acceptRate"));
    }

    private List<FeaturePageEvent> queryEvents(Date start, Date endExclusive, String eventType, Integer bookId) {
        Criteria criteria = Criteria.where("createdAt").gte(start).lt(endExclusive);
        if (StringUtils.hasText(eventType)) {
            criteria.and("eventType").is(eventType);
        }
        if (bookId != null) {
            criteria.and("bookId").is(bookId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, FeaturePageEvent.class);
    }

    private Set<Integer> distinctUsers(List<FeaturePageEvent> events, String eventType) {
        return events.stream()
                .filter(e -> eventType.equals(e.getEventType()))
                .map(FeaturePageEvent::getUserId)
                .filter(this::validUserId)
                .collect(Collectors.toSet());
    }

    private Set<String> distinctSessions(List<FeaturePageEvent> events, String eventType) {
        return events.stream()
                .filter(e -> eventType.equals(e.getEventType()))
                .map(FeaturePageEvent::getSessionId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    private Set<String> distinctSessionsByTypes(List<FeaturePageEvent> events, Set<String> eventTypes) {
        return events.stream()
                .filter(e -> eventTypes.contains(e.getEventType()))
                .map(FeaturePageEvent::getSessionId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    private List<FeaturePageEvent> filterByType(List<FeaturePageEvent> events, String type) {
        return events.stream().filter(e -> type.equals(e.getEventType())).collect(Collectors.toList());
    }

    private Long avgDuration(List<FeaturePageEvent> exitEvents) {
        long count = exitEvents.stream().filter(e -> e.getDurationMs() != null && e.getDurationMs() > 0).count();
        if (count == 0) {
            return 0L;
        }
        long sum = exitEvents.stream()
                .map(FeaturePageEvent::getDurationMs)
                .filter(v -> v != null && v > 0)
                .mapToLong(Long::longValue)
                .sum();
        return Math.round((double) sum / count);
    }

    private Double calculateAcceptRate24h(List<FeaturePageEvent> rateEvents, List<FeaturePageEvent> acceptEvents) {
        Map<Integer, List<Long>> rateTimeByUser = new HashMap<Integer, List<Long>>();
        for (FeaturePageEvent rateEvent : rateEvents) {
            if (!validUserId(rateEvent.getUserId()) || rateEvent.getCreatedAt() == null) {
                continue;
            }
            rateTimeByUser.computeIfAbsent(rateEvent.getUserId(), k -> new ArrayList<Long>()).add(rateEvent.getCreatedAt().getTime());
        }
        if (rateTimeByUser.isEmpty()) {
            return 0D;
        }

        Map<Integer, List<Long>> acceptTimeByUser = new HashMap<Integer, List<Long>>();
        for (FeaturePageEvent acceptEvent : acceptEvents) {
            if (!validUserId(acceptEvent.getUserId()) || acceptEvent.getCreatedAt() == null) {
                continue;
            }
            acceptTimeByUser.computeIfAbsent(acceptEvent.getUserId(), k -> new ArrayList<Long>()).add(acceptEvent.getCreatedAt().getTime());
        }

        int acceptedUserCount = 0;
        for (Map.Entry<Integer, List<Long>> entry : rateTimeByUser.entrySet()) {
            Integer userId = entry.getKey();
            List<Long> rateTimes = entry.getValue();
            List<Long> acceptTimes = acceptTimeByUser.getOrDefault(userId, Collections.emptyList());
            if (acceptTimes.isEmpty()) {
                continue;
            }
            boolean accepted = false;
            for (Long rateTime : rateTimes) {
                long max = rateTime + 24L * 3600 * 1000;
                for (Long acceptTime : acceptTimes) {
                    if (acceptTime >= rateTime && acceptTime <= max) {
                        accepted = true;
                        break;
                    }
                }
                if (accepted) {
                    break;
                }
            }
            if (accepted) {
                acceptedUserCount++;
            }
        }
        return safeRate(acceptedUserCount, rateTimeByUser.size());
    }

    private String formatDay(Date date) {
        if (date == null) {
            return "";
        }
        return date.toInstant().atZone(ZONE_ID).toLocalDate().format(DAY_FORMATTER);
    }

    private boolean validUserId(Integer userId) {
        return userId != null && userId > 0;
    }

    private String bookGroupKey(FeaturePageEvent event) {
        Integer bookId = event.getBookId();
        String bookName = event.getBookName() == null ? "" : event.getBookName();
        return (bookId == null ? "null" : String.valueOf(bookId)) + "|" + bookName;
    }

    private Set<String> eventTypeSet(String... types) {
        Set<String> set = new HashSet<String>();
        if (types == null) {
            return set;
        }
        Collections.addAll(set, types);
        return set;
    }

    private double safeRate(int numerator, int denominator) {
        if (denominator <= 0 || numerator <= 0) {
            return 0D;
        }
        return (double) numerator / denominator;
    }

    private DateRange parseRange(String from, String to) {
        try {
            LocalDate fromDate = LocalDate.parse(from, DAY_FORMATTER);
            LocalDate toDate = LocalDate.parse(to, DAY_FORMATTER);
            if (toDate.isBefore(fromDate)) {
                throw new IllegalArgumentException("to 不能早于 from");
            }
            Date start = Date.from(fromDate.atStartOfDay(ZONE_ID).toInstant());
            Date endExclusive = Date.from(toDate.plusDays(1).atStartOfDay(ZONE_ID).toInstant());
            return new DateRange(fromDate, toDate, start, endExclusive);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期格式必须是 yyyy-MM-dd");
        }
    }

    private static class DateRange {
        private final LocalDate startLocalDate;
        private final LocalDate endLocalDate;
        private final Date start;
        private final Date endExclusive;

        private DateRange(LocalDate startLocalDate, LocalDate endLocalDate, Date start, Date endExclusive) {
            this.startLocalDate = startLocalDate;
            this.endLocalDate = endLocalDate;
            this.start = start;
            this.endExclusive = endExclusive;
        }
    }
}
