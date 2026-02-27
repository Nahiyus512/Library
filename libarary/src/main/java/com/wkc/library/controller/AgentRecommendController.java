package com.wkc.library.controller;

import com.wkc.library.controller.vo.agent.AgentRecommendRequest;
import com.wkc.library.controller.vo.agent.AgentRecommendResponse;
import com.wkc.library.entity.R;
import com.wkc.library.service.agent.AgentRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
@CrossOrigin
public class AgentRecommendController {

    @Autowired
    private AgentRecommendService agentRecommendService;

    @PostMapping("/recommend")
    public R<AgentRecommendResponse> recommend(@RequestBody AgentRecommendRequest request) {
        AgentRecommendResponse response = agentRecommendService.recommend(
                request.getUserId(),
                request.getDemand(),
                request.getTopN()
        );
        return R.success(response);
    }
}
