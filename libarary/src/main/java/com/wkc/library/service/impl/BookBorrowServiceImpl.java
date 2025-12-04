package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookBorrow;
import com.wkc.library.mapper.BookBorrowMapper;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.service.BookBorrowService;
import com.wkc.library.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaoniao
 * @date 2024/6/14 19:19
 */
@Service
public class BookBorrowServiceImpl extends ServiceImpl<BookBorrowMapper, BookBorrow> implements BookBorrowService {

    @Autowired
    BookMapper bookMapper;

    @Autowired
    BookBorrowMapper borrowMapper;

    @Override
    public Boolean borrowBook(BookBorrow bookBorrow) {
        bookBorrow.setId(UUIDUtil.generateUUID());
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        LambdaUpdateWrapper<Book> updateWrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Book::getBookId,bookBorrow.getBookId());
        Book book = bookMapper.selectOne(wrapper);
        if(book.getBookNum() <= 0) {
            return false;
        }else {
            borrowMapper.insertBorrowInfo(bookBorrow);
            updateWrapper.eq(Book::getBookId,bookBorrow.getBookId()).set(Book::getBookNum,book.getBookNum()-1);
            bookMapper.update(book,updateWrapper);
            return true;
        }
    }

    @Override
    public Boolean backBook(BookBorrow borrow) {
        LambdaQueryWrapper<BookBorrow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookBorrow::getUserName,borrow.getUserName());
        wrapper.eq(BookBorrow::getBookName,borrow.getBookName());
        wrapper.eq(BookBorrow::getBeginTime,borrow.getBeginTime());
        int delete = borrowMapper.delete(wrapper);
        if(delete > 0) {
            LambdaUpdateWrapper<Book> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Book::getBookName,borrow.getBookName());
            updateWrapper.setSql("book_num = book_num + 1");
            int update = bookMapper.update(null, updateWrapper);
            if(update > 0) {
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
}
