package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.common.BookRecommend;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookScore;
import com.wkc.library.entity.User;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.mapper.BookScoreMapper;
import com.wkc.library.mapper.UserMapper;
import com.wkc.library.service.BookScoreService;
import com.wkc.library.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author xiaoniao
 * @date 2023/4/11 13:15
 */
@Service
public class BookScoreServiceImpl extends ServiceImpl<BookScoreMapper, BookScore> implements BookScoreService {


    @Autowired
    BookMapper bookMapper;

    @Autowired
    BookScoreMapper bookScoreMapper;
    
    @Autowired
    UserMapper userMapper;

    @Autowired
    BookRecommend bookRecommend;

    @Autowired
    private com.wkc.library.common.ItemCFRecommend itemCFRecommend;

    @Autowired
    private com.wkc.library.common.ContentBasedRecommend contentBasedRecommend;

    @Autowired
    private com.wkc.library.common.AssociationRuleRecommend associationRuleRecommend;

    @Autowired
    private com.wkc.library.common.LFMRecommend lfmRecommend;

    @Override
    public List<Book> recommend(Integer userId) {
        return recommend(userId, "user_cf");
    }

    @Override
    public List<Book> recommend(Integer userId, String strategy) {
        List<Book> bookList = new ArrayList<>();
        
        // 获取用户名
        // User user = userMapper.selectById(userId);
        // if (user == null) return bookList;
        // String userName = user.getName();

        if ("item_cf".equals(strategy)) {
            // 基于物品的协同过滤
            List<Integer> bookIds = itemCFRecommend.recommend(userId, 5);
            return getBooksByIds(bookIds);
        } else if ("content_based".equals(strategy)) {
            // 基于内容的推荐
            return contentBasedRecommend.recommend(userId, 5);
        } else if ("association_rule".equals(strategy)) {
            // 关联规则推荐
            List<Integer> bookIds = associationRuleRecommend.recommend(userId, 5);
            return getBooksByIds(bookIds);
        } else if ("lfm".equals(strategy)) {
            // 隐语义模型
            List<Integer> bookIds = lfmRecommend.recommend(userId, 5);
            return getBooksByIds(bookIds);
        } else {
            // 默认为基于用户的协同过滤 (user_cf)
            
            // 加载用户评分数据
            Map<Integer, Map<Integer, Double>> userRatings = bookRecommend.loadRatings();
            
            // 如果当前用户没有评分数据，直接返回空
            if (!userRatings.containsKey(userId)) {
                return bookList;
            }
    
            // 找到与目标用户最相似的三个用户
            List<Integer> similarUsers = bookRecommend.findTopThreeSimilarUsers(userId, userRatings);
            // System.out.println("与用户 " + userId + " 最相似的三个用户为：" + similarUsers);
    
            // 基于相似用户推荐图书
            List<Map.Entry<Integer, Integer>> recommendedBooks = bookRecommend.recommendBooksBasedOnSimilarUsers(userRatings, similarUsers, userId);
    
            // 根据推荐的图书 ID 查询图书信息
            for (Map.Entry<Integer, Integer> entry : recommendedBooks) {
                Integer bookId = entry.getKey(); // 获取推荐的图书 ID
                LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Book::getBookId, bookId);
                Book book = bookMapper.selectOne(queryWrapper);
                if (book != null) {
                    bookList.add(book);
                }
            }
    
            return bookList;
        }
    }

    private List<Book> getBooksByIds(List<Integer> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Book::getBookId, bookIds);
        return bookMapper.selectList(queryWrapper);
    }

    @Override
    public boolean updateScore(BookScore bookScore) {
        LambdaQueryWrapper<BookScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookScore::getUserId, bookScore.getUserId())
               .eq(BookScore::getBookId, bookScore.getBookId());
        
        BookScore existingScore = bookScoreMapper.selectOne(wrapper);
        
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentTime = date.format(formatter);
        bookScore.setTime(currentTime);

        if (existingScore != null) {
            // Update existing record
            existingScore.setScore(bookScore.getScore());
            existingScore.setTime(currentTime);
            int update = bookScoreMapper.updateById(existingScore);
            return update > 0;
        } else {
            // Insert new record
            int insert = bookScoreMapper.insetScore(bookScore);
            return insert > 0;
        }
    }
}
