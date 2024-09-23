package com.alphashop.articles_web_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alphashop.articles_web_service.entities.Article;

public interface ArticleRepository extends JpaRepository<Article, String> {

	Page<Article> findAllByOrderByCodArtAsc(Pageable pageRequest);
	
	// selByDescrizioneLike e findByDescrizioneLike sono alternativi.
	// Usare findByDescriptionLike.. se si vogliono i risultati con paginazione
	@Query(value = "SELECT * FROM articles a WHERE UPPER(a.description) LIKE UPPER(:desArt) ORDER BY a.codart ASC", nativeQuery = true)
	List<Article> selByDescriptionLike(@Param("desArt") String description);

	@Query(value = "SELECT * FROM articles a WHERE UPPER(a.description) LIKE UPPER(:desArt) ORDER BY a.codart ASC", nativeQuery = true)
	Page<Article> findByDescriptionLikeOrderByCodArtAsc(@Param("desArt") String description, Pageable pageRequest);

	Optional<Article> findByCodArt(String codArt);

	@Query(value="SELECT a FROM Article a JOIN a.barcodes b WHERE b.barcode IN (:ean)")
	Optional<Article> selByEan(@Param("ean") String ean);
}
