package com.alphashop.ControllerTests;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alphashop.test.BaseSpringIT;

public class ArticleRestControllerReadIT extends BaseSpringIT {
	
	String JsonData = """
			{
			    "codArt": "002000301",
			    "description": "ACQUA ULIVETO 15 LT",
			    "um": "PZ",
			    "codStat": "",
			    "pzCart": 6,
			    "netWeight": 1.5,
			    "idArtStatus": "1",
			    "creationDate": "2010-06-14",
			    "barcode": [
			        {
			            "barcode": "8008490000021",
			            "type": "CP"
			        }
			    ],
			    "famAssort": {
			        "id": 1,
			        "description": "DROGHERIA ALIMENTARE"
			    },
			    "ingredients": null,
			    "vat": {
			        "idVat": 22,
			        "description": "IVA RIVENDITA 22%",
			        "taxRate": 22
			    }
			}""";

	@Test
	@Order(1)
	public void listArtByEan() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/article/findByBarcode/8008490000021").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				// articolo
				.andExpect(jsonPath("$.codArt").exists()).andExpect(jsonPath("$.codArt").value("002000301"))
				.andExpect(jsonPath("$.description").exists())
				.andExpect(jsonPath("$.description").value("ACQUA ULIVETO 15 LT")).andExpect(jsonPath("$.um").exists())
				.andExpect(jsonPath("$.um").value("PZ")).andExpect(jsonPath("$.codStat").exists())
				.andExpect(jsonPath("$.codStat").value("")).andExpect(jsonPath("$.pzCart").exists())
				.andExpect(jsonPath("$.pzCart").value("6")).andExpect(jsonPath("$.netWeight").exists())
				.andExpect(jsonPath("$.netWeight").value("1.5")).andExpect(jsonPath("$.idArtStatus").exists())
				.andExpect(jsonPath("$.idArtStatus").value("1")).andExpect(jsonPath("$.creationDate").exists())
				.andExpect(jsonPath("$.creationDate").value("2010-06-14"))
				// barcode
				.andExpect(jsonPath("$.barcode[0].barcode").exists())
				.andExpect(jsonPath("$.barcode[0].barcode").value("8008490000021"))
				.andExpect(jsonPath("$.barcode[0].type").exists()).andExpect(jsonPath("$.barcode[0].type").value("CP"))
				// famAssort
				.andExpect(jsonPath("$.famAssort.id").exists()).andExpect(jsonPath("$.famAssort.id").value("1"))
				.andExpect(jsonPath("$.famAssort.description").exists())
				.andExpect(jsonPath("$.famAssort.description").value("DROGHERIA ALIMENTARE"))
				// ingredienti
				.andExpect(jsonPath("$.ingredients").isEmpty())
				// Vat
				.andExpect(jsonPath("$.vat.idVat").exists()).andExpect(jsonPath("$.vat.idVat").value("22"))
				.andExpect(jsonPath("$.vat.description").exists())
				.andExpect(jsonPath("$.vat.description").value("IVA RIVENDITA 22%"))
				.andExpect(jsonPath("$.vat.taxRate").exists()).andExpect(jsonPath("$.vat.taxRate").value("22"))

				.andDo(print());
	}

	private String Barcode = "8008490002138";

	@Test
	@Order(2)
	public void ErrlistArtByEan() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/article/findByBarcode/" + Barcode)
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("The article with barcode \'" + Barcode + "\' was not found!"))
				.andDo(print());
	}

	@Test
	@Order(3)
	public void listArtByCodArt() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/article/findByCodart/002000301").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(JsonData)).andReturn();
	}

	private String CodArt = "002000301b";

	@Test
	@Order(4)
	public void errlistArtByCodArt() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/article/findByCodart/" + CodArt)
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("The article with codart " + CodArt + " was not found!"))
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
			      "description": "ACQUA ULIVETO 15 LT",
			      "um": "PZ",
			      "codStat": "",
			      "pzCart": 6,
			      "netWeight": 1.5,
			      "idArtStatus": "1",
			      "creationDate": "2010-06-14",
			      "price": 0.0,
			      "barcode": [
			        {
			          "barcode": "8008490000021",
			          "type": "CP"
			        }
			      ],
			      "ingredients": null,
			      "famAssort": {
			        "id": 1,
			        "description": "DROGHERIA ALIMENTARE"
			      },
			      "vat": {
			        "idVat": 22,
			        "description": "IVA RIVENDITA 22%",
			        "taxRate": 22
			      }
			    }
			  ]
			}""";

	@Test
	@Order(5)
	public void listArtByDesc() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/findByDescription/ACQUA ULIVETO 15 LT")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.itemList", hasSize(1)))
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(JsonData2)).andReturn();
	}
}