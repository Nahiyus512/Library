package com.wkc.library.common;

import java.sql.*;
import java.util.*;

public class BookRecommend {

    public static final String DB_URL = "jdbc:mysql://localhost:3306/tushu";
    public static final String USER = "root";
    public static final String PASS = "1234";

    // 加载用户评分数据的方法
    public static Map<String, Map<String, Double>> loadRatingsFromDB(Connection conn) throws SQLException {
        Map<String, Map<String, Double>> userRatings = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, book_id, score FROM book_score")) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String bookId = rs.getString("book_id");
                double rating = rs.getDouble("score");

                userRatings.computeIfAbsent(userId, k -> new HashMap<>()).put(bookId, rating);
            }
        }
        return userRatings;
    }

    // 计算余弦相似度
    public static double cosineSimilarity(Map<String, Double> ratings1, Map<String, Double> ratings2) {
        Set<String> commonBooks = new HashSet<>(ratings1.keySet());
        commonBooks.retainAll(ratings2.keySet());

        if (commonBooks.isEmpty()) return 0;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String bookId : commonBooks) {
            double rating1 = ratings1.get(bookId);
            double rating2 = ratings2.get(bookId);
            dotProduct += rating1 * rating2;
            normA += Math.pow(rating1, 2);
            normB += Math.pow(rating2, 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // 找到与目标用户最相似的三个用户
    public static List<String> findTopThreeSimilarUsers(String targetUserId, Map<String, Map<String, Double>> userRatings) {

        PriorityQueue<Map.Entry<String, Double>> pq = new PriorityQueue<>(
                (e1, e2) -> Double.compare(e1.getValue(), e2.getValue())
        );
        for (Map.Entry<String, Map<String, Double>> entry : userRatings.entrySet()) {
            String userId = entry.getKey();
            if (userId.equals(targetUserId)) continue;

            double similarity = cosineSimilarity(userRatings.get(targetUserId), entry.getValue());
            Map.Entry<String, Double> user = new AbstractMap.SimpleEntry<>(userId, similarity);
            pq.offer(user);
            if (pq.size() > 3) {
                pq.poll();
            }
        }

        List<String> topUsers = new ArrayList<>();
        while (!pq.isEmpty()) {
            topUsers.add(pq.poll().getKey());
        }
        return topUsers;
    }

    // 基于相似用户推荐图书
    public static List<Map.Entry<String, Integer>> recommendBooksBasedOnSimilarUsers(
            Map<String, Map<String, Double>> userRatings,
            List<String> similarUsers, String targetUserId) {
        Map<String, Integer> bookVotes = new HashMap<>();

        for (String userId : similarUsers) {
            Map<String, Double> userBooks = userRatings.getOrDefault(userId, Collections.emptyMap());
            for (Map.Entry<String, Double> bookRating : userBooks.entrySet()) {
                if (!userRatings.get(targetUserId).containsKey(bookRating.getKey())) {
                    bookVotes.merge(bookRating.getKey(), 1, Integer::sum);
                }
            }
        }

        List<Map.Entry<String, Integer>> sortedBooks = new ArrayList<>(bookVotes.entrySet());
        sortedBooks.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        List<Map.Entry<String, Integer>> recommendBook = new ArrayList<>();
        System.out.println("为用户 " + targetUserId + " 推荐的图书：");
        for (int i = 0; i < Math.min(5, sortedBooks.size()); i++) {
            recommendBook.add(sortedBooks.get(i));
            System.out.println("图书ID: " + sortedBooks.get(i).getKey() + ", 被推荐次数: " + sortedBooks.get(i).getValue());
        }

        return recommendBook;
    }
}