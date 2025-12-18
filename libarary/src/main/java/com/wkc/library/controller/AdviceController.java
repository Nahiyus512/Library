package com.wkc.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wkc.library.entity.Advice;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.R;
import com.wkc.library.service.AdviceService;
import com.wkc.library.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiaoniao
 * @date 2024/7/2 19:29
 */
@RestController
@Slf4j
@RequestMapping("/advice")
@CrossOrigin
public class AdviceController {

    @Autowired
    AdviceService adviceService;
    
    @Autowired
    BookService bookService;

    @GetMapping("/get")
    public R<List<Advice>> getAdvice(String userName) {
        List<Advice> userAdvice = adviceService.getUserAdvice(userName);
        return R.success(userAdvice);
    }

    @GetMapping("/getBookAdvice")
    public R<List<Advice>> getBookAdvice(String bookId) {
        List<Advice> bookAdvice = adviceService.getBookAdvice(bookId);
        return R.success(bookAdvice);
    }

    @PostMapping("/input")
    public R<String> inputAdvice(@RequestBody Advice advice) {
        log.info("info:{},userName:{},bookId:{}",advice.getInfo(),advice.getUserName(), advice.getBookId());
        Integer integer = adviceService.inputSuggest(advice);
        if(integer > 0) {
            return R.success("提交留言建议成功");
        }else {
            return R.error("提交留言建议失败");
        }
    }


    @GetMapping("/getAll")
    public R<List<Advice>> getAllAdvice() {
        LambdaQueryWrapper<Advice> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(Advice::getBookId);
        List<Advice> adviceList = adviceService.list(wrapper);
        return R.success(adviceList);
    }

    @GetMapping("/getAllBookReviews")
    public R<List<Advice>> getAllBookReviews() {
        LambdaQueryWrapper<Advice> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(Advice::getBookId);
        List<Advice> adviceList = adviceService.list(wrapper);
        
        if (!adviceList.isEmpty()) {
            List<String> bookIds = adviceList.stream()
                .map(Advice::getBookId)
                .distinct()
                .collect(Collectors.toList());
            
            // Convert String IDs to Integer if necessary, but assuming BookService takes serializable or we query
            // BookId in Book is Integer. Advice has String.
            List<Integer> intIds = bookIds.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
                
            List<Book> books = bookService.listByIds(intIds);
            Map<Integer, String> bookNameMap = books.stream()
                .collect(Collectors.toMap(Book::getBookId, Book::getBookName));
                
            for (Advice advice : adviceList) {
                if (advice.getBookId() != null) {
                    try {
                        Integer bid = Integer.parseInt(advice.getBookId());
                        advice.setBookName(bookNameMap.get(bid));
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }
        return R.success(adviceList);
    }

    @PostMapping("/reply")
    public R<String> replyInfo(@RequestBody Advice advice) {
        LambdaUpdateWrapper<Advice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Advice::getId,advice.getId());
        wrapper.set(Advice::getReply,advice.getReply());
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        advice.setReplyTime(localDateTime.format(formatter));
        wrapper.set(Advice::getReplyTime,advice.getReplyTime());
        boolean update = adviceService.update(wrapper);
        if(update) {
            return R.success("回复成功");
        }
        return R.error("回复失败");
    }
}
