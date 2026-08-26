package com.ashimCS.Spring_ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {  //Spring, give me a ChatClient.Builder
        return builder.build();
    }
}

/*
Spring, this is a configuration class. Create a ChatClient bean for me. To create it,
give me the ChatClient.Builder that Spring AI has configured. I'll call build() on that builder,
and whatever ChatClient it produces should become a Spring bean."
Builder object
     ↓
 build()
     ↓
ChatClient object
 */
