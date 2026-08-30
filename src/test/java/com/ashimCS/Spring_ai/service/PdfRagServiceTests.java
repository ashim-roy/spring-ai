package com.ashimCS.Spring_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PdfRagServiceTests {


    @Autowired
    private PdfRagService pdfRagService;

    @Test
    public void testIngest(){ // works offline, run once
        pdfRagService.ingestPdfToVectorStore();
    }


    @Test
    public void testAskAi() {
        var response = pdfRagService.askAi("live class is lagging");
        System.out.println(response);

    }

}
