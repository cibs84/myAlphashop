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
import com.xantrix.webapp.dtos.ArticoloDto;
import com.xantrix.webapp.entities.Articolo;
import com.xantrix.webapp.exceptions.ItemAlreadyExistsException;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.mappers.ArticoloMapper;
import com.xantrix.webapp.repository.ArticoloRepository;

@Service
@Transactional(readOnly = true)
public class ArticoloService {

	private static final Logger logger = LoggerFactory.getLogger(ArticoloService.class);
	private final ArticoloRepository articoloRepository;
	private final ArticoloMapper articoloMapper;

	public ArticoloService(ArticoloRepository articoloRepository, 
						   ArticoloMapper articoloMapper) {
		this.articoloRepository = articoloRepository;
		this.articoloMapper = articoloMapper;
	}

	public PaginatedResponseList<ArticoloDto> getAll(Optional<Integer> currentPage, Optional<Integer> pageSize) throws NotFoundException {
		Pageable articlesPagination = PageRequest.of(currentPage.filter(n -> n > -1).orElse(1),
				pageSize.filter(n -> n > 0).orElse(10));
		Page<Articolo> articolo = articoloRepository.findAllByOrderByCodArtAsc(articlesPagination);
		
		if (articolo.isEmpty()) {
			String errMessage = "Non è stato trovato alcun articolo";
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		List<ArticoloDto> articoloDto = articolo.stream().map(art -> articoloMapper.toModel(art))
				.collect(Collectors.toList());
		
		PaginatedResponseList<ArticoloDto> articoloResponse = new PaginatedResponseList<>(articolo, articoloDto);
		
		return articoloResponse;
	}

	public PaginatedResponseList<ArticoloDto> getByDescrizione(String descrizione, Optional<Integer> currentPage,
			Optional<Integer> pageSize) throws NotFoundException {

		String descrizioneMod = "%" + descrizione.toUpperCase() + "%";

		Pageable articlesPagination = PageRequest.of(currentPage.map(n -> n-1).filter(n -> n > -1).orElse(0),
				pageSize.filter(n -> n > 0).orElse(10));
		
		Page<Articolo> articolo = articoloRepository.findByDescrizioneLikeOrderByCodArtAsc(descrizioneMod, articlesPagination);
		
		if (articolo.isEmpty()) {
			String errMessage = "Non è stato trovato alcun articolo avente descrizione '%s'".formatted(descrizione);
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		// Converte Page<Articolo> in List<ArticoloDto>
		List<ArticoloDto> articoloDto = articolo.stream().map(art -> articoloMapper.toModel(art))
				.collect(Collectors.toList());

		PaginatedResponseList<ArticoloDto> articoloResponse = new PaginatedResponseList<ArticoloDto>(articolo, articoloDto);
		
		
		return articoloResponse;
	}

	public ArticoloDto getByCodArt(String codArt) throws NotFoundException {
		Optional<Articolo> articolo = articoloRepository.findByCodArt(codArt);
		ArticoloDto articoloDto = null;
		
		if (articolo.isPresent()) {
			articoloDto = articoloMapper.toModel(articolo.get());
		}
		
		return articoloDto;
	}

	public ArticoloDto getByBarcode(String ean) throws NotFoundException {
		
		Optional<Articolo> articolo = articoloRepository.selByEan(ean);
		
		if (articolo.isEmpty()) {

			// Questo messaggio di errore potrebbe non essere usato nel frontend
			String errMessage = "L'articolo con barcode '%s' non è stato trovato!".formatted(ean);
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		ArticoloDto articoloDto = articoloMapper.toModel(articolo.get());
		
		return articoloDto;
	}

	public boolean existsByCodArt(String codArt) {
		return articoloRepository.existsByCodArt(codArt);
	}

	@Transactional
	public void create(ArticoloDto articoloDto) throws ItemAlreadyExistsException {
		Articolo articolo = articoloMapper.toEntity(articoloDto);
		articolo.setDescrizione(articolo.getDescrizione().toUpperCase());
		articoloRepository.save(articolo);
	}

	@Transactional
	public void delete(Articolo articolo) {
		articoloRepository.delete(articolo);
	}
}
