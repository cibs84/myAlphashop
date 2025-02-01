package test;

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

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public abstract class BaseSpringIT {
	
	@Autowired
	private WebApplicationContext wac;
	
	protected MockMvc mockMvc;

//	static GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:7"))
//	        .withExposedPorts(27017);
//
//    @DynamicPropertySource
//    static void containersProperties(DynamicPropertyRegistry registry) {
//        mongoDBContainer.start();
//    }
    
	@BeforeAll
    public static void setUp() {
    }
	
	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	@AfterEach
    public void tearDown() {
	}
}
