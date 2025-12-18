package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.entity.BookLike;
import com.wkc.library.mapper.BookLikeMapper;
import com.wkc.library.service.BookLikeService;
import org.springframework.stereotype.Service;

/**
 * @author TraeAI
 * @date 2025/12/18
 */
@Service
public class BookLikeServiceImpl extends ServiceImpl<BookLikeMapper, BookLike> implements BookLikeService {

    @Override
    public Boolean likeBook(BookLike bookLike) {
        LambdaQueryWrapper<BookLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookLike::getUserName, bookLike.getUserName())
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
