package com.wkc.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookLike;
import com.wkc.library.entity.R;
import com.wkc.library.service.BookLikeService;
import com.wkc.library.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author TraeAI
 * @date 2025/12/18
 */
@RestController
@Slf4j
@RequestMapping("/bookLike")
@CrossOrigin
public class BookLikeController {

    @Autowired
    BookLikeService bookLikeService;

    @Autowired
    BookService bookService;

    @PutMapping("/like")
    public R<String> likeBook(@RequestBody BookLike bookLike) {
        Boolean flag = bookLikeService.likeBook(bookLike);
        if(flag){
            return R.success("操作成功");
        }
        return R.error("操作失败");
    }

    @GetMapping("/list")
    public R<List<Map<String, Object>>> getBookshelf(@RequestParam Integer userId) {
        LambdaQueryWrapper<BookLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(BookLike::getUserId, userId);
        List<BookLike> likes = bookLikeService.list(likeWrapper);

        if (likes.isEmpty()) {
            return R.success(Collections.emptyList());
        }

        Set<Integer> bookIds = likes.stream().map(BookLike::getBookId).collect(Collectors.toSet());
        List<Book> books = bookService.listByIds(bookIds);
        Map<Integer, Book> bookMap = books.stream().collect(Collectors.toMap(Book::getBookId, b -> b));

        List<Map<String, Object>> result = new ArrayList<>();
        for (BookLike like : likes) {
            Book book = bookMap.get(like.getBookId());
            if (book != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", like.getId());
                map.put("bookId", book.getBookId());
                map.put("bookName", book.getBookName());
                map.put("bookImge", book.getBookImge());
                map.put("bookAuthor", book.getBookAuthor());
                map.put("bookPublic", book.getBookPublic());
                map.put("bookClassify", book.getBookClassify());
                map.put("bookNum", book.getBookNum());
                map.put("likeLevel", like.getLikeLevel());
                result.add(map);
            }
        }
        return R.success(result);
    }

    @GetMapping("/getAll")
    public R<List<BookLike>> getAllLikes() {
        List<BookLike> list = bookLikeService.list();
        return R.success(list);
    }

    @DeleteMapping("/delete")
    public R<String> deleteLike(@RequestParam Integer id) {
        boolean b = bookLikeService.removeById(id);
        return b ? R.success("删除成功") : R.error("删除失败");
    }

    @GetMapping("/status")
    public R<Integer> getLikeStatus(@RequestParam Integer userId, @RequestParam Integer bookId) {
        LambdaQueryWrapper<BookLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookLike::getUserId, userId)
               .eq(BookLike::getBookId, bookId);
        BookLike one = bookLikeService.getOne(wrapper);
        if (one != null) {
            return R.success(one.getLikeLevel());
        }
        return R.success(-1); // -1 表示未操作过
    }

    @GetMapping("/top")
    public R<List<Map<String, Object>>> getTopLikes() {
        List<BookLike> allLikes = bookLikeService.list();
        Map<Integer, Long> likeCounts = allLikes.stream()
            .collect(Collectors.groupingBy(BookLike::getBookId, Collectors.counting()));
        
        List<Map.Entry<Integer, Long>> topEntries = likeCounts.entrySet().stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());
            
        if (topEntries.isEmpty()) {
            return R.success(Collections.emptyList());
        }
        
        List<Integer> bookIds = topEntries.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<Book> books = bookService.listByIds(bookIds);
        Map<Integer, Book> bookMap = books.stream().collect(Collectors.toMap(Book::getBookId, b -> b));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : topEntries) {
            Book book = bookMap.get(entry.getKey());
            if (book != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("bookId", book.getBookId());
                map.put("bookName", book.getBookName());
                map.put("count", entry.getValue());
                result.add(map);
            }
        }
        return R.success(result);
    }
}
