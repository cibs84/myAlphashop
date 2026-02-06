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

class ArticleRestControllerCreateIT extends BaseSpringIT {

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

	// TODO Delete it and replace it with JsonData when the price
	// will be introduced in response (now the price in response is null).
	private String JsonDataResponse = """
			{
			    "codart": "123Test",
			    "description": "ARTICOLO UNIT TEST INSERIMENTO",
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
	public void testCreateArticle() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articles").contentType(MediaType.APPLICATION_JSON)
				.content(JsonData).accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(content().json(JsonDataResponse)).andReturn();

		Article article = articleRepository.findByCodart("123Test").get();
		assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
	}

	@Test
	public void testErrCreateExistingArticle() throws Exception {
		{
			mockMvc.perform(MockMvcRequestBuilders.post("/api/articles").contentType(MediaType.APPLICATION_JSON)
					.content(JsonData).accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
					.andDo(print());

			Article article = articleRepository.findByCodart("123Test").get();
			assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
		}

		mockMvc.perform(MockMvcRequestBuilders.post("/api/articles").contentType(MediaType.APPLICATION_JSON)
				.content(JsonData).accept(MediaType.APPLICATION_JSON)).andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409)).andExpect(jsonPath("$.code").value("ITEM_ALREADY_EXISTS"))
				.andDo(print());
	}

	String ErrJsonData = """
			{
			    "codart": "123Test",
			    "description": "asd",
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

	@Test
	public void testErrCreateArticleWithInvalidDescription() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articles").contentType(MediaType.APPLICATION_JSON)
				.content(ErrJsonData).accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422)).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.errorValidationMap.description[0]").value("MIN_LENGTH")).andDo(print());
	}
}
