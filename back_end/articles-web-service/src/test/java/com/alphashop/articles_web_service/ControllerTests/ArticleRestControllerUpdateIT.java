package com.alphashop.articles_web_service.ControllerTests;

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
	  "codArt": "123Test",
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
	    "codArt": "123Test",
	    "info": "TEST INGREDIENTI"
	  },
	  "vat": {
		"idVat": 22,
		"description": "IVA RIVENDITA 22%",
		"taxRate": 22
	  },
	  "category": {
		"id": 1,
		"description": "DROGHERIA ALIMENTARE"
	  }
	}""";
	
	private String JsonDataMod = """
	{
	  "codArt": "123Test",
	  "description": "ARTICOLO UNIT TEST MODIFICA",
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
	    "codArt": "123Test",
	    "info": "TEST INGREDIENTI"
	  },
	  "vat": {
		"idVat": 22,
		"description": "IVA RIVENDITA 22%",
		"taxRate": 22
	  },
	  "category": {
		"id": 1,
		"description": "DROGHERIA ALIMENTARE"
	  }
	}""";

	@Test
	public void testUpdArticle() throws Exception {
		{
			mockMvc.perform(MockMvcRequestBuilders.post("/api/articles/create")
					.contentType(MediaType.APPLICATION_JSON)
					.content(JsonData)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andDo(print());
			
			Article article = articleRepository.findByCodArt("123Test").get();
			assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
		}
		
		mockMvc.perform(MockMvcRequestBuilders.put("/api/articles/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonDataMod).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(JsonDataMod))
				.andReturn();
		
		Article article = articleRepository.findByCodArt("123Test").get();
		assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST MODIFICA");
	}
	
	@Test
	public void testErrUpdArticleNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/articles/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonDataMod).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message")
						.value("Article '123Test' doesn't exist"))
				.andDo(print());
	}
}
