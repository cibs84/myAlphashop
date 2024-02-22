package com.xantrix.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entities.Articoli;

public interface ArticoliRepository extends JpaRepository<Articoli, String> {
	
	@Query(value = "SELECT * FROM ARTICOLI WHERE DESCRIZIONE LIKE :desArt", nativeQuery = true)
	List<Articoli> SelByDescrizioneLike(@Param("desArt") String descrizione);
	
	List<Articoli> findByDescrizioneLike(String descrizione, PageRequest pageRequest);
	
	Optional<Articoli> findByCodArt(String codArt);
	
	boolean existsByCodArt(String codArt);
}
