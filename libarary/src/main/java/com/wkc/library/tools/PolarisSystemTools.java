package com.wkc.library.tools;

import com.wkc.library.service.agent.AgentMetricService;
import com.wkc.library.service.agent.PolarisAgentRouteResult;
import com.wkc.library.service.agent.PolarisAgentRouterService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PolarisSystemTools {

    @Autowired
    private PolarisAgentRouterService routerService;
    @Autowired
    private AgentMetricService agentMetricService;

    @Tool(
            name = "featuredPageRecommendAgent",
            value = "Recommend featured pages/books by in-system page analytics score. Only call when user asks for featured recommendations."
    )
    public String featuredPageRecommendAgent(Integer userId, Long memoryId, boolean nextBatch) {
        PolarisAgentRouteResult result = routerService.executeFeaturedRecommendAgent(
                userId, memoryId, "tool:featuredPageRecommendAgent", nextBatch
        );
        return result.getResponse();
    }

    @Tool(
            name = "systemCatalogSearchAgent",
            value = "Search system catalog and return bookName, bookAuthor, bookClassify. Call only when catalog data is needed."
    )
    public Map<String, Object> systemCatalogSearchAgent(String keyword, Integer limit) {
        long start = System.currentTimeMillis();
        try {
            Map<String, Object> result = routerService.systemCatalogForTool(keyword, limit);
            agentMetricService.record("system_catalog_search_agent", null, null,
                    "tool:systemCatalogSearchAgent", true, System.currentTimeMillis() - start, "tool", null);
            return result;
        } catch (Exception e) {
            agentMetricService.record("system_catalog_search_agent", null, null,
                    "tool:systemCatalogSearchAgent", false, System.currentTimeMillis() - start, "tool", e.getMessage());
            throw e;
        }
    }

    @Tool(
            name = "userDataQueryAgent",
            value = "Query user likes and scores from book_like and book_score. Call only when user-specific data is required."
    )
    public Map<String, Object> userDataQueryAgent(Integer userId, Integer limit) {
        long start = System.currentTimeMillis();
        try {
            Map<String, Object> result = routerService.userDataForTool(userId, limit);
            agentMetricService.record("user_data_query_agent", userId, null,
                    "tool:userDataQueryAgent", true, System.currentTimeMillis() - start, "tool", null);
            return result;
        } catch (Exception e) {
            agentMetricService.record("user_data_query_agent", userId, null,
                    "tool:userDataQueryAgent", false, System.currentTimeMillis() - start, "tool", e.getMessage());
            throw e;
        }
    }

    @Tool(
            name = "preferenceManageAgent",
            value = "Adjust user preference (likeLevel 0/1/2) for a given bookId or bookName."
    )
    public String preferenceManageAgent(Integer userId, String userName, Integer bookId, String bookName, Integer likeLevel) {
        PolarisAgentRouteResult result = routerService.executePreferenceManageAgent(
                userName, userId, null, "tool:preferenceManageAgent", bookId, bookName, likeLevel
        );
        return result.getResponse();
    }

    @Tool(
            name = "scoreManageAgent",
            value = "Set user score (1-5) for a given bookId or bookName."
    )
    public String scoreManageAgent(Integer userId, String userName, Integer bookId, String bookName, Integer score) {
        PolarisAgentRouteResult result = routerService.executeScoreManageAgent(
                userName, userId, null, "tool:scoreManageAgent", bookId, bookName, score
        );
        return result.getResponse();
    }

    @Tool(
            name = "missingBookAdviceAgent",
            value = "Submit missing-book advice. Supports existing bookId or missing bookName."
    )
    public String missingBookAdviceAgent(String userName, Integer userId, Integer bookId, String bookName, String adviceText) {
        PolarisAgentRouteResult result = routerService.executeMissingBookAdviceAgent(
                userName, userId, null, "tool:missingBookAdviceAgent", bookId, bookName, adviceText
        );
        return result.getResponse();
    }

    @Tool(
            name = "feedbackAdviceAgent",
            value = "Submit generic feedback advice into advice table without bookId."
    )
    public String feedbackAdviceAgent(String userName, Integer userId, String adviceText) {
        PolarisAgentRouteResult result = routerService.executeFeedbackAdviceAgent(
                userName, userId, null, "tool:feedbackAdviceAgent", adviceText
        );
        return result.getResponse();
    }
}
