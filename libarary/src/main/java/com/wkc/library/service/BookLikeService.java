package com.wkc.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wkc.library.entity.BookLike;

/**
 * @author TraeAI
 * @date 2025/12/18
 */
public interface BookLikeService extends IService<BookLike> {
    Boolean likeBook(BookLike bookLike);
}
