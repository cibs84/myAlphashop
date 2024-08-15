package com.alphashop.ControllerTests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.alphashop.repositories.ArticleRepository;
import com.alphashop.test.BaseSpringIT;

public class ArticleRestControllerReadIT extends BaseSpringIT {
	
	@Autowired
	ArticleRepository articleRepository;
	
	String JsonData = """
			{
			    "codArt": "002000301",
			    "description": "ACQUA ULIVETO 15 LT",
			    "um": "PZ",
			    "codStat": "",
			    "pcsCart": 6,
			    "netWeight": 1.5,
			    "idArtStatus": "1",
			    "creationDate": "2010-06-14",
			    "barcode": [
			        {
			            "barcode": "8008490000021",
			            "type": "CP"
			        }
			    ],
			    "category": {
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
	public void listArtByEan() throws Exception {
		
		String existentBarcode = "8008490000021";
		
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/article/findByBarcode/" + existentBarcode).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				// articolo
				.andExpect(jsonPath("$.codArt").exists()).andExpect(jsonPath("$.codArt").value("002000301"))
				.andExpect(jsonPath("$.description").exists())
				.andExpect(jsonPath("$.description").value("ACQUA ULIVETO 15 LT")).andExpect(jsonPath("$.um").exists())
				.andExpect(jsonPath("$.um").value("PZ")).andExpect(jsonPath("$.codStat").exists())
				.andExpect(jsonPath("$.codStat").value("")).andExpect(jsonPath("$.pcsCart").exists())
				.andExpect(jsonPath("$.pcsCart").value("6")).andExpect(jsonPath("$.netWeight").exists())
				.andExpect(jsonPath("$.netWeight").value("1.5")).andExpect(jsonPath("$.idArtStatus").exists())
				.andExpect(jsonPath("$.idArtStatus").value("1")).andExpect(jsonPath("$.creationDate").exists())
				.andExpect(jsonPath("$.creationDate").value("2010-06-14"))
				// barcode
				.andExpect(jsonPath("$.barcode[0].barcode").exists())
				.andExpect(jsonPath("$.barcode[0].barcode").value("8008490000021"))
				.andExpect(jsonPath("$.barcode[0].type").exists()).andExpect(jsonPath("$.barcode[0].type").value("CP"))
				// famAssort
				.andExpect(jsonPath("$.category.id").exists()).andExpect(jsonPath("$.category.id").value("1"))
				.andExpect(jsonPath("$.category.description").exists())
				.andExpect(jsonPath("$.category.description").value("DROGHERIA ALIMENTARE"))
				// ingredienti
				.andExpect(jsonPath("$.ingredients").isEmpty())
				// Vat
				.andExpect(jsonPath("$.vat.idVat").exists()).andExpect(jsonPath("$.vat.idVat").value("22"))
				.andExpect(jsonPath("$.vat.description").exists())
				.andExpect(jsonPath("$.vat.description").value("IVA RIVENDITA 22%"))
				.andExpect(jsonPath("$.vat.taxRate").exists()).andExpect(jsonPath("$.vat.taxRate").value("22"))

				.andDo(print());
	}

	@Test
	public void errListArtByEan() throws Exception {
		
		String inexistentBarcode = "inexistent_barcode";
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/article/findByBarcode/" + inexistentBarcode)
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("The article with barcode \'" + inexistentBarcode + "\' was not found!"))
				.andDo(print());
	}

	@Test
	public void listArtByCodArt() throws Exception {
		
		String existentCodart = "002000301";
		
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/article/findByCodart/" + existentCodart).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(JsonData)).andReturn();
	}

	@Test
	public void errListArtByCodArt() throws Exception {
		
		String CodArt = "inexistent_codart";
		
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
			      "pcsCart": 6,
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
			      "category": {
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
	public void listArtByDesc() throws Exception {
		
		String existentDescription = "ACQUA ULIVETO 15 LT";
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/findByDescription/" + existentDescription)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.itemList", hasSize(1)))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(content().json(JsonData2)).andReturn();
	}
	
	@Test
	public void listArtByDescWithoutDescription1() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/findByDescription/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andDo(print());
	}

	@Test
	public void errorListArtByDescWithoutDescription2() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/findByDescription")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andDo(print());
	}
	
	@Test
	public void listArtByDescWithDescriptionEmpty() throws Exception {

		String emptyDescription = " ";

		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/findByDescription/" + emptyDescription)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.pagination.totalPages", equalTo(597)))
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andReturn();
	}
	
	@Test
	@Transactional // At the end of the method, undo all changes made on the db
	public void errorListArtByDescWithDescriptionAndDbEmpties() throws Exception {
		
		articleRepository.deleteAll();
		
		String CodArt = " ";
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/findByDescription/" + CodArt)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("No articles were found"))
				.andDo(print());
	}
}