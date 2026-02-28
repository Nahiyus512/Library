package com.wkc.library.service;

import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardBookItem;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardFunnelResponse;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardOverviewResponse;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardSourceItem;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardTrendPoint;

import java.util.List;

public interface FeatureDashboardService {
    FeatureDashboardOverviewResponse getOverview(String from, String to);

    List<FeatureDashboardTrendPoint> getTrend(String from, String to, String granularity);

    List<FeatureDashboardBookItem> getBooks(String from, String to, String sort, Integer limit);

    FeatureDashboardFunnelResponse getFunnel(Integer bookId, String from, String to);

    List<FeatureDashboardSourceItem> getSourceDistribution(String from, String to);
}

