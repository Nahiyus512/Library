package com.wkc.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author rong
 * @Description
 * @date 2025/12/8 13:37
 */
@Data
@Component
@TableName("book_class")
public class BookClass {
    @TableId(type = IdType.AUTO)
    private Integer classId;
    private String classify;
}
