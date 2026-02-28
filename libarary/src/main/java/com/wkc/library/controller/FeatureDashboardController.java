package com.wkc.library.controller;

import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardBookItem;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardFunnelResponse;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardOverviewResponse;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardSourceItem;
import com.wkc.library.controller.vo.featuredashboard.FeatureDashboardTrendPoint;
import com.wkc.library.entity.R;
import com.wkc.library.service.FeatureDashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/feature/dashboard")
public class FeatureDashboardController {

    @Autowired
    private FeatureDashboardService featureDashboardService;

    @GetMapping("/overview")
    public R<FeatureDashboardOverviewResponse> overview(@RequestParam String from,
                                                        @RequestParam String to) {
        try {
            return R.success(featureDashboardService.getOverview(from, to));
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("feature dashboard overview failed", e);
            return R.error("查询失败");
        }
    }

    @GetMapping("/trend")
    public R<List<FeatureDashboardTrendPoint>> trend(@RequestParam String from,
                                                     @RequestParam String to,
                                                     @RequestParam(required = false, defaultValue = "day") String granularity) {
        try {
            return R.success(featureDashboardService.getTrend(from, to, granularity));
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("feature dashboard trend failed", e);
            return R.error("查询失败");
        }
    }

    @GetMapping("/books")
    public R<List<FeatureDashboardBookItem>> books(@RequestParam String from,
                                                   @RequestParam String to,
                                                   @RequestParam(required = false, defaultValue = "acceptRate") String sort,
                                                   @RequestParam(required = false, defaultValue = "20") Integer limit) {
        try {
            return R.success(featureDashboardService.getBooks(from, to, sort, limit));
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("feature dashboard books failed", e);
            return R.error("查询失败");
        }
    }

    @GetMapping("/funnel")
    public R<FeatureDashboardFunnelResponse> funnel(@RequestParam(required = false) Integer bookId,
                                                    @RequestParam String from,
                                                    @RequestParam String to) {
        try {
            return R.success(featureDashboardService.getFunnel(bookId, from, to));
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("feature dashboard funnel failed", e);
            return R.error("查询失败");
        }
    }

    @GetMapping("/source")
    public R<List<FeatureDashboardSourceItem>> source(@RequestParam String from,
                                                      @RequestParam String to) {
        try {
            return R.success(featureDashboardService.getSourceDistribution(from, to));
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("feature dashboard source failed", e);
            return R.error("查询失败");
        }
    }
}
