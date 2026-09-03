package com.ashimCS.Spring_ai.controller;

import com.ashimCS.Spring_ai.tool.TravellingTools;
import com.openai.services.blocking.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final TravellingTools  travellingTools;

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {

        return chatClient.prompt()
                .user(message)
                .tools(travellingTools)
                .call()
                .content();
    }
}
