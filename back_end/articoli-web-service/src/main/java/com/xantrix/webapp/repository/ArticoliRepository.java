package com.xantrix.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entities.Articoli;

public interface ArticoliRepository extends JpaRepository<Articoli, String> {

	@Query(value = "SELECT * FROM ARTICOLI WHERE DESCRIZIONE LIKE :desArt", nativeQuery = true)
	List<Articoli> SelByDescrizioneLike(@Param("desArt") String descrizione);

	List<Articoli> findByDescrizioneLike(String descrizione, Pageable pageRequest);

	Optional<Articoli> findByCodArt(String codArt);

	boolean existsByCodArt(String codArt);
	
	@Query(value="SELECT a FROM Articoli a JOIN a.barcode b WHERE b.barcode IN (:ean)")
	Optional<Articoli> selByEan(@Param("ean") String ean);
}
