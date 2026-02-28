package com.wkc.library.controller;

import com.wkc.library.controller.vo.feature.FeatureEventBatchRequest;
import com.wkc.library.controller.vo.feature.FeatureEventBatchResult;
import com.wkc.library.entity.R;
import com.wkc.library.service.FeatureEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/feature/events")
public class FeatureEventController {

    @Autowired
    private FeatureEventService featureEventService;

    @PostMapping("/batch")
    public R<FeatureEventBatchResult> reportBatch(@RequestBody FeatureEventBatchRequest request) {
        try {
            FeatureEventBatchResult result = featureEventService.reportBatch(request);
            return R.success(result);
        } catch (IllegalArgumentException e) {
            log.warn("invalid feature event batch: {}", e.getMessage());
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("report feature event batch failed", e);
            return R.error("事件上报失败");
        }
    }
}

