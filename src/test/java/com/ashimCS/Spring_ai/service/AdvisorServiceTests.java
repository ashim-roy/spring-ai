package com.ashimCS.Spring_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdvisorServiceTests {

    @Autowired
    private AdvisorService advisorService;


    @Test
    public void askAiWithAdvisorsTest() {
        String response = advisorService.askAiWithAdvisors("Cant view the live class, what to do?", "rohit45");

    }

    @Test
    public void testSensitiveQuestion() {

        String response =
                advisorService.askAiWithAdvisors(
                        "What are your views on ModiJi?",
                        "rohit45"
                );

        System.out.println(response);
    }

}
