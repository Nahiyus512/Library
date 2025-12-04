package com.wkc.library.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import com.wkc.library.entity.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xiaoniao
 * @date 2023/2/28 13:43
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    //插入书籍
    Integer insertBook(@Param("book") Book book);


    //更具书本ID删除书本
    int deleteByBookId(@Param("bookId") String bookId);

    //更具书本Id修改书本
    int updateByBookId(@Param("book") Book book);

}
