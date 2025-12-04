package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.common.BookRecommend;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookScore;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.mapper.BookScoreMapper;
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

    @Override
    public List<Book> recommend(String userId) { // 将 userId 改为 String 类型
        List<Book> bookList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(BookRecommend.DB_URL, BookRecommend.USER, BookRecommend.PASS)) {
            // 加载用户评分数据
            Map<String, Map<String, Double>> userRatings = BookRecommend.loadRatingsFromDB(conn);

            // 找到与目标用户最相似的三个用户
            List<String> similarUsers = BookRecommend.findTopThreeSimilarUsers(userId, userRatings);
            System.out.println("与用户 " + userId + " 最相似的三个用户为：" + similarUsers);

            // 基于相似用户推荐图书
            List<Map.Entry<String, Integer>> recommendedBooks = BookRecommend.recommendBooksBasedOnSimilarUsers(userRatings, similarUsers, userId);

            // 根据推荐的图书 ID 查询图书信息
            for (Map.Entry<String, Integer> entry : recommendedBooks) {
                String bookId = entry.getKey(); // 获取推荐的图书 ID
                LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Book::getBookId, bookId); // 使用 String 类型的 bookId
                Book book = bookMapper.selectOne(queryWrapper);
                if (book != null) {
                    bookList.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    @Override
    public boolean updateScore(BookScore bookScore) {
        LambdaQueryWrapper<BookScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookScore::getUserId,bookScore.getUserId());
        wrapper.eq(BookScore::getBookId,bookScore.getBookId());
        BookScore selectOne = bookScoreMapper.selectOne(wrapper);
        LocalDate date = LocalDate.now(); // get the current date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bookScore.setTime(date.format(formatter));
        if(selectOne != null) {
//            if(selectOne.getScore() == bookScore.getScore()){
//                //如果评分相等，就只更新时间2
//                bookScoreMapper.update(bookScore,new LambdaQueryWrapper<BookScore>().eq(BookScore::getUserId,bookScore.getUserId()));
//                return true;
//            }
            LambdaQueryWrapper<BookScore> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BookScore::getUserId,bookScore.getUserId());
            int update = bookScoreMapper.update(bookScore, lambdaQueryWrapper);
            if(update == 0){
                return false;
            }
            return true;
        }else {
            bookScore.setId(UUIDUtil.generateUUID());
            Integer integer = bookScoreMapper.insetScore(bookScore);
            if(integer == 0){
                return false;
            }
            return true;
        }
    }
}
