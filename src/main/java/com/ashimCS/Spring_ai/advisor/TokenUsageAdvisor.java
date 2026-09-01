package com.ashimCS.Spring_ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.api.OllamaApi;

@Slf4j
public class TokenUsageAdvisor implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        long startTime = System.currentTimeMillis();

        // 1. Pass the request down th chain(to the LLM)
        ChatClientResponse advisedResponse = callAdvisorChain.nextCall(chatClientRequest);

        //2. Extract the actual LLM response
        ChatResponse chatResponse = advisedResponse.chatResponse();

        // 3. inspect usage metadata
        if(chatResponse != null && chatResponse.getMetadata().getUsage() != null) {
            var usage = chatResponse.getMetadata().getUsage();
            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "========== TOKEN USAGE ==========\n" +
                            "Input Tokens  : {}\n" +
                            "Output Tokens : {}\n" +
                            "Total Tokens  : {}\n" +
                            "Time          : {} ms",
                    usage.getPromptTokens(),
                    usage.getCompletionTokens(),
                    usage.getTotalTokens(),
                    duration
            );

            // make a DB call to store token count
        }

        return advisedResponse;
    }

    @Override
    public String getName() {
        return "TokenUsageAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
