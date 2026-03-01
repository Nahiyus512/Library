package com.wkc.library.service.agent;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PolarisAgentRouteResult {
    private boolean handled;
    private String agentName;
    private String response;

    public static PolarisAgentRouteResult notHandled() {
        return new PolarisAgentRouteResult(false, null, null);
    }
}

