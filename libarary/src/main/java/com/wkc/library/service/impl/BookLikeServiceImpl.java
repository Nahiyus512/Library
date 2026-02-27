package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.entity.BookLike;
import com.wkc.library.mapper.BookLikeMapper;
import com.wkc.library.service.BookLikeService;
import org.springframework.stereotype.Service;

/**
 * @author Nah
 * @date 2025/12/18
 */
@Service
public class BookLikeServiceImpl extends ServiceImpl<BookLikeMapper, BookLike> implements BookLikeService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.wkc.library.service.BookService bookService;

    @Override
    public Boolean likeBook(BookLike bookLike) {
        if (bookLike.getBookId() == null && bookLike.getBookName() != null) {
            LambdaQueryWrapper<com.wkc.library.entity.Book> bookWrapper = new LambdaQueryWrapper<>();
            bookWrapper.eq(com.wkc.library.entity.Book::getBookName, bookLike.getBookName().trim());
            com.wkc.library.entity.Book book = bookService.getOne(bookWrapper);
            if (book != null) {
                bookLike.setBookId(book.getBookId());
            }
        }

        if (bookLike.getBookId() == null) {
            return false;
        }

        LambdaQueryWrapper<BookLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookLike::getUserId, bookLike.getUserId())
               .eq(BookLike::getBookId, bookLike.getBookId());
        
        BookLike exist = this.getOne(wrapper);
        if (exist != null) {
            exist.setLikeLevel(bookLike.getLikeLevel());
            return this.updateById(exist);
        } else {
            return this.save(bookLike);
        }
    }
}
