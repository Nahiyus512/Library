package com.wkc.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author xiaoniao
 * @date 2024/6/14 16:07
 */
@Data
@TableName("book_borrow")
public class BookBorrow {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String userName;
    private String bookId;
    private String bookName;
    private double borrowTime;
    private String beginTime;
    private String endTime;
}
