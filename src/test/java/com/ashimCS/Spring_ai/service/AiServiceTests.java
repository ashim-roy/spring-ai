package com.ashimCS.Spring_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AiServiceTests {

    @Autowired
    private AiService aiService;

    @Test
    public void testGetJoke() {
        // now we will unit test it
        String joke = aiService.getJoke("cat");
        assertNotNull(joke);
        assertFalse(joke.isEmpty());
        assertFalse(joke.isBlank());
        System.out.println( joke);
       /*
        // CS Anuj way:
        var jokes = aiService.getJoke("chickens");
        System.out.println(jokes);
        */
    }

    @Test
    public void testEmbedText() {
        float[] embed = aiService.getEmbedding("hello world");
        System.out.println("Embedding dimensions: " + embed.length);
        for(float e : embed){
            System.out.println(e+ " ");
        }

    }

    @Test
    public void testToStoreData() {
        aiService.ingestDataToVectorStore();
    }
}
