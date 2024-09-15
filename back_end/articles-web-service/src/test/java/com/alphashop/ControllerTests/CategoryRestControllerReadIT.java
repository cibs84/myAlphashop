package com.alphashop.ControllerTests;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alphashop.test.BaseSpringIT;

public class CategoryRestControllerReadIT extends BaseSpringIT {

	@Test
	public void listAllCategories() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/find/all")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(13)))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andReturn();
	}

}