package com.ashimCS.Spring_ai.service;

import com.ashimCS.Spring_ai.dto.JokeDto;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;

@Service
@Slf4j
public class AiService {

    private final ChatClient chatClient;
    private final EmbeddingModel  embeddingModel;
    private final VectorStore  vectorStore;

        public AiService(  ChatClient chatClient,
            @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel,
                       VectorStore  vectorStore) {

        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    public float[] getEmbedding(String inputText){
        return  embeddingModel.embed(inputText); // returns an array of float
    }

    public void ingestDataToVectorStore(){
        List<Document> movies = List.of(
                new Document("A thief who steals corporate secrets through the use of dream-sharing technology.",
                        Map.of("title", "Inception", "genre", "Sci-Fi", "year", 2010)),

                new Document("A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                        Map.of("title", "Interstellar", "genre", "Sci-Fi", "year", 2014)),

                new Document("A poor yet passionate young man falls in love with a rich young woman, giving her a sense of freedom.",
                        Map.of("title", "The Notebook", "genre", "Romance", "year", 2004))
        );
        vectorStore.add(movies);

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
