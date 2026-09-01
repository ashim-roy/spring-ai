package com.ashimCS.Spring_ai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;

import java.util.List;

public class CustomSafeGuardAdvisor implements CallAdvisor {

    private static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE;
    private final List<String> sensitiveWords;
    private final String failureResponse;

    public CustomSafeGuardAdvisor(List<String> sensitiveWords) {
        this(
                sensitiveWords,
                "I'm unable to respond to that due to sensitive content."
        );
    }

    public CustomSafeGuardAdvisor( List<String> sensitiveWords, String failureResponse ) {
        this.sensitiveWords = sensitiveWords;
        this.failureResponse = failureResponse;
    }

    @Override
    public ChatClientResponse adviseCall(
            ChatClientRequest request,
            CallAdvisorChain chain
    ) {

        // Get the user's prompt
        String userPrompt = request.prompt()
                .getUserMessage()
                .getText();

        // Check for sensitive words
        for (String sensitiveWord : sensitiveWords) {

            if (userPrompt.contains(sensitiveWord)) {

                // Block the request
                throw new IllegalArgumentException(
                        failureResponse
                );
            }
        }

        // Nothing sensitive found.
        // Continue to the next advisor.
        return chain.nextCall(request);
    }

    @Override
    public String getName() {
        return "CustomSafeGuardAdvisor";
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
}
