package com.wkc.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wkc.library.entity.Advice;

import java.util.List;


/**
 * @author xiaoniao
 * @date 2024/5/30 20:07
 */
public interface AdviceService extends IService<Advice> {

    List<Advice> getUserAdvice(String userName);

    Integer inputSuggest(String info, String userName);
}
