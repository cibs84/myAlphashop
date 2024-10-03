package com.alphashop.articles_web_service.controllers;

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

import com.alphashop.articles_web_service.repositories.ArticleRepository;
import com.alphashop.articles_web_service.test.BaseSpringIT;

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
	    "price": null,
	    "barcodes": [
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
				MockMvcRequestBuilders.get("/api/articles/find/barcode/" + existentBarcode).accept(MediaType.APPLICATION_JSON))
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
				.andExpect(jsonPath("$.barcodes[0].barcode").exists())
				.andExpect(jsonPath("$.barcodes[0].barcode").value("8008490000021"))
				.andExpect(jsonPath("$.barcodes[0].type").exists())
				.andExpect(jsonPath("$.barcodes[0].type").value("CP"))
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
	public void errListArtByEanWithInexistentBarcode() throws Exception {
		
		String inexistentBarcode = "inexistent_barcode";
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/barcode/" + inexistentBarcode)
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
		.andExpect(jsonPath("$.message").value("The article with barcode \'" + inexistentBarcode + "\' was not found!"))
		.andDo(print());
	}

	@Test
	public void errListArtByEanWithoutBarcode() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/barcode")
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("Insert a valid barcode!"))
				.andDo(print());
	}
	
	@Test
	public void listArtByCodArt() throws Exception {
		
		String existentCodart = "002000301";
		
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/articles/find/codart/" + existentCodart).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(JsonData)).andReturn();
	}

	@Test
	public void errListArtByCodArtWithInexistentCodArt() throws Exception {
		
		String inexistentCodArt = "inexistent_codart";
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/codart/" + inexistentCodArt)
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("The article with codart " + inexistentCodArt + " was not found!"))
				.andDo(print());
	}

	@Test
	public void errListArtByCodArtWithoutCodArt() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/codart")
				.contentType(MediaType.APPLICATION_JSON).content(JsonData).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("Insert a valid codArt!"))
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
	        "totalElements": 2,
	        "totalPagesArray": [
	            1
	        ]
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
	            "price": null,
	            "barcodes": [
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
	        },
	        {
	            "codArt": "058578901",
	            "description": "ACQUA ULIVETO NATUR.ML.500",
	            "um": "PZ",
	            "codStat": "",
	            "pcsCart": 24,
	            "netWeight": 0.5,
	            "idArtStatus": "2",
	            "creationDate": "2011-01-12",
	            "price": null,
	            "barcodes": [
	                {
	                    "barcode": "8008490991046",
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
	public void listAllArticles() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/all")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pagination.totalPages").value(598))
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn();
	}
	
	@Test
	public void listArtByDesc() throws Exception {
		
		String existentDescription = "ACQUA ULIVETO";
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/description/" + existentDescription)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.itemList", hasSize(2)))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(content().json(JsonData2)).andReturn();
	}
	
	@Test
	public void errListArtByDescWithoutDescription1() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/description/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("Insert a valid description!"))
				.andDo(print());
	}

	@Test
	public void errorListArtByDescWithoutDescription2() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/description")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("Insert a valid description!"))
				.andDo(print());
	}
	
	@Test
	public void listArtByDescWithDescriptionEmpty() throws Exception {

		String emptyDescription = " ";

		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/description/" + emptyDescription)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.pagination.totalPages", equalTo(597)))
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andReturn();
	}
	
	@Test
	@Transactional // At the end of the method, undo all changes made on the db
	public void errorListArtByDescWithDescriptionAndDbEmpties() throws Exception {
		
		articleRepository.deleteAll();
		
		String CodArt = " ";
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/find/description/" + CodArt)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message").value("No articles were found"))
				.andDo(print());
	}
}