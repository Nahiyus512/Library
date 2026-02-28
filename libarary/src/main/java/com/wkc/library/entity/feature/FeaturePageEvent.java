package com.wkc.library.entity.feature;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@Document(collection = "feature_page_events")
public class FeaturePageEvent {
    @Id
    private String id;

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
    private Date createdAt;
}

