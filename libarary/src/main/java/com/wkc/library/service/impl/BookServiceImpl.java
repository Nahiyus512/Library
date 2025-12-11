package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.entity.Book;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaoniao
 * @date 2023/2/17 13:33
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    @Autowired
    BookMapper bookMapper;


    @Override
    public int add(Book book) {
        int insert = bookMapper.insert(book);
        return insert;
    }

    @Override
    public boolean deleteById(Integer id) {
        int i = bookMapper.deleteByBookId(id);
        if(i > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByBookId(Book book) {
        int i =  bookMapper.updateByBookId(book);
        if(i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Book> searchBookByLike(String name) {
        LambdaQueryWrapper<Book> bookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bookLambdaQueryWrapper.like(Book::getBookName,name);
        List<Book> books = bookMapper.selectList(bookLambdaQueryWrapper);
        return books;
    }
}
