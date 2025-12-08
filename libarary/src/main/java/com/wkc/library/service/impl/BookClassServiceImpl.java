package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.controller.BookClassController;
import com.wkc.library.entity.BookBorrow;
import com.wkc.library.entity.BookClass;
import com.wkc.library.mapper.BookBorrowMapper;
import com.wkc.library.mapper.BookClassMapper;
import com.wkc.library.service.BookBorrowService;
import com.wkc.library.service.BookClassService;
import org.springframework.stereotype.Service;

/**
 * @author rong
 * @Description
 * @date 2025/12/8 13:40
 */
@Service
public class BookClassServiceImpl extends ServiceImpl<BookClassMapper, BookClass> implements BookClassService {
}
