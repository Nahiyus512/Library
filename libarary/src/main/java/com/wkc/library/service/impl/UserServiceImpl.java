package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.mapper.UserMapper;
import com.wkc.library.service.UserService;
import com.wkc.library.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaoniao
 * @date 2023/2/17 13:33
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    final UserMapper userMapper;

    @Override
    public Boolean deleteByName(String username) {

        Integer integer = userMapper.deleteByName(username);
        if(integer > 0){
            return true;
        }
        return false;
    }


    @Override
    public Boolean updateUserById(User user) {
        Integer result = userMapper.updateInfo(user);
        if(result > 0)
            return true;
        return false;
    }

    @Override
    public User getUserByUserName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
        queryWrapper.eq(User::getName, username);
        return userMapper.selectOne(queryWrapper);
    }
}
