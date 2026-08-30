package com.ashimCS.Spring_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RagService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;   //vectorStore internally uses embedding model

    public RagService(ChatClient chatClient,
                      @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel,
                      VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    public String askAi(String prompt){
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();

    }

    // 1- Define the instructions for the LLM
    // You don't know the context yet. You will retrieve it from pgvector in the next step.
    public String askToAi(String prompt) {
        String template = """
                You are an AI assistant helping a developer.
                
                Rules:
                1. Use only the information provided in the context.
                2. You MAY rephrase, summerize, and explain in natural language
                3. Do not introduce new concept or facts
                4. If multiple context sections are relavant, combine them into a single explaination.
                5. If the answer is not present, say "I dont know"
                
                context:
                {context}
                
                Answer in a friendly, conversational tone.
                """;

        // provide the context, will get from vector store and search on similarity
        // Take what is rag?, convert it into an embedding, search my vector database, and give me the 2 most relevant documents."
        // Step 2 — Search pgvector - performs embedding + vector search
        List<Document> documents =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(prompt)
                                .similarityThreshold(0.5)
                                .topK(2)
                                .filterExpression(
                                        "topic == 'ai' or topic == 'vectorstore' or topic == 'rag'"
                                )
                                .build()
                );

        // converting document into String
        // Step 3 — Convert Documents into context
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // converting this into prompt template
        PromptTemplate promptTemplate = new PromptTemplate(template);
        //  Step 4 — Put the context into the prompt
        String systemPrompt = promptTemplate.render(Map.of("context", context));

        // Step 5 — Send context + question to the LLM
        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();

    }

    public void ingestDataToVectorStore(){
        vectorStore.add(springAiDocs());
    }


    public static List<Document> springAiDocs() {
        return List.of(
                new Document(
                        "Spring AI provides abstractions like ChatClient, ChatModel, and EmbeddingModel to interact with Large Language Models (LLMs).",
                        Map.of("topic", "ai", "concept", "spring-ai")
                ),
                new Document(
                        "ChatClient provides a fluent API for sending prompts to language models such as OpenAI, Ollama, and Google Gemini.",
                        Map.of("topic", "ai", "concept", "chatclient")
                ),
                new Document(
                        "ChatModel is an abstraction that allows an application to communicate with different LLM providers without tightly coupling the application to a specific provider.",
                        Map.of("topic", "ai", "concept", "chatmodel")
                ),
                new Document(
                        "EmbeddingModel converts text into numerical vector representations called embeddings. These vectors capture the semantic meaning of the text.",
                        Map.of("topic", "ai", "concept", "embeddings")
                ),
                new Document(
                        "VectorStore is used to persist embeddings and perform similarity searches for Retrieval-Augmented Generation (RAG).",
                        Map.of("topic", "vectorstore", "concept", "similarity-search")
                ),
                new Document(
                        "PgVectorStore stores embeddings inside PostgreSQL using the pgvector extension, allowing PostgreSQL to perform vector similarity searches.",
                        Map.of("topic", "vectorstore", "concept", "pgvector")
                ),
                new Document(
                        "Vector similarity search converts a user query into an embedding and finds stored documents whose embeddings are semantically similar to the query.",
                        Map.of("topic", "vectorstore", "concept", "similarity-search")
                ),
                new Document(
                        "Retrieval-Augmented Generation combines vector similarity search with prompt augmentation. Relevant documents are retrieved and provided to the LLM as context to improve the generated answer.",
                        Map.of("topic", "rag", "concept", "retrieval-augmented-generation")
                ),
                new Document(
                        "RAG can help reduce hallucinations by grounding an LLM response in relevant information retrieved from an application's own data.",
                        Map.of("topic", "rag", "concept", "grounding")
                ),
                new Document(
                        "Spring AI Advisors allow developers to intercept and process AI requests and responses. Advisors can be used for logging, prompt modification, memory, adding context, and other cross-cutting concerns.",
                        Map.of("topic", "ai", "concept", "advisors")
                ),
                new Document(
                        "SimpleLoggerAdvisor is a Spring AI advisor that helps log and inspect requests sent to the LLM and responses received from the LLM.",
                        Map.of("topic", "ai", "concept", "logging")
                ),
                new Document(
                        "Structured output allows Spring AI to convert an LLM response into a Java object instead of returning only plain text.",
                        Map.of("topic", "ai", "concept", "structured-output")
                ),
                new Document(
                        "Tool Calling allows an LLM to determine when it needs an external function or tool. The application executes the tool, obtains the result, and provides the result back to the LLM.",
                        Map.of("topic", "ai", "concept", "tool-calling")
                )
        );
    }



}
