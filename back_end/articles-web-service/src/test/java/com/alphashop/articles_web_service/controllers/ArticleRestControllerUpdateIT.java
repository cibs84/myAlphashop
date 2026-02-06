package com.alphashop.articles_web_service.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.repositories.ArticleRepository;
import com.alphashop.articles_web_service.test.BaseSpringIT;

class ArticleRestControllerUpdateIT extends BaseSpringIT {
    
	@Autowired
	private ArticleRepository articleRepository;
	
	private String JsonData = """
	{
	    "codart": "123Test",
	    "description": "ARTICOLO UNIT TEST INSERIMENTO",
	    "codStat": "TESTART",
	    "price": 0,
	    "um": "PZ",
	    "pcsCart": 6,
	    "netWeight": 1.75,
	    "idArtStatus": 1,
	    "category": {
	        "id": 1
	    },
	    "vat": {
	        "idVat": 22
	    },
	    "ingredients": {
	        "codart": "123Test",
	        "info": "TEST INGREDIENTI"
	    },
	    "barcodes": [
	        {
	            "barcode": "12345678",
	            "idTypeArt": "CP"
	        }
	    ]
	}""";
	
	private String JsonDataMod = """
	{
	    "codart": "123Test",
	    "description": "ARTICOLO UNIT TEST MODIFICA",
	    "codStat": "TESTART",
	    "price": 0,
	    "um": "PZ",
	    "pcsCart": 6,
	    "netWeight": 1.75,
	    "idArtStatus": 1,
	    "category": {
	        "id": 1
	    },
	    "vat": {
	        "idVat": 22
	    },
	    "ingredients": {
	        "codart": "123Test",
	        "info": "TEST INGREDIENTI"
	    },
	    "barcodes": [
	        {
	            "barcode": "12345678",
	            "idTypeArt": "CP"
	        }
	    ]
	}""";
	
	// TODO Delete it and replace it with JsonDataMod when the price
	// will be introduced in response (now the price in response is null).
	private String JsonDataModResponse = """
	{
	    "codart": "123Test",
	    "description": "ARTICOLO UNIT TEST MODIFICA",
	    "codStat": "TESTART",
	    "um": "PZ",
	    "pcsCart": 6,
	    "netWeight": 1.75,
	    "idArtStatus": 1,
	    "category": {
	        "id": 1
	    },
	    "vat": {
	        "idVat": 22
	    },
	    "ingredients": {
	        "codart": "123Test",
	        "info": "TEST INGREDIENTI"
	    },
	    "barcodes": [
	        {
	            "barcode": "12345678",
	            "idTypeArt": "CP"
	        }
	    ]
	}""";

	@Test
	public void testUpdArticle() throws Exception {
		
		String codart = "123Test"; 
		
		// Creating article '123Test' 
		{
			mockMvc.perform(MockMvcRequestBuilders.post("/api/articles")
					.contentType(MediaType.APPLICATION_JSON)
					.content(JsonData)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andDo(print());
			
			Article article = articleRepository.findByCodart(codart).get();
			assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
		}
		// Updating article '123Test'
		mockMvc.perform(MockMvcRequestBuilders.put("/api/articles/" + codart)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonDataMod).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(JsonDataModResponse))
				.andReturn();
		
		Article article = articleRepository.findByCodart("123Test").get();
		assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST MODIFICA");
	}
	
	@Test
	public void testErrUpdArticleNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/articles/inexistent-codart")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonDataMod).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.code").value("ITEM_NOT_FOUND"))
				.andDo(print());
	}
}
