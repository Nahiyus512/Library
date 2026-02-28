package com.wkc.library.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.controller.vo.agent.AgentProfileInsightResponse;
import com.wkc.library.controller.vo.agent.AgentRecommendExplanation;
import com.wkc.library.controller.vo.agent.AgentRecommendItem;
import com.wkc.library.controller.vo.agent.AgentRecommendResponse;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookLike;
import com.wkc.library.entity.BookScore;
import com.wkc.library.entity.agent.AgentDecisionTrace;
import com.wkc.library.entity.feature.FeaturePageEvent;
import com.wkc.library.mapper.BookLikeMapper;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.mapper.BookScoreMapper;
import com.wkc.library.service.BookScoreService;
import com.wkc.library.service.agent.AgentRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class AgentRecommendServiceImpl implements AgentRecommendService {

    private static final List<String> STRATEGIES = Arrays.asList(
            "user_cf", "item_cf", "content_based", "lfm", "association_rule"
    );
    private static final int FEATURE_ANALYTICS_LOOKBACK_DAYS = 30;
    private static final double FEATURE_BOOST_MAX = 0.08;

    @Autowired
    private BookScoreService bookScoreService;

    @Autowired
    private BookScoreMapper bookScoreMapper;

    @Autowired
    private BookLikeMapper bookLikeMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public AgentRecommendResponse recommend(Integer userId, String demand, Integer topN) {
        int limit = (topN == null || topN <= 0) ? 5 : Math.min(topN, 20);
        String normalizedDemand = demand == null ? "" : demand.trim();

        String intent = recognizeIntent(normalizedDemand);
        Map<String, Integer> profile = analyzeProfile(userId);
        Map<String, Double> weights = allocateWeights(intent, profile);

        Map<String, List<Book>> strategyBooks = new LinkedHashMap<>();
        for (String strategy : STRATEGIES) {
            List<Book> books = bookScoreService.recommend(userId, strategy);
            strategyBooks.put(strategy, books == null ? Collections.emptyList() : books);
        }

        ExplanationContext explanationContext = buildExplanationContext(userId);
        List<AgentRecommendItem> merged = rankFusion(strategyBooks, weights, intent, limit, explanationContext);
        if (merged.isEmpty()) {
            merged = fallbackByPopularity(limit, intent, explanationContext);
        }

        List<String> decisionPath = buildDecisionPath(
                intent,
                profile,
                weights,
                !merged.isEmpty(),
                explanationContext.featureAnalyticsByBook != null && !explanationContext.featureAnalyticsByBook.isEmpty()
        );
        String strategyDesc = buildStrategyDescription(intent, profile, weights);

        AgentRecommendResponse response = new AgentRecommendResponse();
        response.setIntent(intent);
        response.setStrategyUsed("hybrid_strategy");
        response.setStrategyDescription(strategyDesc);
        response.setWeights(weights);
        response.setDecisionPath(decisionPath);
        response.setRecommendedBooks(merged);

        String traceId = saveTrace(userId, normalizedDemand, intent, weights, decisionPath, merged);
        response.setDecisionTraceId(traceId);
        return response;
    }

    @Override
    public AgentDecisionTrace getDecisionTrace(String traceId) {
        if (!StringUtils.hasText(traceId)) {
            return null;
        }
        return mongoTemplate.findById(traceId, AgentDecisionTrace.class);
    }

    @Override
    public List<AgentDecisionTrace> getLatestDecisionTraces(Integer userId, Integer limit) {
        if (userId == null) {
            return Collections.emptyList();
        }
        int safeLimit = (limit == null || limit <= 0) ? 3 : Math.min(limit, 10);
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.limit(safeLimit);
        return mongoTemplate.find(query, AgentDecisionTrace.class);
    }

    @Override
    public AgentProfileInsightResponse getProfileInsight(Integer userId) {
        AgentProfileInsightResponse response = new AgentProfileInsightResponse();
        if (userId == null) {
            response.setInterestCategoryDistribution(Collections.emptyMap());
            response.setReadingActiveTrend(Collections.emptyList());
            response.setScoreRangeDistribution(Collections.emptyMap());
            response.setHistoryBehaviorStats(Collections.emptyMap());
            return response;
        }

        List<BookLike> userLikes = bookLikeMapper.selectList(new LambdaQueryWrapper<BookLike>()
                .eq(BookLike::getUserId, userId)
                .ge(BookLike::getLikeLevel, 1));
        List<BookScore> userScores = bookScoreMapper.selectList(new LambdaQueryWrapper<BookScore>()
                .eq(BookScore::getUserId, userId));

        Map<Integer, Book> bookById = loadBookMapByIds(userLikes.stream().map(BookLike::getBookId).collect(Collectors.toSet()));

        Map<String, Integer> classCounter = new LinkedHashMap<>();
        for (BookLike like : userLikes) {
            Book book = bookById.get(like.getBookId());
            for (String classify : splitClassify(book == null ? null : book.getBookClassify())) {
                classCounter.merge(classify, 1, Integer::sum);
            }
        }
        response.setInterestCategoryDistribution(normalizeDistribution(classCounter));

        Map<String, Integer> trend = new TreeMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (BookScore score : userScores) {
            if (!StringUtils.hasText(score.getTime())) {
                continue;
            }
            LocalDate d = parseDate(score.getTime());
            if (d != null) {
                trend.merge(d.format(monthFormatter), 1, Integer::sum);
            }
        }
        for (BookLike like : userLikes) {
            if (like.getCreateTime() == null) {
                continue;
            }
            LocalDate d = like.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            trend.merge(d.format(monthFormatter), 1, Integer::sum);
        }
        List<Map<String, Object>> trendRows = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : trend.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("period", entry.getKey());
            row.put("count", entry.getValue());
            trendRows.add(row);
        }
        response.setReadingActiveTrend(trendRows);

        Map<String, Integer> scoreRange = new LinkedHashMap<>();
        scoreRange.put("<=2", 0);
        scoreRange.put("3", 0);
        scoreRange.put("4", 0);
        scoreRange.put(">=5", 0);
        for (BookScore score : userScores) {
            int value = score.getScore() == null ? 0 : score.getScore();
            if (value <= 2) {
                scoreRange.merge("<=2", 1, Integer::sum);
            } else if (value == 3) {
                scoreRange.merge("3", 1, Integer::sum);
            } else if (value == 4) {
                scoreRange.merge("4", 1, Integer::sum);
            } else {
                scoreRange.merge(">=5", 1, Integer::sum);
            }
        }
        response.setScoreRangeDistribution(scoreRange);

        LocalDate since = LocalDate.now().minusDays(30);
        int recentScores = 0;
        for (BookScore score : userScores) {
            LocalDate d = parseDate(score.getTime());
            if (d != null && !d.isBefore(since)) {
                recentScores++;
            }
        }
        int recentLikes = 0;
        for (BookLike like : userLikes) {
            if (like.getCreateTime() == null) {
                continue;
            }
            LocalDate d = like.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!d.isBefore(since)) {
                recentLikes++;
            }
        }
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("totalScores", userScores.size());
        stats.put("totalLikes", userLikes.size());
        stats.put("distinctInterestCategories", classCounter.size());
        stats.put("recent30dScores", recentScores);
        stats.put("recent30dLikes", recentLikes);
        response.setHistoryBehaviorStats(stats);

        return response;
    }

    private String recognizeIntent(String demand) {
        if (!StringUtils.hasText(demand)) {
            return "interest_exploration";
        }
        String d = demand.toLowerCase(Locale.ROOT);
        if (d.contains("goal_learning")) {
            return "learning";
        }
        if (d.contains("goal_professional")) {
            return "professional";
        }
        if (d.contains("goal_leisure")) {
            return "leisure";
        }
        if (d.contains("goal_exploration")) {
            return "interest_exploration";
        }
        return "interest_exploration";
    }

    private Map<String, Integer> analyzeProfile(Integer userId) {
        Map<String, Integer> profile = new HashMap<>();
        if (userId == null) {
            profile.put("scoreCount", 0);
            profile.put("likeCount", 0);
            profile.put("classifyDiversity", 0);
            return profile;
        }

        LambdaQueryWrapper<BookScore> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(BookScore::getUserId, userId);
        int scoreCount = Math.toIntExact(bookScoreMapper.selectCount(scoreWrapper));

        LambdaQueryWrapper<BookLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(BookLike::getUserId, userId).ge(BookLike::getLikeLevel, 1);
        List<BookLike> likes = bookLikeMapper.selectList(likeWrapper);

        Set<Integer> bookIds = likes.stream().map(BookLike::getBookId).collect(Collectors.toSet());
        int classifyDiversity = 0;
        if (!bookIds.isEmpty()) {
            classifyDiversity = Math.toIntExact(
                    bookMapper.selectList(new LambdaQueryWrapper<Book>().in(Book::getBookId, bookIds))
                            .stream()
                            .map(Book::getBookClassify)
                            .filter(StringUtils::hasText)
                            .distinct()
                            .count()
            );
        }

        profile.put("scoreCount", scoreCount);
        profile.put("likeCount", likes.size());
        profile.put("classifyDiversity", classifyDiversity);
        return profile;
    }

    private Map<String, Double> allocateWeights(String intent, Map<String, Integer> profile) {
        Map<String, Double> weights = new LinkedHashMap<>();
        weights.put("user_cf", 0.30);
        weights.put("item_cf", 0.20);
        weights.put("content_based", 0.25);
        weights.put("lfm", 0.15);
        weights.put("association_rule", 0.10);

        if ("learning".equals(intent)) {
            weights.put("content_based", 0.38);
            weights.put("lfm", 0.24);
            weights.put("user_cf", 0.16);
            weights.put("item_cf", 0.12);
            weights.put("association_rule", 0.10);
        } else if ("professional".equals(intent)) {
            weights.put("content_based", 0.34);
            weights.put("lfm", 0.28);
            weights.put("user_cf", 0.18);
            weights.put("item_cf", 0.12);
            weights.put("association_rule", 0.08);
        } else if ("leisure".equals(intent)) {
            weights.put("user_cf", 0.36);
            weights.put("association_rule", 0.22);
            weights.put("item_cf", 0.20);
            weights.put("content_based", 0.14);
            weights.put("lfm", 0.08);
        } else if ("interest_exploration".equals(intent)) {
            weights.put("content_based", 0.30);
            weights.put("association_rule", 0.22);
            weights.put("item_cf", 0.20);
            weights.put("user_cf", 0.18);
            weights.put("lfm", 0.10);
        }

        int scoreCount = profile.getOrDefault("scoreCount", 0);
        int likeCount = profile.getOrDefault("likeCount", 0);
        boolean coldStart = scoreCount < 3 && likeCount < 3;
        if (coldStart) {
            if ("learning".equals(intent)) {
                weights.put("user_cf", 0.06);
                weights.put("item_cf", 0.10);
                weights.put("content_based", 0.46);
                weights.put("lfm", 0.16);
                weights.put("association_rule", 0.22);
            } else if ("professional".equals(intent)) {
                weights.put("user_cf", 0.06);
                weights.put("item_cf", 0.10);
                weights.put("content_based", 0.40);
                weights.put("lfm", 0.24);
                weights.put("association_rule", 0.20);
            } else if ("leisure".equals(intent)) {
                weights.put("user_cf", 0.12);
                weights.put("item_cf", 0.20);
                weights.put("content_based", 0.18);
                weights.put("lfm", 0.06);
                weights.put("association_rule", 0.44);
            } else {
                weights.put("user_cf", 0.08);
                weights.put("item_cf", 0.16);
                weights.put("content_based", 0.30);
                weights.put("lfm", 0.10);
                weights.put("association_rule", 0.36);
            }
        }

        normalize(weights);
        return weights;
    }

    private void normalize(Map<String, Double> weights) {
        double sum = 0.0;
        for (String key : weights.keySet()) {
            sum += weights.get(key);
        }
        if (sum <= 0) {
            return;
        }
        for (String key : new ArrayList<>(weights.keySet())) {
            weights.put(key, Math.round((weights.get(key) / sum) * 1000.0) / 1000.0);
        }
    }

    private List<AgentRecommendItem> rankFusion(Map<String, List<Book>> strategyBooks,
                                                Map<String, Double> weights,
                                                String intent,
                                                int topN,
                                                ExplanationContext explanationContext) {
        Map<Integer, Double> scoreMap = new HashMap<>();
        Map<Integer, Book> bookMap = new HashMap<>();
        Map<Integer, Set<String>> hits = new HashMap<>();

        for (Map.Entry<String, List<Book>> entry : strategyBooks.entrySet()) {
            String strategy = entry.getKey();
            List<Book> books = entry.getValue();
            if (books == null || books.isEmpty()) {
                continue;
            }

            int size = books.size();
            for (int i = 0; i < size; i++) {
                Book book = books.get(i);
                if (book == null || book.getBookId() == null) {
                    continue;
                }
                double rankScore = (double) (size - i) / size;
                double weighted = rankScore * weights.getOrDefault(strategy, 0.0);
                double boosted = weighted + intentBoost(intent, book) + featureAnalyticsBoost(book, explanationContext);
                scoreMap.merge(book.getBookId(), boosted, Double::sum);
                bookMap.put(book.getBookId(), book);
                hits.computeIfAbsent(book.getBookId(), k -> new HashSet<>()).add(strategy);
            }
        }

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> {
                    Book book = bookMap.get(entry.getKey());
                    List<String> hitStrategies = new ArrayList<>(hits.getOrDefault(entry.getKey(), Collections.emptySet()));
                    Collections.sort(hitStrategies);
                    AgentRecommendExplanation explanation = buildExplanation(book, hitStrategies, intent, explanationContext);
                    String reason = explanation.getMainReason();
                    return new AgentRecommendItem(book, round3(entry.getValue()), reason, hitStrategies, explanation);
                })
                .collect(Collectors.toList());
    }

    private double intentBoost(String intent, Book book) {
        if (book == null) {
            return 0.0;
        }
        String meta = (safe(book.getBookClassify()) + " " + safe(book.getBookDescription())).toLowerCase(Locale.ROOT);

        if ("learning".equals(intent) || "professional".equals(intent)) {
            if (meta.contains("history") || meta.contains("knowledge") || meta.contains("哲学") || meta.contains("method")) {
                return 0.05;
            }
            return 0.01;
        }
        if ("leisure".equals(intent)) {
            if (meta.contains("sci") || meta.contains("fiction") || meta.contains("classic") || meta.contains("小说")) {
                return 0.05;
            }
            return 0.01;
        }
        if (meta.contains("life") || meta.contains("history") || meta.contains("classic")) {
            return 0.03;
        }
        return 0.01;
    }

    private double featureAnalyticsBoost(Book book, ExplanationContext context) {
        if (book == null || book.getBookId() == null || context == null || context.featureAnalyticsByBook == null) {
            return 0.0;
        }
        FeatureAnalyticsScore score = context.featureAnalyticsByBook.get(book.getBookId());
        if (score == null) {
            return 0.0;
        }
        return round3(FEATURE_BOOST_MAX * score.normalizedScore);
    }

    private List<AgentRecommendItem> fallbackByPopularity(int topN,
                                                          String intent,
                                                          ExplanationContext explanationContext) {
        List<Book> allBooks = bookMapper.selectList(null);
        if (allBooks == null || allBooks.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, Integer> heatMap = new HashMap<>();
        List<BookLike> likes = bookLikeMapper.selectList(new LambdaQueryWrapper<BookLike>().ge(BookLike::getLikeLevel, 1));
        for (BookLike like : likes) {
            heatMap.merge(like.getBookId(), 1, Integer::sum);
        }
        return allBooks.stream()
                .sorted((a, b) -> {
                    double aScore = heatMap.getOrDefault(a.getBookId(), 0) + intentBoost(intent, a) * 100;
                    double bScore = heatMap.getOrDefault(b.getBookId(), 0) + intentBoost(intent, b) * 100;
                    return Double.compare(bScore, aScore);
                })
                .limit(topN)
                .map(book -> new AgentRecommendItem(
                        book,
                        round3(0.2 + intentBoost(intent, book)),
                        "冷启动热度兜底，匹配当前阅读目标",
                        Collections.singletonList("fallback_popularity"),
                        buildExplanation(book, Collections.singletonList("fallback_popularity"), intent, explanationContext)
                ))
                .collect(Collectors.toList());
    }

    private List<String> buildDecisionPath(String intent,
                                           Map<String, Integer> profile,
                                           Map<String, Double> weights,
                                           boolean hasResult,
                                           boolean featureBoostEnabled) {
        List<String> path = new ArrayList<>();
        path.add("intent_recognition=" + intent);
        path.add("profile(scoreCount=" + profile.getOrDefault("scoreCount", 0)
                + ", likeCount=" + profile.getOrDefault("likeCount", 0)
                + ", classifyDiversity=" + profile.getOrDefault("classifyDiversity", 0) + ")");
        path.add("strategy=hybrid_strategy");
        path.add("weights=" + weights);
        path.add("feature_analytics_boost=" + (featureBoostEnabled ? "on(window=30d,max=0.08)" : "off"));
        path.add("result=" + (hasResult ? "hit" : "empty_fallback"));
        return path;
    }

    private String buildStrategyDescription(String intent,
                                            Map<String, Integer> profile,
                                            Map<String, Double> weights) {
        return "Rule-based intent + rank fusion; intent=" + intent
                + ", scoreCount=" + profile.getOrDefault("scoreCount", 0)
                + ", likeCount=" + profile.getOrDefault("likeCount", 0)
                + ", weights=" + weights;
    }

    private String saveTrace(Integer userId,
                             String demand,
                             String intent,
                             Map<String, Double> weights,
                             List<String> decisionPath,
                             List<AgentRecommendItem> recommendItems) {
        AgentDecisionTrace trace = new AgentDecisionTrace();
        trace.setUserId(userId);
        trace.setDemand(demand);
        trace.setIntent(intent);
        trace.setStrategyUsed("hybrid_strategy");
        trace.setWeights(weights);
        trace.setDecisionPath(decisionPath);
        trace.setCreatedAt(new Date());

        List<Map<String, Object>> snapshot = recommendItems.stream().map(item -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("bookId", item.getBook() == null ? null : item.getBook().getBookId());
            row.put("bookName", item.getBook() == null ? null : item.getBook().getBookName());
            row.put("score", item.getScore());
            row.put("reason", item.getReason());
            row.put("hitStrategies", item.getHitStrategies());
            row.put("explanation", item.getExplanation());
            return row;
        }).collect(Collectors.toList());
        trace.setRecommendSnapshot(snapshot);

        AgentDecisionTrace saved = mongoTemplate.save(trace);
        return saved.getId();
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }

    private double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private double safeRate(int numerator, int denominator) {
        if (denominator <= 0 || numerator <= 0) {
            return 0D;
        }
        return (double) numerator / denominator;
    }

    private ExplanationContext buildExplanationContext(Integer userId) {
        ExplanationContext context = new ExplanationContext();
        if (userId == null) {
            context.likeClassShare = Collections.emptyMap();
            context.highScoreClassShare = Collections.emptyMap();
            context.popularityByBook = Collections.emptyMap();
            context.maxPopularity = 1;
            context.featureAnalyticsByBook = Collections.emptyMap();
            return context;
        }

        List<BookLike> userLikes = bookLikeMapper.selectList(new LambdaQueryWrapper<BookLike>()
                .eq(BookLike::getUserId, userId)
                .ge(BookLike::getLikeLevel, 1));
        List<BookScore> userScores = bookScoreMapper.selectList(new LambdaQueryWrapper<BookScore>()
                .eq(BookScore::getUserId, userId));

        Set<Integer> likedBookIds = userLikes.stream().map(BookLike::getBookId).collect(Collectors.toSet());
        Set<Integer> highScoreBookIds = userScores.stream()
                .filter(s -> s.getScore() != null && s.getScore() >= 4)
                .map(BookScore::getBookId)
                .collect(Collectors.toSet());

        Set<Integer> unionBookIds = new HashSet<>(likedBookIds);
        unionBookIds.addAll(highScoreBookIds);
        Map<Integer, Book> books = loadBookMapByIds(unionBookIds);

        Map<String, Integer> likeClassCounter = new HashMap<>();
        for (Integer bookId : likedBookIds) {
            for (String classify : splitClassify(books.get(bookId) == null ? null : books.get(bookId).getBookClassify())) {
                likeClassCounter.merge(classify, 1, Integer::sum);
            }
        }
        context.likeClassShare = normalizeDistribution(likeClassCounter);

        Map<String, Integer> highScoreClassCounter = new HashMap<>();
        for (Integer bookId : highScoreBookIds) {
            for (String classify : splitClassify(books.get(bookId) == null ? null : books.get(bookId).getBookClassify())) {
                highScoreClassCounter.merge(classify, 1, Integer::sum);
            }
        }
        context.highScoreClassShare = normalizeDistribution(highScoreClassCounter);

        Map<Integer, Integer> popularity = new HashMap<>();
        List<BookLike> globalLikes = bookLikeMapper.selectList(new LambdaQueryWrapper<BookLike>().ge(BookLike::getLikeLevel, 1));
        int max = 1;
        for (BookLike like : globalLikes) {
            int value = popularity.merge(like.getBookId(), 1, Integer::sum);
            if (value > max) {
                max = value;
            }
        }
        context.popularityByBook = popularity;
        context.maxPopularity = max;
        context.featureAnalyticsByBook = buildFeatureAnalyticsScores();
        return context;
    }

    private AgentRecommendExplanation buildExplanation(Book book,
                                                       List<String> hitStrategies,
                                                       String intent,
                                                       ExplanationContext context) {
        AgentRecommendExplanation explanation = new AgentRecommendExplanation();
        List<String> classifies = splitClassify(book == null ? null : book.getBookClassify());
        String classifyText = String.join(" / ", classifies);

        double historyPref = avgClassShare(context.highScoreClassShare, classifies);
        double likeShare = avgClassShare(context.likeClassShare, classifies);
        double similarUser = collaborativeSignal(hitStrategies);
        double contentSim = contentSimilaritySignal(intent, classifies);
        double popularity = popularitySignal(book, context);
        double featureAnalytics = featureAnalyticsSignal(book, context);

        Map<String, Double> raw = new LinkedHashMap<>();
        raw.put("historyPref", historyPref);
        raw.put("likeCategoryShare", likeShare);
        raw.put("similarUserBehavior", similarUser);
        raw.put("contentSimilarity", contentSim);
        raw.put("popularity", popularity);
        raw.put("featureAnalytics", featureAnalytics);
        Map<String, Double> normalized = normalizeScores(raw);
        explanation.setWeightContributions(normalized);

        List<String> keyFeatures = new ArrayList<>();
        keyFeatures.add("阅读目标：" + intent);
        keyFeatures.add("图书分类：" + classifyText);
        keyFeatures.add("命中策略：" + (hitStrategies == null || hitStrategies.isEmpty() ? "fallback_popularity" : String.join("+", hitStrategies)));
        if (normalized.getOrDefault("historyPref", 0.0) >= 0.2) {
            keyFeatures.add("与你高分历史偏好高度一致");
        }
        if (normalized.getOrDefault("similarUserBehavior", 0.0) >= 0.2) {
            keyFeatures.add("相似用户行为支持该推荐");
        }
        if (normalized.getOrDefault("featureAnalytics", 0.0) >= 0.15) {
            FeatureAnalyticsScore score = (book == null || book.getBookId() == null || context.featureAnalyticsByBook == null)
                    ? null
                    : context.featureAnalyticsByBook.get(book.getBookId());
            if (score != null) {
                keyFeatures.add("特色页分析加权：完成率 " + (int) Math.round(score.completeRate * 100)
                        + "%，高分率 " + (int) Math.round(score.like2Rate * 100)
                        + "%，接受率 " + (int) Math.round(score.acceptRate * 100) + "%");
            } else {
                keyFeatures.add("特色页分析高表现信号支持该推荐");
            }
        }
        explanation.setKeyFeatures(keyFeatures);

        String mainReason = "该书与当前目标 " + intent + " 匹配，综合了历史偏好、相似用户行为、内容相关性与热度信号。";
        if (normalized.getOrDefault("featureAnalytics", 0.0) >= 0.15) {
            mainReason = mainReason + " 同时参考了特色页分析中的高表现数据，提升了推荐权重。";
        }
        explanation.setMainReason(mainReason);
        return explanation;
    }
    private double collaborativeSignal(List<String> hitStrategies) {
        if (hitStrategies == null || hitStrategies.isEmpty()) {
            return 0.1;
        }
        int hit = 0;
        for (String strategy : hitStrategies) {
            if ("user_cf".equals(strategy) || "item_cf".equals(strategy) || "association_rule".equals(strategy)) {
                hit++;
            }
        }
        return Math.min(1.0, hit / 2.0);
    }

    private double contentSimilaritySignal(String intent, List<String> classifies) {
        if (classifies == null || classifies.isEmpty()) {
            return 0.1;
        }
        String joined = String.join(" ", classifies).toLowerCase(Locale.ROOT);
        if ("learning".equals(intent) || "professional".equals(intent)) {
            if (joined.contains("knowledge") || joined.contains("history") || joined.contains("philosophy")) {
                return 0.9;
            }
            return 0.4;
        }
        if ("leisure".equals(intent)) {
            if (joined.contains("classic") || joined.contains("fiction") || joined.contains("sci")) {
                return 0.9;
            }
            return 0.4;
        }
        return 0.6;
    }

    private double popularitySignal(Book book, ExplanationContext context) {
        if (book == null || book.getBookId() == null || context.maxPopularity <= 0) {
            return 0.0;
        }
        int heat = context.popularityByBook.getOrDefault(book.getBookId(), 0);
        return round3((double) heat / context.maxPopularity);
    }

    private double featureAnalyticsSignal(Book book, ExplanationContext context) {
        if (book == null || book.getBookId() == null || context == null || context.featureAnalyticsByBook == null) {
            return 0.0;
        }
        FeatureAnalyticsScore score = context.featureAnalyticsByBook.get(book.getBookId());
        return score == null ? 0.0 : score.normalizedScore;
    }

    private Map<Integer, FeatureAnalyticsScore> buildFeatureAnalyticsScores() {
        Date from = Date.from(LocalDate.now().minusDays(FEATURE_ANALYTICS_LOOKBACK_DAYS - 1L)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        Query query = new Query(Criteria.where("createdAt").gte(from));
        List<FeaturePageEvent> events = mongoTemplate.find(query, FeaturePageEvent.class);
        if (events == null || events.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, List<FeaturePageEvent>> byBook = events.stream()
                .filter(e -> e.getBookId() != null && e.getBookId() > 0)
                .collect(Collectors.groupingBy(FeaturePageEvent::getBookId));
        if (byBook.isEmpty()) {
            return Collections.emptyMap();
        }

        int maxEnterUv = 1;
        Map<Integer, Integer> enterUvByBook = new HashMap<>();
        for (Map.Entry<Integer, List<FeaturePageEvent>> entry : byBook.entrySet()) {
            int enterUv = (int) entry.getValue().stream()
                    .filter(e -> "enter".equals(e.getEventType()))
                    .map(FeaturePageEvent::getUserId)
                    .filter(this::validUserId)
                    .distinct()
                    .count();
            enterUvByBook.put(entry.getKey(), enterUv);
            if (enterUv > maxEnterUv) {
                maxEnterUv = enterUv;
            }
        }

        Map<Integer, FeatureAnalyticsScore> rawScores = new HashMap<>();
        double maxRaw = 0.0;
        for (Map.Entry<Integer, List<FeaturePageEvent>> entry : byBook.entrySet()) {
            Integer bookId = entry.getKey();
            List<FeaturePageEvent> bucket = entry.getValue();

            long enterSessions = bucket.stream()
                    .filter(e -> "enter".equals(e.getEventType()) && StringUtils.hasText(e.getSessionId()))
                    .map(FeaturePageEvent::getSessionId)
                    .distinct()
                    .count();
            long completeSessions = bucket.stream()
                    .filter(e -> ("rate".equals(e.getEventType()) || "checkpoint".equals(e.getEventType())) && StringUtils.hasText(e.getSessionId()))
                    .map(FeaturePageEvent::getSessionId)
                    .distinct()
                    .count();
            List<FeaturePageEvent> rateEvents = bucket.stream()
                    .filter(e -> "rate".equals(e.getEventType()))
                    .collect(Collectors.toList());
            long like2Count = rateEvents.stream()
                    .filter(e -> Integer.valueOf(2).equals(e.getLikeLevel()))
                    .count();
            long acceptUv = bucket.stream()
                    .filter(e -> "recommend_accept".equals(e.getEventType()))
                    .map(FeaturePageEvent::getUserId)
                    .filter(this::validUserId)
                    .distinct()
                    .count();

            double completeRate = safeRate((int) completeSessions, (int) enterSessions);
            double like2Rate = safeRate((int) like2Count, rateEvents.size());
            double acceptRate = safeRate((int) acceptUv, enterUvByBook.getOrDefault(bookId, 0));
            double enterUvNorm = (double) enterUvByBook.getOrDefault(bookId, 0) / maxEnterUv;
            double raw = 0.40 * acceptRate + 0.30 * like2Rate + 0.20 * completeRate + 0.10 * enterUvNorm;

            FeatureAnalyticsScore score = new FeatureAnalyticsScore();
            score.completeRate = round3(completeRate);
            score.like2Rate = round3(like2Rate);
            score.acceptRate = round3(acceptRate);
            score.enterUv = enterUvByBook.getOrDefault(bookId, 0);
            score.rawScore = round3(raw);
            rawScores.put(bookId, score);
            if (raw > maxRaw) {
                maxRaw = raw;
            }
        }

        if (maxRaw <= 0.0) {
            return Collections.emptyMap();
        }
        for (FeatureAnalyticsScore score : rawScores.values()) {
            score.normalizedScore = round3(score.rawScore / maxRaw);
        }
        return rawScores;
    }

    private boolean validUserId(Integer userId) {
        return userId != null && userId > 0;
    }

    private Map<String, Double> normalizeDistribution(Map<String, Integer> counter) {
        if (counter == null || counter.isEmpty()) {
            return Collections.emptyMap();
        }
        double total = counter.values().stream().mapToInt(Integer::intValue).sum();
        if (total <= 0) {
            return Collections.emptyMap();
        }
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            result.put(entry.getKey(), round3(entry.getValue() / total));
        }
        return result;
    }

    private Map<String, Double> normalizeScores(Map<String, Double> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }
        double sum = values.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum <= 0) {
            return values;
        }
        Map<String, Double> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            normalized.put(entry.getKey(), round3(entry.getValue() / sum));
        }
        return normalized;
    }

    private Map<Integer, Book> loadBookMapByIds(Set<Integer> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>().in(Book::getBookId, bookIds));
        return books.stream().collect(Collectors.toMap(Book::getBookId, b -> b, (a, b) -> a));
    }

    private String normalizeClassify(String classify) {
        return StringUtils.hasText(classify) ? classify : "unknown";
    }

    private List<String> splitClassify(String classify) {
        String normalized = normalizeClassify(classify);
        if ("unknown".equals(normalized)) {
            return Collections.singletonList("unknown");
        }
        String[] tokens = normalized.split("[,，/|;；、]");
        List<String> result = new ArrayList<>();
        for (String token : tokens) {
            String trimmed = token == null ? "" : token.trim();
            if (StringUtils.hasText(trimmed)) {
                result.add(trimmed);
            }
        }
        if (result.isEmpty()) {
            result.add(normalized);
        }
        return result;
    }

    private double avgClassShare(Map<String, Double> shareMap, List<String> classifies) {
        if (shareMap == null || shareMap.isEmpty() || classifies == null || classifies.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (String classify : classifies) {
            sum += shareMap.getOrDefault(classify, 0.0);
        }
        return round3(sum / classifies.size());
    }

    private LocalDate parseDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static class ExplanationContext {
        private Map<String, Double> likeClassShare;
        private Map<String, Double> highScoreClassShare;
        private Map<Integer, Integer> popularityByBook;
        private int maxPopularity;
        private Map<Integer, FeatureAnalyticsScore> featureAnalyticsByBook;
    }

    private static class FeatureAnalyticsScore {
        private int enterUv;
        private double completeRate;
        private double like2Rate;
        private double acceptRate;
        private double rawScore;
        private double normalizedScore;
    }
}

