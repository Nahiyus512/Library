package com.wkc.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wkc.library.entity.BookBorrow;


/**
 * @author xiaoniao
 * @date 2024/6/14 19:17
 */
public interface BookBorrowService extends IService<BookBorrow> {
    Boolean borrowBook(BookBorrow bookBorrow);

    Boolean backBook(BookBorrow borrow);
}
