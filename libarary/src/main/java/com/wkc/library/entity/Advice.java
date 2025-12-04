package com.wkc.library.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author xiaoniao
 * @date 2024/7/2 19:21
 */

@Data
public class Advice {

    private int id;
    private String info;
    private String reply;
    private String userName;
    private String infoTime;
    private String replyTime;
}
