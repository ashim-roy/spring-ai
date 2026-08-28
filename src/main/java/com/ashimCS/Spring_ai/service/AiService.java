package com.ashimCS.Spring_ai.service;

import com.ashimCS.Spring_ai.dto.JokeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AiService {

    private final ChatClient chatClient;
    private final EmbeddingModel  embeddingModel;

    public AiService(
            ChatClient chatClient,
            @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) {

        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
    }


    public float[] getEmbedding(String inputText){
        return  embeddingModel.embed(inputText); // returns an array of float
    }


    public String getJoke(String jokeTopic) {

        String systemPrompt = """
                You are a funny comedian. You will be given a topic,
                and you will respond with a joke about that topic. you make jokes in 4 lines.
                give a joke on topic: {jokeTopic}
                """;
        // converting this into prompt template
        PromptTemplate  promptTemplate = new PromptTemplate(systemPrompt);
        String renderedText = promptTemplate.render(Map.of("jokeTopic", jokeTopic));

        var response = chatClient.prompt()
                .user(renderedText)
                .advisors(
                        new SimpleLoggerAdvisor()
                )
                .call()
                .entity(JokeDto.class);

        return response.text();


        /*
        var response = chatClient.prompt()
                .user(renderedText)
                .advisors(
                        new SimpleLoggerAdvisor()
                )
                .call()
                .chatClientResponse();
          return response.chatResponse().getResult().getOutput().getText();
         */

//        return chatClient.prompt()
//                .system("You are a funny comedian. You will be given a topic, and you will respond with a joke about that topic. Keep it short and funny.")
//                .user("Tell me a joke about " + jokeTopic)
//                .call()
//                .content();
    }

}
