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
import java.util.*;
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
    public R<List<Advice>> getAdvice(String userName, Integer type) {
        LambdaQueryWrapper<Advice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Advice::getUserName, userName);
        if (type != null) {
            if (type == 0) {
                // System suggestion: no bookId
                wrapper.isNull(Advice::getBookId);
            } else if (type == 1) {
                // Book review: has bookId
                wrapper.isNotNull(Advice::getBookId);
            }
        }
        List<Advice> userAdvice = adviceService.list(wrapper);
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

    @GetMapping("/topCommented")
    public R<List<Map<String, Object>>> getTopCommented() {
        LambdaQueryWrapper<Advice> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(Advice::getBookId);
        List<Advice> allAdvice = adviceService.list(wrapper);
        
        Map<String, Long> commentCounts = allAdvice.stream()
            .filter(a -> a.getBookId() != null && !a.getBookId().isEmpty())
            .collect(Collectors.groupingBy(Advice::getBookId, Collectors.counting()));
            
        List<Map.Entry<String, Long>> topEntries = commentCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());
            
        if (topEntries.isEmpty()) {
            return R.success(Collections.emptyList());
        }
        
        List<Integer> bookIds = topEntries.stream()
            .map(e -> Integer.parseInt(e.getKey()))
            .collect(Collectors.toList());
            
        List<Book> books = bookService.listByIds(bookIds);
        Map<Integer, Book> bookMap = books.stream().collect(Collectors.toMap(Book::getBookId, b -> b));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : topEntries) {
            try {
                Integer bid = Integer.parseInt(entry.getKey());
                Book book = bookMap.get(bid);
                if (book != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookId", book.getBookId());
                    map.put("bookName", book.getBookName());
                    map.put("count", entry.getValue());
                    result.add(map);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return R.success(result);
    }
}
