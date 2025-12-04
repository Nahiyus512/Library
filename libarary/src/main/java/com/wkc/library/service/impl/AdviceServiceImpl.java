package com.wkc.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkc.library.entity.Advice;
import com.wkc.library.mapper.AdviceMapper;
import com.wkc.library.service.AdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author xiaoniao
 * @date 2024/7/2 19:26
 */
@Service
public class AdviceServiceImpl extends ServiceImpl<AdviceMapper, Advice> implements AdviceService {

    @Autowired
    AdviceMapper adviceMapper;

    @Override
    public List<Advice> getUserAdvice(String userName) {
        LambdaQueryWrapper<Advice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Advice::getUserName,userName);
        List<Advice> adviceList = adviceMapper.selectList(wrapper);
        return adviceList;
    }

    @Override
    public Integer inputSuggest(String info, String userName) {
        Advice advice = new Advice();
        advice.setInfo(info);
        advice.setUserName(userName);
        LocalDateTime date = LocalDateTime.now(); // get the current date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        advice.setInfoTime(date.format(formatter));
        int i = adviceMapper.insert(advice);
        return i;
    }
}
