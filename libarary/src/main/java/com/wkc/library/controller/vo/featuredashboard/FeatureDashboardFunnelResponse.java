package com.wkc.library.controller.vo.featuredashboard;

import lombok.Data;

@Data
public class FeatureDashboardFunnelResponse {
    private Integer enter;
    private Integer checkpoint;
    private Integer rate;
    private Integer recommendAccept;
}

