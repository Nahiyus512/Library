package com.wkc.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookScore;

import java.util.List;

/**
 * @author xiaoniao
 * @date 2023/4/11 13:14
 */
public interface BookScoreService extends IService<BookScore> {
    List<Book> recommend(String userId);

    boolean updateScore(BookScore bookScore);
}
