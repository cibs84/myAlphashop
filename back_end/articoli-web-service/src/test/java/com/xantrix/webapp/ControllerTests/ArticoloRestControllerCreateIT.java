package com.xantrix.webapp.ControllerTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.xantrix.webapp.entities.Articolo;
import com.xantrix.webapp.repository.ArticoloRepository;
import com.xantrix.webapp.test.BaseSpringIT;

class ArticoloRestControllerCreateIT extends BaseSpringIT {
    
	@Autowired
	private ArticoloRepository articoloRepository;
	
	private String JsonData = """
			{
			  "codArt": "123Test",
			  "descrizione": "ARTICOLO UNIT TEST INSERIMENTO",
			  "um": "PZ",
			  "codStat": "TESTART",
			  "pzCart": 6,
			  "pesoNetto": 1.75,
			  "idStatoArt": "1",
			  "dataCreazione": "2019-05-14",
			  "barcode": [{
			      "barcode": "12345678",
			      "tipo": "CP"
			    }],
			  "ingredienti": {
			    "codArt": "123Test",
			    "info": "TEST INGREDIENTI"
			  },
			  "iva": {
			    "idIva": 22
			  },
			  "famAssort": {
			    "id": 1
			  }
			}""";

	@Test
	public void testInsArticolo() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articolo/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
		
		Articolo articolo = articoloRepository.findByCodArt("123Test").get();
		assertThat(articolo.getDescrizione()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
	}

	@Test
	public void testErrInsArticolo1() throws Exception {
		{
			mockMvc.perform(MockMvcRequestBuilders.post("/api/articolo/inserisci")
					.contentType(MediaType.APPLICATION_JSON)
					.content(JsonData)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andDo(print());
			
			Articolo articolo = articoloRepository.findByCodArt("123Test").get();
			assertThat(articolo.getDescrizione()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
		}
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articolo/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value(409))
				.andExpect(jsonPath("$.message")
						.value("Articolo '123Test - ARTICOLO UNIT TEST INSERIMENTO' già presente in anagrafica"))
				.andDo(print());
	}

	String ErrJsonData = """
		{
			"codArt": "321Test",
			"descrizione": "asd",
			"um": "PZ",
			"codStat": "TESTART",
			"pzCart": 6,
			"pesoNetto": 1.75,
			"idStatoArt": "1",
			"dataCreazione": "2019-05-14",
			"barcode": [
			  {
				"barcode": "12345678",
				"tipo": "CP"
			  }
			],
			"ingredienti": {
			  "codArt": "321Test",
			  "info": "TEST INGREDIENTI"
			},
			"iva": {
			  "idIva": 22
			},
			"famAssort": {
			  "id": 1
			}
		}""";

	@Test
	public void testErrInsArticolo2() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articolo/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ErrJsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.code").value(422))
				.andExpect(jsonPath("$.message")
						.value("Il campo Descrizione deve avere un numero di caratteri compreso tra 6 e 80"))
				.andDo(print());
	}
}
