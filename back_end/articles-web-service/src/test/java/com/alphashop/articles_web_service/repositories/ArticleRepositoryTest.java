package com.alphashop.articles_web_service.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.repositories.ArticleRepository;
import com.alphashop.articles_web_service.test.BaseSpringIT;

public class ArticleRepositoryTest extends BaseSpringIT {

	@Autowired
	private ArticleRepository articleRepository;

	@Test
	public void testFindByDescriptionLike() {
		List<Article> items = articleRepository.selByDescriptionLike("%ACQUA ULIVETO%");
		assertEquals(2, items.size());
	}

	@Test
	public void testFindByDescriptionLikePage() {
		Page<Article> items = articleRepository.findByDescriptionLikeOrderByCodArtAsc("%ACquA%", PageRequest.of(0, 10));
		assertEquals(10, items.getNumberOfElements());
	}

	@Test
	public void testFindByCodArt() throws Exception {
		assertThat(articleRepository.findByCodArt("002000301").get()).extracting(Article::getDescription)
				.isEqualTo("ACQUA ULIVETO 15 LT");

	}

	@Test
	public void testSelByEan() {
		assertThat(articleRepository.selByEan("8008490000021").get())
			.extracting(Article::getDescription)
			.isEqualTo("ACQUA ULIVETO 15 LT");
	}
}
