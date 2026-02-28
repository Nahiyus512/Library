package com.wkc.library.controller.vo.feature;

import lombok.Data;

import java.util.Map;

@Data
public class FeatureEventPayload {
    private String eventId;
    private Integer userId;
    private String sessionId;
    private String traceId;
    private Integer bookId;
    private String bookName;
    private String routePath;
    private String source;
    private String eventType;
    private String checkpointKey;
    private Integer likeLevel;
    private Long durationMs;
    private Double scrollDepth;
    private Map<String, Object> extra;
    private String createdAt;
}

