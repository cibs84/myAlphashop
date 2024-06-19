package com.xantrix.webapp.tests.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class ArticoliRepositoryTest {

	@Autowired
	private ArticoliRepository articoliRepository;

	@Test
	public void TestfindByDescrizioneLike() {
		List<Articoli> items = articoliRepository.SelByDescrizioneLike("%ACQUA ULIVETO%");
		assertEquals(2, items.size());
	}

	@Test
	public void TestfindByDescrizioneLikePage() {
		Page<Articoli> items = articoliRepository.findByDescrizioneLikeOrderByCodArtAsc("%ACQUA%", PageRequest.of(0, 10));
		assertEquals(10, items.getNumberOfElements());
	}

	@Test
	public void TestfindByCodArt() throws Exception {
		assertThat(articoliRepository.findByCodArt("002000301").get()).extracting(Articoli::getDescrizione)
				.isEqualTo("ACQUA ULIVETO 15 LT");

	}

	@Test
	public void TestSelByEan() {
		assertThat(articoliRepository.selByEan("8008490000021").get())
			.extracting(Articoli::getDescrizione)
			.isEqualTo("ACQUA ULIVETO 15 LT");
	}
}
