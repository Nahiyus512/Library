package com.wkc.library.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wkc.library.entity.BookScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author Nah
 * @date 2025/12/11 13:13
 */
@Mapper
public interface BookScoreMapper extends BaseMapper<BookScore> {
    Integer insetScore(@Param("bookScore") BookScore bookScore);
    List<BookScore> getAll();
}
