package com.wkc.library;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.mapper.UserMapper;
import com.wkc.library.service.UserService;
import com.wkc.library.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class LibraryApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Test
    public void testInsert(){
        User user = new User();
        user.setName("xiao2");
        user.setPassword("1223");
        user.setAge(121);
        Integer result = userMapper.logon(user);
        System.out.println(result);
    }

    @Test
    public void testSelectByName(){
        User user = new User();
        user.setName("xiao");

        User user1 = userMapper.selectByName(user);
        System.out.println(user1);
    }

    @Test
    public void testUpdate(){
        User user = new User();
        user.setName("xiao");
        user.setPassword("1122");
        user.setAge(18);
        Integer result = userMapper.updateInfo(user);
        System.out.println(result);
    }

}
