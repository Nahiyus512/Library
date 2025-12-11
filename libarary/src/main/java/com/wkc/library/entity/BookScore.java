package com.wkc.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author xiaoniao
 * @date 2023/4/10 15:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookScore {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Integer score;
    private String time;
}
