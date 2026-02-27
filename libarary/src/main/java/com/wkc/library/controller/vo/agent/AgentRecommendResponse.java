package com.wkc.library.controller.vo.agent;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AgentRecommendResponse {
    private String decisionTraceId;
    private String intent;
    private String strategyUsed;
    private String strategyDescription;
    private Map<String, Double> weights;
    private List<String> decisionPath;
    private List<AgentRecommendItem> recommendedBooks;
}
