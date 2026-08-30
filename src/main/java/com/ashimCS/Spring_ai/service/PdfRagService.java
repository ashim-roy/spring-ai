package com.ashimCS.Spring_ai.service;

import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.core.io.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PdfRagService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @Value("classpath:faq.pdf")
    Resource pdfFile;

    public PdfRagService(ChatClient chatClient,
                         @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel,
                         VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    public void ingestPdfToVectorStore(){
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfFile);
        // read it to geta list of document
        List<Document> pages = pdfReader.get();
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()    // Old Spring AI API new TokenTextSplitter()
                .withChunkSize(200)
                .build();

        // use it to split our documents
        List<Document> chunks =  tokenTextSplitter.split(pages);
        vectorStore.add(chunks);
    }

    //RAG part
    public String askAi(String prompt) {

        // provide the context, will get from vector store and search on similarity
        // Take what is rag?, convert it into an embedding, search my vector database, and give me the 2 most relevant documents."
        // Step 1 — Search pgvector - performs embedding + vector search
        List<Document> documents =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(prompt)
                                .similarityThreshold(0.5)
                                .topK(2)
                                .filterExpression("file_name == 'faq.pdf'")
                                .build()
                );

        // converting document into String
        // Step 2 — Convert Documents into context
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // Step 3 — Create prompt template
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

        // // Step 4 — Inject context into prompt - converting this into prompt template
        PromptTemplate promptTemplate = new PromptTemplate(template);
        String systemPrompt = promptTemplate.render(Map.of("context", context));

        // Step 5 — Send context + question to the LLM
        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();

    }


}
