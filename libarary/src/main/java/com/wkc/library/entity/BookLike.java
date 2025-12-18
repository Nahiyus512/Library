package com.wkc.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author TraeAI
 * @date 2025/12/18
 */
@Data
@TableName("book_like")
public class BookLike {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String userName;
    private Integer bookId;
    private String bookName;
    private Integer likeLevel; // 0-不想看, 1-还行, 2-想看
    private Date createTime;
    private Date updateTime;
}
