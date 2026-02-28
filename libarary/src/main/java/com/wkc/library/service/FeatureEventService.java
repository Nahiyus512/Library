package com.wkc.library.service;

import com.wkc.library.controller.vo.feature.FeatureEventBatchRequest;
import com.wkc.library.controller.vo.feature.FeatureEventBatchResult;

public interface FeatureEventService {
    FeatureEventBatchResult reportBatch(FeatureEventBatchRequest request);
}

