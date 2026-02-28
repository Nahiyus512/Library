package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.controller.vo.feature.FeatureEventBatchRequest;
import com.wkc.library.controller.vo.feature.FeatureEventBatchResult;
import com.wkc.library.controller.vo.feature.FeatureEventPayload;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.feature.FeaturePageEvent;
import com.wkc.library.service.BookService;
import com.wkc.library.service.FeatureEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FeatureEventServiceImpl implements FeatureEventService {

    private static final int MAX_BATCH_SIZE = 50;
    private static final Set<String> ALLOWED_EVENT_TYPES = new HashSet<String>(Arrays.asList(
            "enter", "heartbeat", "checkpoint", "rate", "exit", "recommend_accept"
    ));

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BookService bookService;

    @Override
    public FeatureEventBatchResult reportBatch(FeatureEventBatchRequest request) {
        List<FeatureEventPayload> events = request == null ? null : request.getEvents();
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("events 不能为空");
        }
        if (events.size() > MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("单次最多上报 50 条事件");
        }

        FeatureEventBatchResult result = new FeatureEventBatchResult();
        result.setReceived(events.size());
        result.setInserted(0);
        result.setDuplicated(0);

        Set<String> currentBatchEventIds = new HashSet<String>();
        for (FeatureEventPayload payload : events) {
            validatePayload(payload);

            if (!currentBatchEventIds.add(payload.getEventId())) {
                result.setDuplicated(result.getDuplicated() + 1);
                continue;
            }

            if (existsByEventId(payload.getEventId())) {
                result.setDuplicated(result.getDuplicated() + 1);
                continue;
            }

            FeaturePageEvent event = toEntity(payload);
            try {
                mongoTemplate.insert(event);
                result.setInserted(result.getInserted() + 1);
            } catch (DuplicateKeyException e) {
                result.setDuplicated(result.getDuplicated() + 1);
                log.warn("duplicate feature eventId: {}", payload.getEventId());
            }
        }
        return result;
    }

    private boolean existsByEventId(String eventId) {
        Query query = new Query(Criteria.where("eventId").is(eventId));
        return mongoTemplate.exists(query, FeaturePageEvent.class);
    }

    private void validatePayload(FeatureEventPayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("事件不能为空");
        }
        if (!StringUtils.hasText(payload.getEventId())) {
            throw new IllegalArgumentException("eventId 不能为空");
        }
        if (payload.getUserId() == null) {
            throw new IllegalArgumentException("userId 不能为空");
        }
        if (!StringUtils.hasText(payload.getSessionId())) {
            throw new IllegalArgumentException("sessionId 不能为空");
        }
        if (!StringUtils.hasText(payload.getEventType()) || !ALLOWED_EVENT_TYPES.contains(payload.getEventType())) {
            throw new IllegalArgumentException("eventType 非法");
        }
        if (!StringUtils.hasText(payload.getRoutePath())) {
            throw new IllegalArgumentException("routePath 不能为空");
        }
        if (!StringUtils.hasText(payload.getSource())) {
            throw new IllegalArgumentException("source 不能为空");
        }
        if (payload.getBookId() == null && !StringUtils.hasText(payload.getBookName())) {
            throw new IllegalArgumentException("bookId 和 bookName 不能同时为空");
        }
        if (StringUtils.hasText(payload.getCreatedAt())) {
            parseDate(payload.getCreatedAt());
        }
    }

    private FeaturePageEvent toEntity(FeatureEventPayload payload) {
        FeaturePageEvent event = new FeaturePageEvent();
        event.setEventId(payload.getEventId());
        event.setUserId(payload.getUserId());
        event.setSessionId(payload.getSessionId());
        event.setTraceId(payload.getTraceId());
        event.setBookId(resolveBookId(payload));
        event.setBookName(payload.getBookName());
        event.setRoutePath(payload.getRoutePath());
        event.setSource(payload.getSource());
        event.setEventType(payload.getEventType());
        event.setCheckpointKey(payload.getCheckpointKey());
        event.setLikeLevel(payload.getLikeLevel());
        event.setDurationMs(payload.getDurationMs());
        event.setScrollDepth(payload.getScrollDepth());
        event.setExtra(payload.getExtra());
        event.setCreatedAt(parseOrNow(payload.getCreatedAt()));
        return event;
    }

    private Integer resolveBookId(FeatureEventPayload payload) {
        if (payload.getBookId() != null) {
            return payload.getBookId();
        }
        if (!StringUtils.hasText(payload.getBookName())) {
            return null;
        }
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>();
        wrapper.eq(Book::getBookName, payload.getBookName().trim());
        Book book = bookService.getOne(wrapper);
        return book == null ? null : book.getBookId();
    }

    private Date parseOrNow(String createdAt) {
        if (!StringUtils.hasText(createdAt)) {
            return new Date();
        }
        return parseDate(createdAt);
    }

    private Date parseDate(String isoDateTime) {
        try {
            return Date.from(Instant.parse(isoDateTime));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("createdAt 必须是 ISO-8601 UTC 时间格式");
        }
    }
}

