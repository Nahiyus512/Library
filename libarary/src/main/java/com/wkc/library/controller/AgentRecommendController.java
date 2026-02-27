package com.wkc.library.controller;

import com.wkc.library.controller.vo.agent.AgentRecommendRequest;
import com.wkc.library.controller.vo.agent.AgentRecommendResponse;
import com.wkc.library.controller.vo.agent.AgentProfileInsightResponse;
import com.wkc.library.entity.R;
import com.wkc.library.entity.agent.AgentDecisionTrace;
import com.wkc.library.service.agent.AgentRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/decisionTrace/{traceId}")
    public R<AgentDecisionTrace> getDecisionTrace(@PathVariable String traceId) {
        return R.success(agentRecommendService.getDecisionTrace(traceId));
    }

    @GetMapping("/decisionTrace/latest")
    public R<List<AgentDecisionTrace>> getLatestDecisionTraces(@RequestParam Integer userId,
                                                               @RequestParam(required = false) Integer limit) {
        return R.success(agentRecommendService.getLatestDecisionTraces(userId, limit));
    }

    @GetMapping("/profile/insight")
    public R<AgentProfileInsightResponse> getProfileInsight(@RequestParam Integer userId) {
        return R.success(agentRecommendService.getProfileInsight(userId));
    }
}
