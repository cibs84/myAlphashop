package com.alphashop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alphashop.entities.Article;

public interface ArticleRepository extends JpaRepository<Article, String> {

	Page<Article> findAllByOrderByCodArtAsc(Pageable pageRequest);
	
	// selByDescrizioneLike e findByDescrizioneLike sono alternativi.
	// Usare findByDescrizioneLike se si vogliono i risultati con paginazione
	@Query(value = "SELECT * FROM ARTICLES WHERE DESCRIPTION LIKE :desArt", nativeQuery = true)
	List<Article> selByDescriptionLike(@Param("desArt") String description);

	Page<Article> findByDescriptionLikeOrderByCodArtAsc(String description, Pageable pageRequest);
	// ------------------------------------------------------------------------------------------

	Optional<Article> findByCodArt(String codArt);

	boolean existsByCodArt(String codArt);
	
	@Query(value="SELECT a FROM Article a JOIN a.barcode b WHERE b.barcode IN (:ean)")
	Optional<Article> selByEan(@Param("ean") String ean);
}
