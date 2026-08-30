package com.ashimCS.Spring_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RagServiceTests {

    @Autowired
    private RagService ragService;

    @Test
    public void testAskAi() {
        var response = ragService.askAi("what is spring Ai?");
        System.out.println(response);

    }

    @Test
    public void testToStoreData() {
        ragService.ingestDataToVectorStore();
    }



    @Test
    public void testAskToAi() {
        var response = ragService.askToAi("what is appple?");
        System.out.println(response);

    }


}
