package com.wkc.library.assistant;

import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
//        chatModel = "qwenChatModel",
        streamingChatModel = "qwenStreamingChatModel",
        chatMemoryProvider = "chatMemoryProviderPolaris",
        tools = "selectBookTools",
        contentRetriever = "contentRetrieverPolarisPinecone"
)
public interface PolarisAgent {
    @SystemMessage(fromResource = "polaris-prompt-template.txt")
    Flux<String> chat(@MemoryId Long memoryId, @UserMessage String userMessage,@V("userId") String userId);
}
