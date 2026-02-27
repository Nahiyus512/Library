package com.wkc.library.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.controller.vo.agent.AgentRecommendItem;
import com.wkc.library.controller.vo.agent.AgentRecommendResponse;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookLike;
import com.wkc.library.entity.BookScore;
import com.wkc.library.entity.agent.AgentDecisionTrace;
import com.wkc.library.mapper.BookLikeMapper;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.mapper.BookScoreMapper;
import com.wkc.library.service.BookScoreService;
import com.wkc.library.service.agent.AgentRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgentRecommendServiceImpl implements AgentRecommendService {

    private static final List<String> STRATEGIES = Arrays.asList(
            "user_cf", "item_cf", "content_based", "lfm", "association_rule"
    );

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

        List<AgentRecommendItem> merged = rankFusion(strategyBooks, weights, intent, limit);
        if (merged.isEmpty()) {
            merged = fallbackByPopularity(limit, intent);
        }

        List<String> decisionPath = buildDecisionPath(intent, profile, weights, !merged.isEmpty());
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
                                                int topN) {
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
                double boosted = weighted + intentBoost(intent, book);
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
                    String reason = "intent=" + intent + ", strategies=" + String.join("+", hitStrategies);
                    return new AgentRecommendItem(book, round3(entry.getValue()), reason, hitStrategies);
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

    private List<AgentRecommendItem> fallbackByPopularity(int topN, String intent) {
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
                        "fallback_popularity",
                        Collections.singletonList("fallback_popularity")
                ))
                .collect(Collectors.toList());
    }

    private List<String> buildDecisionPath(String intent,
                                           Map<String, Integer> profile,
                                           Map<String, Double> weights,
                                           boolean hasResult) {
        List<String> path = new ArrayList<>();
        path.add("intent_recognition=" + intent);
        path.add("profile(scoreCount=" + profile.getOrDefault("scoreCount", 0)
                + ", likeCount=" + profile.getOrDefault("likeCount", 0)
                + ", classifyDiversity=" + profile.getOrDefault("classifyDiversity", 0) + ")");
        path.add("strategy=hybrid_strategy");
        path.add("weights=" + weights);
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
}
