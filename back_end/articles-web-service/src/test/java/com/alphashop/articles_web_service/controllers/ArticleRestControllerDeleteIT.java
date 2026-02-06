package com.alphashop.articles_web_service.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.repositories.ArticleRepository;
import com.alphashop.articles_web_service.test.BaseSpringIT;

class ArticleRestControllerDeleteIT extends BaseSpringIT {
    
	@Autowired
	private ArticleRepository articleRepository;
	
	@Value("codartArtNotDeletable")
	private String codartArtNotDeletable;
	
	private String JsonData = """
	{
	    "codart": "123Test",
	    "description": "ARTICOLO UNIT TEST INSERIMENTO",
	    "price": 0,
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
	
	private String JsonData02 = """
	{
	    "codart": "abcTest",
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
	        "codart": "abcTest",
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
	public void testDelArticle() throws Exception {
		
	String codart = "123Test";
		
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
	
	    Long articoloInitCount = articleRepository.count();
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articles/" + codart)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent())
		.andDo(print());
	
	    Assertions.assertThat(articleRepository.count()).isEqualTo(articoloInitCount - 1);
	}
	
	@Test
	public void testDelArticleNotDeletable() throws Exception {
		
	String notDeletableCodart = "abcTest";
		
	{
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData02)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(print())
				.andExpect(status().isCreated())
				.andDo(print());
		
		Article article = articleRepository.findByCodart(notDeletableCodart).get();
		assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
	}
		
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articles/" + notDeletableCodart)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isForbidden())
		.andExpect(jsonPath("$.status").value(403))
		.andExpect(jsonPath("$.code").value("NOT_DELETABLE"))
		.andDo(print());
	}
	
	@Test
	public void testDelArticleNotFound() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articles/inexistent-codart")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.code").value("ITEM_NOT_FOUND"))
				.andDo(print());
	}
}
