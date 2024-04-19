package com.xantrix.webapp.tests.ControllerTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class InsertArtTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	ArticoliRepository articoliRepository;

	@BeforeEach
	public void setup() throws JSONException, IOException {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	private String JsonData = """
			{
			  "codArt": "123Test",
			  "descrizione": "Articolo Unit Test Inserimento",
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
	@Order(1)
	public void testInsArticolo() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articoli/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
		
		Articoli articolo = articoliRepository.findByCodArt("123Test").get();
		assertThat(articolo.getDescrizione()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
	}

	@Test
	@Order(2)
	public void testErrInsArticolo1() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articoli/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$.code").value(406))
				.andExpect(jsonPath("$.message")
						.value("Articolo 123Test presente in anagrafica! Impossibile utilizzare il metodo POST"))
				.andDo(print());
	}

	String ErrJsonData = """
		{
			"codArt": "123Test",
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
	@Order(3)
	public void testErrInsArticolo2() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articoli/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ErrJsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.message")
						.value("Il campo Descrizione deve avere un numero di caratteri compreso tra 6 e 80"))
				.andDo(print());
	}

	private String JsonDataMod = """
		{
			"codArt": "123Test",
			"descrizione": "Articolo Unit Test Modifica",
			"um": "PZ",
			"codStat": "TESTART",
			"pzCart": 6,
			"pesoNetto": 1.75,
			"idStatoArt": "1",
			"dataCreaz": "2019-05-14",
			"barcode": [
			  {
				"barcode": "12345678",
				"tipo": "CP"
			  }
			],
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
	@Order(4)
	public void testUpdArticolo() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/articoli/modifica")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonDataMod).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
		
		Articoli articolo = articoliRepository.findByCodArt("123Test").get();
		assertThat(articolo.getDescrizione()).isEqualTo("ARTICOLO UNIT TEST MODIFICA");
	}

	@Test
	@Order(5)
	public void testDelArticolo() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articoli/elimina/123Test")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("200 OK"))
				.andExpect(jsonPath("$.message")
						.value("Eliminazione Articolo 123Test Eseguita Con Successo"))
				.andDo(print());
	}

}
