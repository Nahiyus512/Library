package com.wkc.library.entity.agent;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "agent_call_metrics")
public class AgentCallMetric {
    @Id
    private String id;
    private String agentName;
    private Integer userId;
    private Long memoryId;
    private String requestText;
    private Boolean success;
    private Long responseTimeMs;
    private String channel;
    private String errorMessage;
    private Date createdAt;
}

