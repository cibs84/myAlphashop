package com.xantrix.webapp.ControllerTests;

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

import com.xantrix.webapp.entities.Articolo;
import com.xantrix.webapp.repository.ArticoloRepository;
import com.xantrix.webapp.test.BaseSpringIT;

class ArticoloRestControllerDeleteIT extends BaseSpringIT {
    
	@Autowired
	private ArticoloRepository articoloRepository;
	
	@Value("codartArtNonEliminabile")
	private String codartArtNonEliminabile;
	
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
	
	private String JsonData02 = """
			{
			  "codArt": "abcTest",
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
			    "codArt": "abcTest",
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
	public void testDelArticolo() throws Exception {
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
    	
        Long articoloInitCount = articoloRepository.count();
        
        mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articolo/elimina/123Test")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.code").value("200 OK"))
		.andExpect(jsonPath("$.message")
				.value("Eliminazione articolo '123Test - ARTICOLO UNIT TEST INSERIMENTO' eseguita con successo"))
		.andDo(print());

        Assertions.assertThat(articoloRepository.count()).isEqualTo(articoloInitCount - 1);
	}
	
	@Test
	public void testDelArticoloNonEliminabile() throws Exception {
		{
    		mockMvc.perform(MockMvcRequestBuilders.post("/api/articolo/inserisci")
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(JsonData02)
    				.accept(MediaType.APPLICATION_JSON))
    				.andExpect(status().isCreated())
    				.andDo(print());
    		
    		Articolo articolo = articoloRepository.findByCodArt("abcTest").get();
    		assertThat(articolo.getDescrizione()).isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
    	}
		
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articolo/elimina/abcTest")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isForbidden())
		.andExpect(jsonPath("$.code").value(403))
		.andExpect(jsonPath("$.message")
				.value("Articolo 'abcTest' non eliminabile"))
		.andDo(print());
	}
	
	@Test
	public void testDelArticoloNonTrovato() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/articolo/elimina/xxxx")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.message")
						.value("Articolo da eliminare 'xxxx' non è stato trovato"))
				.andDo(print());
	}
}
