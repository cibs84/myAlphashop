package com.alphashop.articles_web_service.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alphashop.articles_web_service.Application;

import lombok.extern.java.Log;

@Log
@ActiveProfiles("test")
@AutoConfigureMockMvc
//@Testcontainers
//@ContextConfiguration(initializers = {AlphashopPostgreSqlContainer.Initializer.class})
@SpringBootTest(classes = Application.class)
public abstract class BaseSpringIT {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private IntegrationTestsNeeds integrationTestsNeeds;
    
    static {
    	Properties properties = new Properties();
        try {
			properties.load(new FileInputStream("src/test/resources/application-test.properties"));
		} catch (IOException e) {
			log.warning(e.getMessage());
		}
        
        String url = properties.getProperty("spring.datasource.url");
        String user= properties.getProperty("spring.datasource.user");
        String password= properties.getProperty("spring.datasource.password");
    	
    	Flyway.configure()
		  	  .dataSource(url, user, password)
		      .load().migrate();
    }
    
    @BeforeAll
    public static void setUp() {
//    	Properties properties = new Properties();
//        properties.load(new FileInputStream("src/test/resources/application-test.properties"));
//        
//        String url = properties.getProperty("spring.datasource.url");
//        String user= properties.getProperty("spring.datasource.user");
//        String password= properties.getProperty("spring.datasource.password");
//    	
//    	Flyway.configure()
//		  	  .dataSource(url, user, password)
//		      .load().migrate();
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
