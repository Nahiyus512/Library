package com.wkc.library.entity;

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

    private String id;
    private String userId;
    private String bookId;
    private Integer score;
    private String time;
}
