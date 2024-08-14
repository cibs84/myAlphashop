package com.alphashop.test;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.alphashop.repositories.ArticleRepository;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public abstract class BaseSpringIT {
	
	protected MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private IntegrationTestsNeeds integrationTestsNeeds;
	
//	private static Flyway flyway;
//	
//	@BeforeAll
//    public static void setUp() {
//        flyway = Flyway.configure()
//                .dataSource("jdbc:tc:postgresql:16:///alphashop_test?TC_REUSABLE=true", "postgres", "pass123")
//                .load();
//        flyway.migrate();
//    }
	
	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	@AfterEach
    public void tearDown() {
		integrationTestsNeeds.deleteAllFromDb();
	}
}
