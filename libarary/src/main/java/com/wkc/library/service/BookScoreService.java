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
    List<Book> recommend(Integer userId);
    
    /**
     * 增强版推荐接口，支持指定算法策略
     * @param userId 用户ID
     * @param strategy 推荐策略: "user_cf", "item_cf", "content_based", "association_rule"
     * @return 推荐图书列表
     */
    List<Book> recommend(Integer userId, String strategy);

    boolean updateScore(BookScore bookScore);
}
