package com.wkc.library.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookLike;
import com.wkc.library.mapper.BookLikeMapper;
import com.wkc.library.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于内容的推荐算法 (Content-Based Filtering)
 * 核心思想：分析用户历史上喜欢的物品的特征（如作者、分类），构建用户画像，
 * 然后推荐具有相似特征的物品。
 */
@Component
public class ContentBasedRecommend {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookLikeMapper bookLikeMapper;

    /**
     * 为指定用户推荐图书
     */
    public List<Book> recommend(Integer userId, int topN) {
        // 1. 获取用户喜爱历史
        LambdaQueryWrapper<BookLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(BookLike::getUserId, userId)
                   .ge(BookLike::getLikeLevel, 1); // 只统计还行和想看
        List<BookLike> userHistory = bookLikeMapper.selectList(likeWrapper);

        if (userHistory.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 获取所有图书信息（用于提取特征）
        List<Book> allBooks = bookMapper.selectList(null);
        Map<Integer, Book> bookMap = allBooks.stream().collect(Collectors.toMap(Book::getBookId, b -> b));

        // 3. 构建用户画像 (特征偏好向量)
        // 统计用户对不同分类、作者的加权得分
        Map<String, Double> categoryPreference = new HashMap<>();
        Map<String, Double> authorPreference = new HashMap<>();
        double totalWeight = 0;

        Set<Integer> watchedBookIds = new HashSet<>();

        for (BookLike like : userHistory) {
            Book book = bookMap.get(like.getBookId());
            if (book == null) continue;

            watchedBookIds.add(book.getBookId());
            double rating = like.getLikeLevel() != null ? like.getLikeLevel().doubleValue() : 0.0;
            // 归一化评分权重，例如：(评分 - 3.0) * 权重，或者直接用评分作为权重
            double weight = rating; 

            if (book.getBookClassify() != null) {
                categoryPreference.merge(book.getBookClassify(), weight, Double::sum);
            }
            if (book.getBookAuthor() != null) {
                authorPreference.merge(book.getBookAuthor(), weight, Double::sum);
            }
            totalWeight += weight;
        }

        // 4. 计算候选图书的匹配度
        Map<Book, Double> bookScores = new HashMap<>();

        for (Book book : allBooks) {
            if (watchedBookIds.contains(book.getBookId())) {
                continue; // 跳过已读
            }

            double score = 0.0;
            
            // 分类匹配得分
            if (book.getBookClassify() != null) {
                score += categoryPreference.getOrDefault(book.getBookClassify(), 0.0);
            }
            // 作者匹配得分
            if (book.getBookAuthor() != null) {
                score += authorPreference.getOrDefault(book.getBookAuthor(), 0.0);
            }

            if (score > 0) {
                bookScores.put(book, score);
            }
        }

        // 5. 排序返回
        return bookScores.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
