package com.wkc.library.controller.vo.feature;

import lombok.Data;

@Data
public class FeatureEventBatchResult {
    private int received;
    private int inserted;
    private int duplicated;
}

