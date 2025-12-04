package com.wkc.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wkc.library.entity.resultInfo.BookPageInfo;
import com.wkc.library.common.QueryPageParam;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.R;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.service.impl.BookServiceImpl;
import com.wkc.library.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiaoniao
 * @date 2023/2/28 15:16
 */
@Controller
@ResponseBody
@CrossOrigin
@Slf4j
@RequestMapping("/book")
public class BookController {

    @Autowired
    BookMapper bookMapper;


    @Autowired
    BookServiceImpl bookService;

    @GetMapping("/get")
    public R<List<Book>> getBookByName(@RequestParam String name){
        //String name = request.getParameter("name");
        //List<Book> byName = bookMapper.getByName(name);
        List<Book> books = bookService.searchBookByLike(name);
        return R.success(books);
    }

    @GetMapping("/all")
    public R<List<Book>> getAllBook(){
        List<Book> books = bookService.list();
        return R.success(books);
    }

    @PostMapping("/listPage")
    public R<BookPageInfo> listPage(@RequestBody QueryPageParam pageParam) {
        //System.out.println(pageParam);
        log.info("分页信息: num:{},size:{}",pageParam.getPageNum(),pageParam.getPageSize());
        Page<Book> page = new Page<>(pageParam.getPageNum(),pageParam.getPageSize(),true);
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Book::getBookName,pageParam.getNameInfo());
        int count = bookMapper.selectCount(queryWrapper);
        IPage result = bookService.page(page,queryWrapper);
        BookPageInfo info = new BookPageInfo();
        info.setBookList(result.getRecords());
        info.setCount(count);
        return R.success(info);
    }

    @PostMapping("/add")
    public R<String> addBook(@RequestBody Book book){
        log.info("添加的信息:id{},name:{},author:{}", book.getBookId(), book.getBookName(), book.getBookAuthor());
        book.setBookId(UUIDUtil.generateUUID());
        return bookService.add(book) > 0 ? R.success("添加成功") : R.error("添加失败");
    }

    @PutMapping("/change")
    public R change(@RequestBody Book book){
        log.info("修改的信息:id{},name:{},author:{}", book.getBookId(), book.getBookName(), book.getBookAuthor());
        boolean b = bookService.updateByBookId(book);
        return b ? R.success("修改信息成功") : R.error("修改信息失败");
    }

    @DeleteMapping("/del")
    public R delete(@RequestBody Book book){
        boolean b = bookService.deleteById(book.bookId);
        if(b) {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @GetMapping("/home")
    public R home() {
        return R.success("测试成功");
    }

}
