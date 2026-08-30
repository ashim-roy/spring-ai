package com.ashimCS.Spring_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
@Slf4j
public class AdvisorService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public AdvisorService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public String askAiWithAdvisors(String prompt, String userId){
        return chatClient.prompt()
                .system("""
                        "You are an Ai assistant called Marla, 
                        Greet users with your name (Marla) and the user name if you know know their name. 
                        Answer in a friendly conversational tone.
                        """)
                .user(prompt)
                .advisors(
                        VectorStoreChatMemoryAdvisor.builder(vectorStore)
                                .defaultTopK(4)
                                .build()

                )
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        userId
                ))
                .call()
                .content();
    }


}
