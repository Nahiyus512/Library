package com.wkc.library.controller;

import com.wkc.library.entity.R;
import com.wkc.library.service.agent.AgentMetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/agent/metrics")
@CrossOrigin
public class AgentMetricsController {

    @Autowired
    private AgentMetricService agentMetricService;

    @GetMapping("/summary")
    public R<Map<String, Object>> summary(@RequestParam(required = false) Integer days) {
        return R.success(agentMetricService.summary(days));
    }
}

