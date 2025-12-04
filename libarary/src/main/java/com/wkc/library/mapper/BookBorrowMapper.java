package com.wkc.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wkc.library.entity.BookBorrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author xiaoniao
 * @date 2024/6/14 19:08
 */
@Mapper
public interface BookBorrowMapper extends BaseMapper<BookBorrow> {

    BookBorrow borrowAll(@Param("bookName") String bookName);


    Integer insertBorrowInfo(@Param("bookBorrow") BookBorrow bookBorrow);
}
