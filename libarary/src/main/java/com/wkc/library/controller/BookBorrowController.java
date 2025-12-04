package com.wkc.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wkc.library.common.QueryPageParam;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookBorrow;
import com.wkc.library.entity.R;
import com.wkc.library.entity.resultInfo.BookPageInfo;
import com.wkc.library.entity.resultInfo.BorrowPageInfo;
import com.wkc.library.mapper.BookBorrowMapper;
import com.wkc.library.service.BookBorrowService;
import com.wkc.library.service.impl.BookBorrowServiceImpl;
import com.wkc.library.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * @author xiaoniao
 * @date 2024/6/15 11:42
 */
@RestController
@Slf4j
@RequestMapping("/bookBorrow")
@CrossOrigin
public class BookBorrowController {

    @Autowired
    BookBorrowService bookBorrowService;

    @Autowired
    BookBorrowMapper borrowMapper;

    @PutMapping("/borrow")
    public R<String> borrowBook(@RequestBody BookBorrow bookBorrow) {
        Boolean flag = bookBorrowService.borrowBook(bookBorrow);
        if(flag){
            return R.success("图书借阅成功");
        }
        return R.error("图书借阅失败");
    }

    @PostMapping("/list")
    public R<BorrowPageInfo> borrowList(@RequestBody QueryPageParam pageParam) {
        log.info("借阅书本分页信息: num:{},size:{}",pageParam.getPageNum(),pageParam.getPageSize());
        Page<BookBorrow> page = new Page<>(pageParam.getPageNum(),pageParam.getPageSize(),true);
        LambdaQueryWrapper<BookBorrow> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(BookBorrow::getBookName,pageParam.getNameInfo());
        int count = borrowMapper.selectCount(wrapper);
        IPage<BookBorrow>  iPage= bookBorrowService.page(page, wrapper);
        BorrowPageInfo borrowPageInfo = new BorrowPageInfo();
        borrowPageInfo.setBorrows(iPage.getRecords());
        borrowPageInfo.setCount(count);
        return R.success(borrowPageInfo);
    }


    @GetMapping("/getBorrowInfo")
    public R<List<BookBorrow>> getBorrowInfo(String username) {
        LambdaQueryWrapper<BookBorrow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookBorrow::getUserName,username);
        List<BookBorrow> list = bookBorrowService.list(wrapper);
        return R.success(list);
    }

    @PostMapping("/borrowTime")
    public R<String> borrowTime(@RequestBody BookBorrow borrow) {
        log.info("borrowTime==>{}",borrow.getBorrowTime());
        log.info("borrowDate==>{}",borrow.getBeginTime());
        // 确保borrowTime是整数类型，代表续借天数
        if (borrow.getBorrowTime() <= 0) {
            return R.error("续借天数需为正数");
        }
        LambdaUpdateWrapper<BookBorrow> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BookBorrow::getUserName,borrow.getUserName());
        wrapper.eq(BookBorrow::getBeginTime,borrow.getBeginTime());
        wrapper.eq(BookBorrow::getBookName,borrow.getBookName());
        wrapper.set(BookBorrow::getBorrowTime,borrow.getBorrowTime());
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime beginTime = LocalDateTime.parse(borrow.getBeginTime().toString(), formatter);
            // 增加续借天数到开始时间
            LocalDateTime endTime = beginTime.plusDays((long) borrow.getBorrowTime());
            // 设置更新的结束时间为endTime
            wrapper.set(BookBorrow::getEndTime, endTime.format(formatter)); // 注意：这里直接格式化回字符串可能不是最佳实践，具体取决于数据库字段类型
            // 执行更新操作
            boolean update = bookBorrowService.update(wrapper);
            if (update) {
                return R.success("续借成功");
            } else {
                return R.error("未找到匹配的借阅记录或更新失败");
            }
        } catch (DateTimeParseException e) {
            // 处理日期时间解析异常
            return R.error("日期时间格式错误：" + e.getMessage());
        }
    }


    @PostMapping("/back")
    public R<String> backBook(@RequestBody BookBorrow borrow) {
        Boolean isOk = bookBorrowService.backBook(borrow);
        if(isOk) {
            return R.success("归还成功");
        }else {
            return R.error("归还失败");
        }
    }

}
