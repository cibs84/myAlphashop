package com.xantrix.webapp.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entities.Articoli;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface ArticoliRepository  extends PagingAndSortingRepository<Articoli, String>
{
	@Query(value = "SELECT * FROM ARTICOLI WHERE DESCRIZIONE LIKE :desArt", nativeQuery = true)
	List<Articoli> SelByDescrizioneLike(@Param("desArt") String descrizione);

	List<Articoli> findByDescrizioneLike(String descrizione, Pageable pageable);
	
	Articoli findByCodArt(String codArt);
}
