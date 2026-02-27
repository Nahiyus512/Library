package com.wkc.library.service.agent;

import com.wkc.library.controller.vo.agent.AgentRecommendResponse;
import com.wkc.library.controller.vo.agent.AgentProfileInsightResponse;
import com.wkc.library.entity.agent.AgentDecisionTrace;

import java.util.List;

public interface AgentRecommendService {
    AgentRecommendResponse recommend(Integer userId, String demand, Integer topN);
    AgentDecisionTrace getDecisionTrace(String traceId);
    List<AgentDecisionTrace> getLatestDecisionTraces(Integer userId, Integer limit);
    AgentProfileInsightResponse getProfileInsight(Integer userId);
}
