package com.wkc.library.entity.agent;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "agent_decision_trace")
public class AgentDecisionTrace {
    @Id
    private String id;
    private Integer userId;
    private String demand;
    private String intent;
    private String strategyUsed;
    private Map<String, Double> weights;
    private List<String> decisionPath;
    private List<Map<String, Object>> recommendSnapshot;
    private Date createdAt;
}
