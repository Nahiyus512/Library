package com.wkc.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookClass;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rong
 * @Description
 * @date 2025/12/8 13:36
 */
@Mapper
public interface BookClassMapper extends BaseMapper<BookClass> {

}
