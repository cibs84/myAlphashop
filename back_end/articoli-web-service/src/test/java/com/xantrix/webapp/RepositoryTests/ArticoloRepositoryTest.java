package com.xantrix.webapp.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xantrix.webapp.entities.Articolo;
import com.xantrix.webapp.repository.ArticoloRepository;
import com.xantrix.webapp.test.BaseSpringIT;

public class ArticoloRepositoryTest extends BaseSpringIT {

	@Autowired
	private ArticoloRepository articoloRepository;

	@Test
	public void TestfindByDescrizioneLike() {
		List<Articolo> items = articoloRepository.SelByDescrizioneLike("%ACQUA ULIVETO%");
		assertEquals(2, items.size());
	}

	@Test
	public void TestfindByDescrizioneLikePage() {
		Page<Articolo> items = articoloRepository.findByDescrizioneLikeOrderByCodArtAsc("%ACQUA%", PageRequest.of(0, 10));
		assertEquals(10, items.getNumberOfElements());
	}

	@Test
	public void TestfindByCodArt() throws Exception {
		assertThat(articoloRepository.findByCodArt("002000301").get()).extracting(Articolo::getDescrizione)
				.isEqualTo("ACQUA ULIVETO 15 LT");

	}

	@Test
	public void TestSelByEan() {
		assertThat(articoloRepository.selByEan("8008490000021").get())
			.extracting(Articolo::getDescrizione)
			.isEqualTo("ACQUA ULIVETO 15 LT");
	}
}
