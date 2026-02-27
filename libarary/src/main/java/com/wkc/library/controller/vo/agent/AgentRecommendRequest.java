package com.wkc.library.controller.vo.agent;

import lombok.Data;

@Data
public class AgentRecommendRequest {
    private Integer userId;
    private String demand;
    private Integer topN;
}
