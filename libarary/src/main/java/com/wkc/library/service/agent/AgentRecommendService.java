package com.wkc.library.service.agent;

import com.wkc.library.controller.vo.agent.AgentRecommendResponse;

public interface AgentRecommendService {
    AgentRecommendResponse recommend(Integer userId, String demand, Integer topN);
}
