package com.xantrix.webapp.tests.ControllerTests;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SelectArtTest {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@BeforeEach
	public void setup() throws JSONException, IOException {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	String JsonData = """
			{
			    "codArt": "002000301",
			    "descrizione": "ACQUA ULIVETO 15 LT",
			    "um": "PZ",
			    "codStat": "",
			    "pzCart": 6,
			    "pesoNetto": 1.5,
			    "idStatoArt": "1",
			    "dataCreazione": "2010-06-14",
			    "barcode": [
			        {
			            "barcode": "8008490000021",
			            "tipo": "CP"
			        }
			    ],
			    "famAssort": {
			        "id": 1,
			        "descrizione": "DROGHERIA ALIMENTARE"
			    },
			    "ingredienti": null,
			    "iva": {
			        "idIva": 22,
			        "descrizione": "IVA RIVENDITA 22%",
			        "aliquota": 22
			    }
			}""";

	@Test
	@Order(1)
	public void listArtByEan() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/articoli/cerca/barcode/8008490000021").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				// articoli
				.andExpect(jsonPath("$.codArt").exists()).andExpect(jsonPath("$.codArt").value("002000301"))
				.andExpect(jsonPath("$.descrizione").exists())
				.andExpect(jsonPath("$.descrizione").value("ACQUA ULIVETO 15 LT")).andExpect(jsonPath("$.um").exists())
				.andExpect(jsonPath("$.um").value("PZ")).andExpect(jsonPath("$.codStat").exists())
				.andExpect(jsonPath("$.codStat").value("")).andExpect(jsonPath("$.pzCart").exists())
				.andExpect(jsonPath("$.pzCart").value("6")).andExpect(jsonPath("$.pesoNetto").exists())
				.andExpect(jsonPath("$.pesoNetto").value("1.5")).andExpect(jsonPath("$.idStatoArt").exists())
				.andExpect(jsonPath("$.idStatoArt").value("1")).andExpect(jsonPath("$.dataCreazione").exists())
				.andExpect(jsonPath("$.dataCreazione").value("2010-06-14"))
				// barcode
				.andExpect(jsonPath("$.barcode[0].barcode").exists())
				.andExpect(jsonPath("$.barcode[0].barcode").value("8008490000021"))
				.andExpect(jsonPath("$.barcode[0].tipo").exists()).andExpect(jsonPath("$.barcode[0].tipo").value("CP"))
				// famAssort
				.andExpect(jsonPath("$.famAssort.id").exists()).andExpect(jsonPath("$.famAssort.id").value("1"))
				.andExpect(jsonPath("$.famAssort.descrizione").exists())
				.andExpect(jsonPath("$.famAssort.descrizione").value("DROGHERIA ALIMENTARE"))
				// ingredienti
				.andExpect(jsonPath("$.ingredienti").isEmpty())
				// Iva
				.andExpect(jsonPath("$.iva.idIva").exists()).andExpect(jsonPath("$.iva.idIva").value("22"))
				.andExpect(jsonPath("$.iva.descrizione").exists())
				.andExpect(jsonPath("$.iva.descrizione").value("IVA RIVENDITA 22%"))
				.andExpect(jsonPath("$.iva.aliquota").exists()).andExpect(jsonPath("$.iva.aliquota").value("22"))

				.andDo(print());
	}

	private String Barcode = "8008490002138";

	@Test
	@Order(2)
	public void ErrlistArtByEan() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/barcode/" + Barcode)
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("Il barcode " + Barcode + " non è stato trovato!"))
				.andDo(print());
	}

	@Test
	@Order(3)
	public void listArtByCodArt() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/articoli/cerca/codice/002000301").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(JsonData)).andReturn();
	}

	private String CodArt = "002000301b";

	@Test
	@Order(4)
	public void errlistArtByCodArt() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/codice/" + CodArt)
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("L'articolo con codice " + CodArt + " non è stato trovato!"))
				.andDo(print());
	}

	private String JsonData2 = """
			{
			  "pagination": {
			        "currentPage": 1,
			        "totalPages": 1,
			        "nextPage": 1,
			        "previousPage": 1,
			        "pageSize": 10,
			        "totalElements": 1
			    },
			  "itemList": [
			    {
			      "codArt": "002000301",
			      "descrizione": "ACQUA ULIVETO 15 LT",
			      "um": "PZ",
			      "codStat": "",
			      "pzCart": 6,
			      "pesoNetto": 1.5,
			      "idStatoArt": "1",
			      "dataCreazione": "2010-06-14",
			      "prezzo": 0.0,
			      "barcode": [
			        {
			          "barcode": "8008490000021",
			          "tipo": "CP"
			        }
			      ],
			      "ingredienti": null,
			      "famAssort": {
			        "id": 1,
			        "descrizione": "DROGHERIA ALIMENTARE"
			      },
			      "iva": {
			        "idIva": 22,
			        "descrizione": "IVA RIVENDITA 22%",
			        "aliquota": 22
			      }
			    }
			  ]
			}""";

	@Test
	@Order(5)
	public void listArtByDesc() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/descrizione/ACQUA ULIVETO 15 LT")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.itemList", hasSize(1)))
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(JsonData2)).andReturn();
	}
}