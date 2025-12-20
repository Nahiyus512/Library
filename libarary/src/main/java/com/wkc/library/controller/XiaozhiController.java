package com.wkc.library.controller;


import com.wkc.library.assistant.XiaozhiAgent;
import com.wkc.library.bean.ChatForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/xiaozhi")
public class XiaozhiController {
    @Autowired
    private XiaozhiAgent xiaozhiAgent;

    @PostMapping(value = "/chat",produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm, HttpServletRequest request) {
        return xiaozhiAgent.chat(chatForm.getMemoryId(), chatForm.getMessage(),chatForm.getUserId());
    }
}
