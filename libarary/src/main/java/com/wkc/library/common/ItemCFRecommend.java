package com.wkc.library.common;

import com.wkc.library.entity.BookScore;
import com.wkc.library.mapper.BookScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于物品的协同过滤 (Item-Based Collaborative Filtering)
 * 核心思想：喜欢物品A的用户也倾向于喜欢物品B，则认为A和B相似。
 * 给用户推荐和他历史上喜欢的物品相似的物品。
 */
@Component
public class ItemCFRecommend {

    @Autowired
    private BookScoreMapper bookScoreMapper;

    /**
     * 为指定用户推荐图书
     * @param targetUserId 目标用户ID
     * @param topN 推荐数量
     * @return 推荐的图书ID列表
     */
    public List<Integer> recommend(Integer targetUserId, int topN) {
        List<BookScore> allScores = bookScoreMapper.selectList(null);

        // 1. 构建用户-物品倒排表：用户 -> 评分过的物品列表
        Map<Integer, List<Integer>> userToBooks = new HashMap<>();
        // 构建物品-用户表：物品 -> 评分过该物品的用户列表 (用于计算物品相似度)
        Map<Integer, List<Integer>> bookToUsers = new HashMap<>();
        // 记录用户对物品的评分
        Map<Integer, Map<Integer, Double>> userBookRatings = new HashMap<>();

        for (BookScore score : allScores) {
            userToBooks.computeIfAbsent(score.getUserId(), k -> new ArrayList<>()).add(score.getBookId());
            bookToUsers.computeIfAbsent(score.getBookId(), k -> new ArrayList<>()).add(score.getUserId());
            
            userBookRatings.computeIfAbsent(score.getUserId(), k -> new HashMap<>())
                    .put(score.getBookId(), score.getScore() != null ? score.getScore().doubleValue() : 0.0);
        }

        List<Integer> targetUserLikedBooks = userToBooks.getOrDefault(targetUserId, Collections.emptyList());
        if (targetUserLikedBooks.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 计算物品相似度矩阵 (基于余弦相似度或同现次数)
        // 这里简化为：两个物品被同一个用户同时喜欢的次数越多，相似度越高
        Map<Integer, Map<Integer, Integer>> itemSimilarity = new HashMap<>();
        
        for (Map.Entry<Integer, List<Integer>> entry : userToBooks.entrySet()) {
            List<Integer> books = entry.getValue();
            for (int i = 0; i < books.size(); i++) {
                for (int j = i + 1; j < books.size(); j++) {
                    int bookA = books.get(i);
                    int bookB = books.get(j);
                    
                    itemSimilarity.computeIfAbsent(bookA, k -> new HashMap<>()).merge(bookB, 1, Integer::sum);
                    itemSimilarity.computeIfAbsent(bookB, k -> new HashMap<>()).merge(bookA, 1, Integer::sum);
                }
            }
        }

        // 3. 计算推荐分数
        // 遍历用户历史喜欢的物品，找到相似物品进行加权
        Map<Integer, Double> recommendScores = new HashMap<>();

        for (Integer likedBookId : targetUserLikedBooks) {
            double rating = userBookRatings.get(targetUserId).get(likedBookId);
            // 找到与 likedBookId 相似的物品
            Map<Integer, Integer> similarBooks = itemSimilarity.getOrDefault(likedBookId, Collections.emptyMap());

            for (Map.Entry<Integer, Integer> entry : similarBooks.entrySet()) {
                Integer candidateBookId = entry.getKey();
                Integer similarity = entry.getValue();

                // 过滤掉用户已经看过的书
                if (targetUserLikedBooks.contains(candidateBookId)) {
                    continue;
                }

                // 推荐分数 = 用户对历史物品的评分 * 物品相似度
                double score = rating * similarity;
                recommendScores.merge(candidateBookId, score, Double::sum);
            }
        }

        // 4. 排序并返回 TopN
        return recommendScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
