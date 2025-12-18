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
        // Ensure we only get general advice (not book comments) for the user's "Advice" view if needed?
        // The requirement says: "The original comment system is also retained, and comments in the suggestion section are distinguished by having no associated book fields"
        // So for "general advice", we might want to filter where bookId is NULL.
        // But getUserAdvice seems to be for showing the user's OWN advice history. They might want to see their book comments too.
        // Let's assume getUserAdvice returns all advice by that user for now, or maybe only general ones.
        // "comments in the suggestion section are distinguished by having no associated book fields"
        // This likely refers to the "list all" or "admin view" or the public suggestion board.
        // Let's stick to the user's specific request.
        // If the user wants to see "other user's comments on this book", that's getBookAdvice.
        // If the user wants to see "my advice history", that's getUserAdvice.
        // I will update getUserAdvice to return all, as it's by userName.
        // Wait, if "suggestion section" implies a public board, then we need a method for that.
        // AdviceController.getAllAdvice() uses adviceService.list().
        
        List<Advice> adviceList = adviceMapper.selectList(wrapper);
        return adviceList;
    }

    @Override
    public List<Advice> getBookAdvice(String bookId) {
        LambdaQueryWrapper<Advice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Advice::getBookId, bookId);
        return adviceMapper.selectList(wrapper);
    }

    @Override
    public Integer inputSuggest(Advice advice) {
        // Advice advice = new Advice(); // object passed in
        // advice.setInfo(info);
        // advice.setUserName(userName);
        LocalDateTime date = LocalDateTime.now(); // get the current date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        advice.setInfoTime(date.format(formatter));
        int i = adviceMapper.insert(advice);
        return i;
    }
}
