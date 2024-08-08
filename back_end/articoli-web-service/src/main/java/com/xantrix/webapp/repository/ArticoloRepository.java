package com.xantrix.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entities.Articolo;

public interface ArticoloRepository extends JpaRepository<Articolo, String> {

	Page<Articolo> findAllByOrderByCodArtAsc(Pageable pageRequest);
	
	// SelByDescrizioneLike e findByDescrizioneLike sono alternativi.
	// Usare findByDescrizioneLike se si vogliono i risultati con paginazione
	@Query(value = "SELECT * FROM ARTICOLI WHERE DESCRIZIONE LIKE :desArt", nativeQuery = true)
	List<Articolo> SelByDescrizioneLike(@Param("desArt") String descrizione);

	Page<Articolo> findByDescrizioneLikeOrderByCodArtAsc(String descrizione, Pageable pageRequest);
	// ------------------------------------------------------------------------------------------

	Optional<Articolo> findByCodArt(String codArt);

	boolean existsByCodArt(String codArt);
	
	@Query(value="SELECT a FROM Articolo a JOIN a.barcode b WHERE b.barcode IN (:ean)")
	Optional<Articolo> selByEan(@Param("ean") String ean);
}
