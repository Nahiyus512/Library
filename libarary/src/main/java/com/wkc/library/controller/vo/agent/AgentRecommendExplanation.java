package com.wkc.library.controller.vo.agent;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AgentRecommendExplanation {
    private String mainReason;
    private List<String> keyFeatures;
    private Map<String, Double> weightContributions;
}
