package com.wkc.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wkc.library.entity.Advice;

import java.util.List;


/**
 * @author Nah
 * @date 2025/12/30 20:07
 */
public interface AdviceService extends IService<Advice> {

    List<Advice> getUserAdvice(String userName);

    List<Advice> getBookAdvice(String bookId);

    Integer inputSuggest(Advice advice);
}
