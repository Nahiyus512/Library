package com.wkc.library.controller;

import com.wkc.library.controller.vo.bookclass.BookClassAddReq;
import com.wkc.library.controller.vo.bookclass.BookClassDeleteReq;
import com.wkc.library.controller.vo.bookclass.BookClassUpdateReq;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookClass;
import com.wkc.library.entity.R;
import com.wkc.library.service.BookClassService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rong
 * @Description 书本分类
 * @date 2025/12/8 13:34
 */
@Controller
@ResponseBody
@CrossOrigin
@Slf4j
@RequestMapping("/class")
@RequiredArgsConstructor
public class BookClassController {
    private final BookClassService bookClassService;

    @PostMapping("/add")
    public R<Boolean> addBookClass(@RequestBody BookClassAddReq bookClassAddReq){
        BookClass bookClass = new BookClass();
        BeanUtils.copyProperties(bookClassAddReq, bookClass);
        return R.success(bookClassService.save(bookClass));
    }

    @GetMapping("/get")
    public R<List<BookClass>> getBookClassList(){
        return R.success(bookClassService.list());
    }

    @PostMapping("/update")
    public R<Boolean> updateBookClassById(@RequestBody BookClassUpdateReq bookClassUpdateReq){
        BookClass bookClass = new BookClass();
        BeanUtils.copyProperties(bookClassUpdateReq, bookClass);
        return R.success(bookClassService.updateById(bookClass));
    }

    @PostMapping("/delete")
    public R<Boolean> deleteBookClassById(@RequestBody BookClassDeleteReq removeBookClassReq){
        return R.success(bookClassService.removeById(removeBookClassReq.getClassId()));
    }
}
