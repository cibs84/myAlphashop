package com.alphashop.articles_web_service.controllers;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alphashop.articles_web_service.test.BaseSpringIT;

public class VatRestControllerReadIT extends BaseSpringIT {

	@Test
	public void listAllCategories() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/vat/find/all")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(4)))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andReturn();
	}

}