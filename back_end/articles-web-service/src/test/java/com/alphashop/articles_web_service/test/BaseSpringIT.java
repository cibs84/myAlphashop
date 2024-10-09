package com.alphashop.articles_web_service.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alphashop.articles_web_service.Application;

import lombok.extern.java.Log;

@Log
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
public abstract class BaseSpringIT {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private IntegrationTestsNeeds integrationTestsNeeds;
    
    @BeforeAll
    public static void setUp() {
    }
    
    @BeforeEach
    public void beforeEachTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @AfterEach
    public void afterEachTest() {
        integrationTestsNeeds.deleteAllFromDb();
    }

    @AfterAll
    public static void tearDown() {

    }
}
