package com.fullstackcourse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
@ContextConfiguration(classes = SalutiWebServiceApplication.class)
class SalutiControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;
	
	@BeforeEach
	public void setup() {
		this.mockMvc = webAppContextSetup(wac).build();
	}
	
	@Test
	public void getSaluti() throws Exception {
		mockMvc.perform(get("/api/saluti")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").value("Ciao, questo è un saluto di benvenuto!"))
		.andDo(print());
	}
	
	@Test
	public void getSalutiWithUsernamePathVariable() throws Exception {
		mockMvc.perform(get("/api/saluti/Dario")
										.contentType(MediaType.APPLICATION_JSON))
										.andExpect(status().isOk())
										.andExpect(jsonPath("@").value("Ciao Dario, questo è un saluto di benvenuto!"))
										.andDo(print());
	}

}
