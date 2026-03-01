package com.wkc.library.service.agent;

import com.wkc.library.entity.agent.AgentCallMetric;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class AgentMetricService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void record(String agentName,
                       Integer userId,
                       Long memoryId,
                       String requestText,
                       boolean success,
                       long responseTimeMs,
                       String channel,
                       String errorMessage) {
        AgentCallMetric metric = new AgentCallMetric();
        metric.setAgentName(agentName);
        metric.setUserId(userId);
        metric.setMemoryId(memoryId);
        metric.setRequestText(requestText);
        metric.setSuccess(success);
        metric.setResponseTimeMs(responseTimeMs);
        metric.setChannel(channel);
        metric.setErrorMessage(errorMessage);
        metric.setCreatedAt(new Date());
        mongoTemplate.save(metric);
    }

    public Map<String, Object> summary(Integer days) {
        int safeDays = (days == null || days <= 0) ? 7 : Math.min(days, 90);
        LocalDate from = LocalDate.now().minusDays(safeDays - 1L);
        Date fromDate = Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Query query = new Query(Criteria.where("createdAt").gte(fromDate));
        List<AgentCallMetric> metrics = mongoTemplate.find(query, AgentCallMetric.class);

        Map<String, List<AgentCallMetric>> group = new HashMap<String, List<AgentCallMetric>>();
        for (AgentCallMetric metric : metrics) {
            String key = metric.getAgentName() == null ? "unknown" : metric.getAgentName();
            List<AgentCallMetric> list = group.get(key);
            if (list == null) {
                list = new ArrayList<AgentCallMetric>();
                group.put(key, list);
            }
            list.add(metric);
        }

        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, List<AgentCallMetric>> entry : group.entrySet()) {
            List<AgentCallMetric> list = entry.getValue();
            int total = list.size();
            int successCount = 0;
            long totalMs = 0L;
            for (AgentCallMetric metric : list) {
                if (Boolean.TRUE.equals(metric.getSuccess())) {
                    successCount++;
                }
                totalMs += metric.getResponseTimeMs() == null ? 0L : metric.getResponseTimeMs();
            }

            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("agentName", entry.getKey());
            row.put("calls", total);
            row.put("successRate", total <= 0 ? 0D : (double) successCount / total);
            row.put("avgResponseTimeMs", total <= 0 ? 0L : Math.round((double) totalMs / total));
            rows.add(row);
        }
        rows.sort((a, b) -> {
            Integer ac = (Integer) a.get("calls");
            Integer bc = (Integer) b.get("calls");
            return bc.compareTo(ac);
        });

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("days", safeDays);
        result.put("totalCalls", metrics.size());
        result.put("agents", rows);
        return result;
    }
}

