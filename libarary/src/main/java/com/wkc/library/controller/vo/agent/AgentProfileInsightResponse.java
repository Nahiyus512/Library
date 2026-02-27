package com.wkc.library.controller.vo.agent;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AgentProfileInsightResponse {
    private Map<String, Double> interestCategoryDistribution;
    private List<Map<String, Object>> readingActiveTrend;
    private Map<String, Integer> scoreRangeDistribution;
    private Map<String, Integer> historyBehaviorStats;
}
