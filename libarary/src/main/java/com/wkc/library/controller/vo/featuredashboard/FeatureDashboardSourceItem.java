package com.wkc.library.controller.vo.featuredashboard;

import lombok.Data;

@Data
public class FeatureDashboardSourceItem {
    private String source;
    private Integer enterPv;
    private Double trafficShare;
    private Double completeRate;
    private Double acceptRate;
}

