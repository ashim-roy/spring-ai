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
        String response = advisorService.askAiWithAdvisors("my name is Ashim, can't view live class, what to do?", "ashim123");

    }
}
