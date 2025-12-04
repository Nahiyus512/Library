package com.wkc.library.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wkc.library.entity.Advice;
import com.wkc.library.entity.R;
import com.wkc.library.service.AdviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author xiaoniao
 * @date 2024/7/2 19:29
 */
@RestController
@Slf4j
@RequestMapping("/advice")
@CrossOrigin
public class AdviceController {

    @Autowired
    AdviceService adviceService;

    @GetMapping("/get")
    public R<List<Advice>> getAdvice(String userName) {
        List<Advice> userAdvice = adviceService.getUserAdvice(userName);
        return R.success(userAdvice);
    }

    @PostMapping("/input")
    public R<String> inputAdvice(@RequestBody Advice advice) {
        log.info("info:{},userName:{}",advice.getInfo(),advice.getUserName());
        Integer integer = adviceService.inputSuggest(advice.getInfo(), advice.getUserName());
        if(integer > 0) {
            return R.success("提交留言建议成功");
        }else {
            return R.error("提交留言建议失败");
        }
    }


    @GetMapping("/getAll")
    public R<List<Advice>> getAllAdvice() {
        List<Advice> adviceList = adviceService.list();
        return R.success(adviceList);
    }

    @PostMapping("/reply")
    public R<String> replyInfo(@RequestBody Advice advice) {
        LambdaUpdateWrapper<Advice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Advice::getId,advice.getId());
        wrapper.set(Advice::getReply,advice.getReply());
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        advice.setReplyTime(localDateTime.format(formatter));
        wrapper.set(Advice::getReplyTime,advice.getReplyTime());
        boolean update = adviceService.update(wrapper);
        if(update) {
            return R.success("回复成功");
        }
        return R.error("回复失败");
    }
}
