package com.wkc.library.controller.vo.featuredashboard;

import lombok.Data;

@Data
public class FeatureDashboardBookItem {
    private Integer bookId;
    private String bookName;
    private Integer enterUv;
    private Integer enterPv;
    private Double completeRate;
    private Long avgDurationMs;
    private Integer like0Count;
    private Integer like1Count;
    private Integer like2Count;
    private Double acceptRate;
}

