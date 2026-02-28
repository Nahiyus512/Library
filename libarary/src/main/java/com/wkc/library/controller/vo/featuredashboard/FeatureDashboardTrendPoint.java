package com.wkc.library.controller.vo.featuredashboard;

import lombok.Data;

@Data
public class FeatureDashboardTrendPoint {
    private String date;
    private Integer enterUv;
    private Double completeRate;
    private Double rateSubmitRate;
    private Double acceptRate24h;
}

