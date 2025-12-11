package com.wkc.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookScore;
import com.wkc.library.entity.R;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.mapper.BookScoreMapper;
import com.wkc.library.service.impl.BookScoreServiceImpl;
import com.wkc.library.service.impl.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;


/**
 * @author xiaoniao
 * @date 2023/4/10 16:00
 */
@RequestMapping("/bookScore")
@RestController
@CrossOrigin
public class BookScoreController {
    @Autowired
    BookScoreMapper bookScoreMapper;

    @Autowired
    BookScoreServiceImpl bookService;

    @Autowired
    BookMapper bookMapper;

    @PostMapping("/score")
    public R<String> score(HttpServletRequest request){
        String userId = request.getParameter("userId");
        String bookId = request.getParameter("bookId");
        String score = request.getParameter("score");
        LocalDateTime time = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = time.atZone(zoneId);
        Date date = Date.from(zonedDateTime.toInstant());
        BookScore bookScore = new BookScore(null,userId,bookId,Integer.parseInt(score),date.toString());
        Integer integer = bookScoreMapper.insert(bookScore);
        if(integer == 1){
            return R.success("评分成功");
        }
        return R.error("评分失败");
    }

    @GetMapping("/all")
    public R<List<BookScore>> getAll(){
        List<BookScore> all = bookScoreMapper.getAll();
        return R.success(all);
    }

    /**
     * 协同过滤算法推荐图书
     * @param userId
     * @return
     */
    @GetMapping("/recommend")
    public R<List<Book>> recommendBook(@RequestParam String userId) {
        LambdaQueryWrapper<BookScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookScore::getUserId,userId);
        List<BookScore> bookScoreList = bookService.list(queryWrapper);

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderBy(true,true,Book::getBookPrice);
        wrapper.last("LIMIT 5");
        //如果用户从未评价过书本推荐价格最高的5本书，评价过书则按协同过滤算法进行推荐
        if(bookScoreList.isEmpty()) {
            List<Book> books = bookMapper.selectList(wrapper);
            return R.success(books);
        }else {
            List<Book> list = bookService.recommend(userId);
            if(list.isEmpty()){
                //如果所有书籍都评价过，则推荐5本价格最高的书
                List<Book> books = bookMapper.selectList(wrapper);
                return R.success(books);
            }else {
                return R.success(list);
            }
        }
    }


    /**
     * 用户评分
     * @param bookScore
     * @return
     */
    @PutMapping("/updateScore")
    public R<String> updateScore(@RequestBody BookScore bookScore) {

        boolean isTrue = bookService.updateScore(bookScore);
        if(isTrue){
            return R.success("评分成功");
        }
        return R.error("评分失败");

    }


    @GetMapping("/findScore")
    public R<Integer> findScore(@RequestParam("userId") String userId,@RequestParam("bookId") String bookId) {
        System.out.println("==>s");
        LambdaQueryWrapper<BookScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookScore::getUserId,userId);
        wrapper.eq(BookScore::getBookId,bookId);
        BookScore one = bookService.getOne(wrapper);
        if(one == null)
            return R.success(0);
        return R.success(one.getScore());

    }

}
