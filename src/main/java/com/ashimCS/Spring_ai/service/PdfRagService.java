package com.ashimCS.Spring_ai.service;

import org.springframework.core.io.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;


import java.util.List;

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


}
