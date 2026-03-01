package com.wkc.library.controller;


import java.util.List;

import com.wkc.library.assistant.PolarisAgent;
import com.wkc.library.bean.ChatForm;
import com.wkc.library.bean.ChatMessages;
import com.wkc.library.entity.R;
import com.wkc.library.service.UserService;
import com.wkc.library.service.agent.PolarisAgentRouteResult;
import com.wkc.library.service.agent.PolarisAgentRouterService;
import com.wkc.library.store.MongoChatMemoryStore;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/polaris")
@CrossOrigin
public class PolarisController {
    @Autowired
    private PolarisAgent polarisAgent;

    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;
    @Autowired
    private PolarisAgentRouterService polarisAgentRouterService;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/chat",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatForm chatForm, HttpServletRequest request) {
        try {
            mongoChatMemoryStore.bindUser(chatForm.getMemoryId(), chatForm.getUserId());
            Integer uid = parseUserId(chatForm.getUserId());
            com.wkc.library.entity.User user = uid == null ? null : userService.getById(uid);
            String userName = user == null ? null : user.getName();
            PolarisAgentRouteResult routeResult = polarisAgentRouterService.routeAndHandle(
                    chatForm.getMessage(),
                    chatForm.getMemoryId(),
                    uid,
                    userName
            );
            if (routeResult.isHandled()) {
                return Flux.just(routeResult.getResponse());
            }
            return polarisAgent.chat(chatForm.getMemoryId(), chatForm.getMessage(), chatForm.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            return Flux.error(e);
        }
    }

    @GetMapping("/history")
    public R<List<ChatMessages>> getHistory(@RequestParam String userId) {
        return R.success(mongoChatMemoryStore.getChatMessagesByUserId(userId));
    }

    private Integer parseUserId(String userId) {
        try {
            return Integer.parseInt(userId);
        } catch (Exception ex) {
            return null;
        }
    }
}
