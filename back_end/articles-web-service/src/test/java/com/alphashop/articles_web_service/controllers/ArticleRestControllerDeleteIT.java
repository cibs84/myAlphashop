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
	  "um": "PZ",
	  "codStat": "TESTART",
	  "pcsCart": 6,
	  "netWeight": 1.75,
	  "idArtStatus": "1",
	  "creationDate": "2019-05-14",
	  "barcodes": [{
	      "barcode": "12345678",
	      "type": "CP"
	    }],
	  "ingredients": {
	    "codart": "123Test",
	    "info": "TEST INGREDIENTI"
	  },
	  "category": {
	    "id": 1,
        "description": "DROGHERIA ALIMENTARE"
	  },
      "vat": {
        "idVat": 22,
        "description": "IVA RIVENDITA 22%",
        "taxRate": 22
      }
	}""";
	
	private String JsonData02 = """
	{
	  "codart": "abcTest",
	  "description": "ARTICOLO UNIT TEST INSERIMENTO",
	  "um": "PZ",
	  "codStat": "TESTART",
	  "pcsCart": 6,
	  "netWeight": 1.75,
	  "idArtStatus": "1",
	  "creationDate": "2019-05-14",
	  "barcodes": [{
	      "barcode": "12345678",
	      "type": "CP"
	    }],
	  "ingredients": {
	    "codart": "abcTest",
	    "info": "TEST INGREDIENTI"
	  },
	  "category": {
	    "id": 1,
        "description": "DROGHERIA ALIMENTARE"
	  },
      "vat": {
        "idVat": 22,
        "description": "IVA RIVENDITA 22%",
        "taxRate": 22
      }
	}""";

	
	@Test
	public void testDelArticle() throws Exception {
	{
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articles/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
		
		Article article = articleRepository.findByCodart("123Test").get();
		assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
	}
	
	    Long articoloInitCount = articleRepository.count();
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articles/delete/123Test")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.status").value("200 OK"))
		.andExpect(jsonPath("$.message")
				.value("Deleting article '123Test - ARTICOLO UNIT TEST INSERIMENTO' performed successfully"))
		.andDo(print());
	
	    Assertions.assertThat(articleRepository.count()).isEqualTo(articoloInitCount - 1);
	}
	
	@Test
	public void testDelArticleNotDeletable() throws Exception {
	{
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articles/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData02)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
		
		Article article = articleRepository.findByCodart("abcTest").get();
		assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
	}
		
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articles/delete/abcTest")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isForbidden())
		.andExpect(jsonPath("$.status").value(403))
		.andExpect(jsonPath("$.message")
				.value("Article 'abcTest' not deletable"))
		.andDo(print());
	}
	
	@Test
	public void testDelArticleNotFound() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articles/delete/xxxx")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message")
						.value("Article to be deleted 'xxxx' was not found"))
				.andDo(print());
	}
}
