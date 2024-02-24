package com.xantrix.webapp.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exceptions.ItemAlreadyExistsException;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.repository.ArticoliRepository;

@Service
@Transactional(readOnly = true)
public class ArticoliService {

	private static final Logger logger = LoggerFactory.getLogger(ArticoliService.class);
	private final ArticoliRepository articoliRepository;
	private final ModelMapper modelMapper;

	public ArticoliService(ArticoliRepository articoliRepository, ModelMapper modelMapper) {
		this.articoliRepository = articoliRepository;
		this.modelMapper = modelMapper;
	}

	public Iterable<ArticoliDto> getAll(Optional<Integer> pageNumber, Optional<Integer> maxRecords) {
		List<ArticoliDto> articoliDto = null;
		Pageable articlesPagination = PageRequest.of(pageNumber.filter(n -> n > -1).orElse(1),
				maxRecords.filter(n -> n > 0).orElse(10));
		Page<Articoli> articoli = articoliRepository.findAll(articlesPagination);
		articoliDto = articoli.stream().map(art -> modelMapper.map(art, ArticoliDto.class))
				.collect(Collectors.toList());
		return articoliDto;
	}

	public List<ArticoliDto> getByDescrizione(String descrizione, Optional<Integer> pageNumber,
			Optional<Integer> maxRecords) {

		descrizione = descrizione.toUpperCase().concat("%");

		Pageable articlesPagination = PageRequest.of(pageNumber.filter(n -> n > -1).orElse(0),
				maxRecords.filter(n -> n > 0).orElse(10));
		List<Articoli> articoli = articoliRepository.findByDescrizioneLike(descrizione, articlesPagination);
		List<ArticoliDto> articoliDto = articoli.stream().map(art -> modelMapper.map(art, ArticoliDto.class))
				.collect(Collectors.toList());

		return articoliDto;
	}

	public ArticoliDto getByCodArt(String codArt) throws NotFoundException {
		Optional<Articoli> articolo = articoliRepository.findByCodArt(codArt);

		if (articolo.isEmpty()) {

			String errMessage = "L'articolo con codice " + codArt + " non è stato trovato!";
			logger.warn(errMessage);

			throw new NotFoundException(errMessage);
		}
		ArticoliDto articoloDto = modelMapper.map(articolo.get(), ArticoliDto.class);
		return articoloDto;
	}

	public ArticoliDto getByBarcode(String ean) throws NotFoundException {
		
		Optional<Articoli> articolo = articoliRepository.selByEan(ean);
		
		if (articolo.isEmpty()) {

			String errMessage = "Il barcode %s non è stato trovato!".formatted(ean);
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		ArticoliDto articoloDto = modelMapper.map(articolo.get(), ArticoliDto.class);
		
		return articoloDto;
	}

	public boolean existsByCodArt(String codArt) {
		return articoliRepository.existsByCodArt(codArt);
	}

	@Transactional
	public void create(ArticoliDto articoloDto) throws ItemAlreadyExistsException {

		Optional<Articoli> art = articoliRepository.findByCodArt(articoloDto.getCodArt());
		if (art.isPresent()) {
			String errorMessage = "Articolo %s presente in anagrafica! Impossibile utilizzare il metodo POST"
					.formatted(articoloDto.getCodArt());
			logger.warn(errorMessage);

			throw new ItemAlreadyExistsException(errorMessage);
		}

		Articoli articolo = modelMapper.map(articoloDto, Articoli.class);
		articoliRepository.save(articolo);
	}

	@Transactional
	public void delete(Articoli articolo) {
		articoliRepository.delete(articolo);
	}
}
