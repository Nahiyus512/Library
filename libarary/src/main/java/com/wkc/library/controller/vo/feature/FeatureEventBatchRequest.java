package com.wkc.library.controller.vo.feature;

import lombok.Data;

import java.util.List;

@Data
public class FeatureEventBatchRequest {
    private List<FeatureEventPayload> events;
}

