package com.wkc.library.mapper;
import java.util.List;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import com.wkc.library.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaoniao
 * @date 2023/2/17 13:27
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    //用户登录
    User login(String username,String password);


    //用户注册
    Integer logon(@Param("user") User user);

    //查询数据库中是否有当前用户
    User selectByName(@Param("user") User user);

    //用户信息修改
    Integer updateInfo(@Param("user") User user);

    //用户信息修改通过ID
    //Integer updateUserByI(@Param("user") User user);

    //根据用户名删除用户
    Integer deleteByName(@Param("username") String name);

}
