package com.wkc.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wkc.library.entity.User;

/**
 * @author xiaoniao
 * @date 2023/2/17 13:32
 */
public interface UserService extends IService<User> {

    //通过用户名删除用户
    Boolean deleteByName(String username);

    //通过用户id修改用户信息
    Boolean updateUserById(User user);
}
