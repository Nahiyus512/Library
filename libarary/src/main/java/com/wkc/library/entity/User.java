package com.wkc.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoniao
 * @date 2023/2/17 13:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @TableId(value = "id")
    private String id;//用户id
    private String name;//用户名
    private String password;//用户密码
    private Integer age;//用户年龄
    private String address;//用户地址


}
