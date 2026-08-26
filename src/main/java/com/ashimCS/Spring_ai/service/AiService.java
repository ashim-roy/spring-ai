package com.ashimCS.Spring_ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;

    public String getJoke(String jokeTopic) {

        return chatClient.prompt()
                .user("Tell me a joke about " + jokeTopic)
                .call()
                .content();
    }

}
