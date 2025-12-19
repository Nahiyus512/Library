package com.wkc.library.common;

import com.wkc.library.entity.BookScore;
import com.wkc.library.mapper.BookScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BookRecommend {

    @Autowired
    private BookScoreMapper bookScoreMapper;

    // 加载用户评分数据的方法
    public Map<Integer, Map<Integer, Double>> loadRatings() {
        Map<Integer, Map<Integer, Double>> userRatings = new HashMap<>();
        List<BookScore> bookScores = bookScoreMapper.selectList(null);
        for (BookScore score : bookScores) {
            userRatings.computeIfAbsent(score.getUserId(), k -> new HashMap<>())
                       .put(score.getBookId(), score.getScore() != null ? score.getScore().doubleValue() : 0.0);
        }
        return userRatings;
    }

    // 计算余弦相似度
    public double cosineSimilarity(Map<Integer, Double> ratings1, Map<Integer, Double> ratings2) {
        Set<Integer> commonBooks = new HashSet<>(ratings1.keySet());
        commonBooks.retainAll(ratings2.keySet());

        if (commonBooks.isEmpty()) return 0;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (Integer bookId : commonBooks) {
            double rating1 = ratings1.get(bookId);
            double rating2 = ratings2.get(bookId);
            dotProduct += rating1 * rating2;
            normA += Math.pow(rating1, 2);
            normB += Math.pow(rating2, 2);
        }
        
        if (normA == 0 || normB == 0) return 0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // 找到与目标用户最相似的三个用户
    public List<Integer> findTopThreeSimilarUsers(Integer targetUserId, Map<Integer, Map<Integer, Double>> userRatings) {

        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(
                (e1, e2) -> Double.compare(e1.getValue(), e2.getValue())
        );
        for (Map.Entry<Integer, Map<Integer, Double>> entry : userRatings.entrySet()) {
            Integer userId = entry.getKey();
            if (userId.equals(targetUserId)) continue;

            double similarity = cosineSimilarity(userRatings.get(targetUserId), entry.getValue());
            Map.Entry<Integer, Double> user = new AbstractMap.SimpleEntry<>(userId, similarity);
            pq.offer(user);
            if (pq.size() > 3) {
                pq.poll();
            }
        }

        List<Integer> topUsers = new ArrayList<>();
        while (!pq.isEmpty()) {
            topUsers.add(pq.poll().getKey());
        }
        return topUsers;
    }

    // 基于相似用户推荐图书
    public List<Map.Entry<Integer, Integer>> recommendBooksBasedOnSimilarUsers(
            Map<Integer, Map<Integer, Double>> userRatings,
            List<Integer> similarUsers, Integer targetUserId) {
        Map<Integer, Integer> bookVotes = new HashMap<>();

        for (Integer userId : similarUsers) {
            Map<Integer, Double> userBooks = userRatings.getOrDefault(userId, Collections.emptyMap());
            for (Map.Entry<Integer, Double> bookRating : userBooks.entrySet()) {
                if (!userRatings.get(targetUserId).containsKey(bookRating.getKey())) {
                    bookVotes.merge(bookRating.getKey(), 1, Integer::sum);
                }
            }
        }

        List<Map.Entry<Integer, Integer>> sortedBooks = new ArrayList<>(bookVotes.entrySet());
        sortedBooks.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());
        List<Map.Entry<Integer, Integer>> recommendBook = new ArrayList<>();
        // System.out.println("为用户 " + targetUserName + " 推荐的图书：");
        for (int i = 0; i < Math.min(5, sortedBooks.size()); i++) {
            recommendBook.add(sortedBooks.get(i));
            // System.out.println("图书ID: " + sortedBooks.get(i).getKey() + ", 被推荐次数: " + sortedBooks.get(i).getValue());
        }

        return recommendBook;
    }
}
