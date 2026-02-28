package com.wkc.library.controller.vo.featuredashboard;

import lombok.Data;

@Data
public class FeatureDashboardOverviewResponse {
    private Integer enterUv;
    private Double enterRate;
    private Double completeRate;
    private Double rateSubmitRate;
    private Long avgDurationMs;
    private Double like2Rate;
    private Double acceptRate24h;
}

