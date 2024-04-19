package com.xantrix.webapp.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xantrix.webapp.common.PaginatedResponseList;
import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exceptions.ItemAlreadyExistsException;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.mappers.ArticoliMapper;
import com.xantrix.webapp.repository.ArticoliRepository;

@Service
@Transactional(readOnly = true)
public class ArticoliService {

	private static final Logger logger = LoggerFactory.getLogger(ArticoliService.class);
	private final ArticoliRepository articoliRepository;
	private final ArticoliMapper articoliMapper;

	public ArticoliService(ArticoliRepository articoliRepository, 
						   ArticoliMapper articoliMapper) {
		this.articoliRepository = articoliRepository;
		this.articoliMapper = articoliMapper;
	}

	public PaginatedResponseList<ArticoliDto> getAll(Optional<Integer> currentPage, Optional<Integer> pageSize) throws NotFoundException {
		Pageable articlesPagination = PageRequest.of(currentPage.filter(n -> n > -1).orElse(1),
				pageSize.filter(n -> n > 0).orElse(10));
		Page<Articoli> articoli = articoliRepository.findAll(articlesPagination);
		
		if (articoli.isEmpty()) {
			String errMessage = "Non è stato trovato alcun articolo";
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		List<ArticoliDto> articoliDto = articoli.stream().map(art -> articoliMapper.toModel(art))
				.collect(Collectors.toList());
		
		PaginatedResponseList<ArticoliDto> articoliResponse = new PaginatedResponseList<>(articoli, articoliDto);
		
		return articoliResponse;
	}

	public PaginatedResponseList<ArticoliDto> getByDescrizione(String descrizione, Optional<Integer> currentPage,
			Optional<Integer> pageSize) throws NotFoundException {

		String descrizioneMod = "%" + descrizione.toUpperCase() + "%";

		Pageable articlesPagination = PageRequest.of(currentPage.map(n -> n-1).filter(n -> n > -1).orElse(0),
				pageSize.filter(n -> n > 0).orElse(10));
		
		Page<Articoli> articoli = articoliRepository.findByDescrizioneLike(descrizioneMod, articlesPagination);
		
		if (articoli.isEmpty()) {
			String errMessage = "Non è stato trovato alcun articolo avente descrizione '%s'".formatted(descrizione);
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		// Convertire da Page<Articoli> a List<ArticoliDto>
		List<ArticoliDto> articoliDto = articoli.stream().map(art -> articoliMapper.toModel(art))
				.collect(Collectors.toList());

		PaginatedResponseList<ArticoliDto> articoliResponse = new PaginatedResponseList<ArticoliDto>(articoli, articoliDto);
		
		
		return articoliResponse;
	}

	public ArticoliDto getByCodArt(String codArt) throws NotFoundException {
		Optional<Articoli> articolo = articoliRepository.findByCodArt(codArt);
		ArticoliDto articoloDto = null;
		
		if (articolo.isPresent()) {
			articoloDto = articoliMapper.toModel(articolo.get());
		}
		
		return articoloDto;
	}

	public ArticoliDto getByBarcode(String ean) throws NotFoundException {
		
		Optional<Articoli> articolo = articoliRepository.selByEan(ean);
		
		if (articolo.isEmpty()) {

			String errMessage = "Il barcode %s non è stato trovato!".formatted(ean);
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		ArticoliDto articoloDto = articoliMapper.toModel(articolo.get());
		
		return articoloDto;
	}

	public boolean existsByCodArt(String codArt) {
		return articoliRepository.existsByCodArt(codArt);
	}

	@Transactional
	public void create(ArticoliDto articoloDto) throws ItemAlreadyExistsException {
		Articoli articolo = articoliMapper.toEntity(articoloDto);
		articolo.setDescrizione(articolo.getDescrizione().toUpperCase());
		articoliRepository.save(articolo);
	}

	@Transactional
	public void delete(Articoli articolo) {
		articoliRepository.delete(articolo);
	}
}
