package com.alphashop.ControllerTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alphashop.entities.Article;
import com.alphashop.repositories.ArticleRepository;
import com.alphashop.test.BaseSpringIT;

class ArticleRestControllerCreateIT extends BaseSpringIT {
    
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
			  "barcode": [{
			      "barcode": "12345678",
			      "type": "CP"
			    }],
			  "ingredients": {
			    "codArt": "123Test",
			    "info": "TEST INGREDIENTI"
			  },
			  "vat": {
			    "idVat": 22
			  },
			  "famAssort": {
			    "id": 1
			  }
			}""";

	@Test
	public void testCreateArticle() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/article/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
		
		Article article = articleRepository.findByCodArt("123Test").get();
		assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
	}

	@Test
	public void testErrCreateArticle1() throws Exception {
		{
			mockMvc.perform(MockMvcRequestBuilders.post("/api/article/create")
					.contentType(MediaType.APPLICATION_JSON)
					.content(JsonData)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andDo(print());
			
			Article article = articleRepository.findByCodArt("123Test").get();
			assertThat(article.getDescription()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
		}
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/article/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value(409))
				.andExpect(jsonPath("$.message")
						.value("Article '123Test - ARTICOLO UNIT TEST INSERIMENTO' already exists"))
				.andDo(print());
	}

	String ErrJsonData = """
		{
			"codArt": "321Test",
			"description": "asd",
			"um": "PZ",
			"codStat": "TESTART",
			"pcsCart": 6,
			"netWeight": 1.75,
			"idArtStatus": "1",
			"creationDate": "2019-05-14",
			"barcode": [
			  {
				"barcode": "12345678",
				"type": "CP"
			  }
			],
			"ingredients": {
			  "codArt": "321Test",
			  "info": "TEST INGREDIENTI"
			},
			"vat": {
			  "idVat": 22
			},
			"famAssort": {
			  "id": 1
			}
		}""";

	@Test
	public void testErrCreateArticle2() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/article/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ErrJsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.code").value(422))
				.andExpect(jsonPath("$.message")
						.value("The Description field must have a number of characters between 6 and 80"))
				.andDo(print());
	}
}
