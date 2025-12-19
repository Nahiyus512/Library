package com.wkc.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wkc.library.common.Constant;
import com.wkc.library.common.QueryPageParam;
import com.wkc.library.entity.R;
import com.wkc.library.entity.User;
import com.wkc.library.entity.resultInfo.UserPageInfo;
import com.wkc.library.mapper.UserMapper;
import com.wkc.library.service.UserService;
import com.wkc.library.util.JwtUtil;
import com.wkc.library.util.RedisUtil;
import com.wkc.library.util.UUIDUtil;
import com.wkc.library.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoniao
 * @date 2023/2/17 13:28
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
   JwtUtil jwtUtil;


   @PostMapping("/login")
    public R<Map<String,Object>> login(@RequestBody Map map){
       Map<String,Object> resultMap = new HashMap<>();
       String username = map.get("username").toString();
       UserContext.setUsername(username);
       log.info("UserContext.username:{}",UserContext.getUsername());
       String password = map.get("password").toString();
       String captcha = map.get("captcha").toString();
       String uuid = map.get("uuid").toString();
       User user = userMapper.login(username, password);
        if (user != null){
            Object captchaObj = redisUtil.get(Constant.getCaptchaKey(uuid));
            if(captchaObj == null) {
                return R.error("验证码已失效");
            }
           if( !String.valueOf(captchaObj).equals(captcha)) {
               return R.error("验证码错误");
           }
            resultMap.put("info","用户登录成功");
            String token = jwtUtil.createToken(user.getName());
            redisUtil.set(Constant.getTokenKey(user.getName()),token,3600);
            resultMap.put("token",token);
            resultMap.put("id", user.getId());
            return R.success(resultMap);
        }else {
            return R.error("账号或密码错误，登录失败");
        }
   }


    @PostMapping("/logon")
    public R<String> logon(@RequestBody User user){
        String username = user.getName();
        String password = user.getPassword();
        Integer age = user.getAge();
        String address = user.getAddress();
        User newUser = new User();
        //newUser.setId(UUIDUtil.generateUUID());
        newUser.setName(username);
        newUser.setAge(age);
        newUser.setPassword(password);
        newUser.setAddress(address);
        if(userMapper.selectByName(user) == null){
            Integer result = userMapper.logon(newUser);
            if(result > 0){
                return R.success("用户注册成功");
            }
            return R.error("用户注册失败");
        }
        return R.error("该用户账号已存在，请重新输入");
    }

    @GetMapping("/all")
    public R<List<User>> getAllUser(){
        List<User> users = userService.list();
        return R.success(users);
    }

    @PostMapping("/listPage")
    public R<UserPageInfo> listPage(@RequestBody QueryPageParam pageParam) {
        System.out.println(pageParam);
        log.info("分页信息: num:{},size:{}",pageParam.getPageNum(),pageParam.getPageSize());
        Page<User> page = new Page<>(pageParam.getPageNum(),pageParam.getPageSize(),true);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(User::getName,pageParam.getNameInfo());
        int count = Math.toIntExact(userMapper.selectCount(queryWrapper));
        IPage result = userService.page(page,queryWrapper);
        UserPageInfo info = new UserPageInfo();
        info.setUserList(result.getRecords());
        info.setCount(count);
        return R.success(info);
    }

    @DeleteMapping("/del")
    public R<String> deleteUserByName(@RequestBody Map map){
        String username = map.get("username").toString();
        Boolean flag = userService.deleteByName(username);
        System.out.println(username);
        if(flag) {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @PutMapping("/change")
    public R<String> updateUserById(@RequestBody User user) {
        Boolean flag = userService.updateUserById(user);
        if(flag)
            return R.success("修改成功");
        return R.error("修改失败");
    }

    @GetMapping("/captcha")
    public R getCaptcha(String name) {
        log.info("username=={}",name);
        //String captcha = captchaService.getCaptcha(username);

        return R.success("captcha");
    }


    @GetMapping("/getUserByName")
    public R<User> getUser(String name) {
       LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
       wrapper.eq(User::getName,name);
        User user = userService.getOne(wrapper);
        return R.success(user);
    }

    @PostMapping("/updateByName")
    public R<String> updateByName(@RequestBody User user) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getName,user.getName());
        wrapper.set(User::getPassword,user.getPassword()).set(User::getAge,user.getAge()).set(User::getAddress,user.getAddress());
        boolean update = userService.update(wrapper);
        if (update) {
            return R.success("修改信息成功");
        }
        return R.error("修改信息失败");
    }
}
