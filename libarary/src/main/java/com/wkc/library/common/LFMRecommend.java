package com.wkc.library.common;

import com.wkc.library.entity.BookScore;
import com.wkc.library.mapper.BookScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 隐语义模型 (Latent Factor Model, LFM) / 矩阵分解
 * 核心思想：将用户-物品评分矩阵分解为 用户-隐因子矩阵(P) 和 隐因子-物品矩阵(Q)。
 * 预测评分 = P[u] * Q[i]
 */
@Component
public class LFMRecommend {

    @Autowired
    private BookScoreMapper bookScoreMapper;

    // 隐特征数量
    private static final int LATENT_FACTORS = 5;
    // 学习率
    private static final double ALPHA = 0.01;
    // 正则化参数
    private static final double LAMBDA = 0.01;
    // 迭代次数
    private static final int ITERATIONS = 50;

    // 用户矩阵 P: userId -> double[] factors
    private Map<Integer, double[]> P = new HashMap<>();
    // 物品矩阵 Q: bookId -> double[] factors
    private Map<Integer, double[]> Q = new HashMap<>();

    /**
     * 训练模型 (简单实现，实际生产中应异步定时执行)
     */
    public void train() {
        List<BookScore> scores = bookScoreMapper.selectList(null);
        Set<Integer> userIds = new HashSet<>();
        Set<Integer> bookIds = new HashSet<>();

        for (BookScore score : scores) {
            userIds.add(score.getUserId());
            bookIds.add(score.getBookId());
        }

        // 初始化 P 和 Q
        Random rand = new Random();
        for (Integer userId : userIds) {
            double[] factors = new double[LATENT_FACTORS];
            for (int k = 0; k < LATENT_FACTORS; k++) factors[k] = rand.nextDouble() / Math.sqrt(LATENT_FACTORS);
            P.put(userId, factors);
        }
        for (Integer bookId : bookIds) {
            double[] factors = new double[LATENT_FACTORS];
            for (int k = 0; k < LATENT_FACTORS; k++) factors[k] = rand.nextDouble() / Math.sqrt(LATENT_FACTORS);
            Q.put(bookId, factors);
        }

        // 随机梯度下降 (SGD)
        for (int step = 0; step < ITERATIONS; step++) {
            for (BookScore score : scores) {
                int u = score.getUserId();
                int i = score.getBookId();
                double realRating = score.getScore();

                double predictedRating = predictRating(u, i);
                double error = realRating - predictedRating;

                // 更新参数
                double[] p_u = P.get(u);
                double[] q_i = Q.get(i);

                for (int k = 0; k < LATENT_FACTORS; k++) {
                    double p_uk = p_u[k];
                    double q_ik = q_i[k];

                    p_u[k] += ALPHA * (error * q_ik - LAMBDA * p_uk);
                    q_i[k] += ALPHA * (error * p_uk - LAMBDA * q_ik);
                }
            }
        }
    }

    private double predictRating(int userId, int bookId) {
        if (!P.containsKey(userId) || !Q.containsKey(bookId)) return 0.0;
        double[] p = P.get(userId);
        double[] q = Q.get(bookId);
        double sum = 0;
        for (int k = 0; k < LATENT_FACTORS; k++) {
            sum += p[k] * q[k];
        }
        return sum;
    }

    /**
     * 推荐
     */
    public List<Integer> recommend(Integer userId, int topN) {
        if (P.isEmpty()) {
            train(); // 如果模型未训练，先训练
        }

        if (!P.containsKey(userId)) return Collections.emptyList();

        Map<Integer, Double> predictions = new HashMap<>();
        
        // 对所有物品进行预测（实际应过滤掉已读物品）
        // 为了简化，这里假设 Q 中包含了所有物品
        for (Integer bookId : Q.keySet()) {
            // 过滤已读逻辑略（需要传入已读列表），此处假设全部预测
            double score = predictRating(userId, bookId);
            predictions.put(bookId, score);
        }

        return predictions.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
