package com.xantrix.webapp.controllers;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.dtos.common.PaginatedResponseList;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exceptions.ItemAlreadyExistsException;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.services.ArticoliService;

@RestController
@RequestMapping("/api/articoli")
@CrossOrigin(origins = "http://localhost:4200/")
public class ArticoliController {

	private static final Logger logger = LoggerFactory.getLogger(ArticoliController.class);

	@Autowired
	ArticoliService articoliService;

	@Autowired
	ModelMapper modelMapper;

	@GetMapping
	public ResponseEntity<PaginatedResponseList<ArticoliDto>> listAll(
			@RequestParam(name = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(name = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		logger.info("******** Otteniamo tutti gli articoli ********");

		PaginatedResponseList<ArticoliDto> articoli = articoliService.getAll(currentPage, pageSize);
		
		return new ResponseEntity<PaginatedResponseList<ArticoliDto>>(articoli, HttpStatus.OK);
	}

	@GetMapping("/cerca/barcode/{ean}")
	public ResponseEntity<ArticoliDto> listArtByEan(@PathVariable("ean") String ean) throws NotFoundException {

		logger.info("******** Otteniamo l'articolo con barcode %s ********".formatted(ean));

		ArticoliDto articoloDto = articoliService.getByBarcode(ean);
		
		return new ResponseEntity<ArticoliDto>(articoloDto, HttpStatus.OK);
	}

	@GetMapping("/cerca/codice/{codart}")
	public ResponseEntity<ArticoliDto> listArtByCodArt(@PathVariable("codart") String codArt) throws NotFoundException {

		logger.info("******** Otteniamo l'articolo con codart %s ********".formatted(codArt));

		ArticoliDto articoloDto = articoliService.getByCodArt(codArt);

		return new ResponseEntity<ArticoliDto>(articoloDto, HttpStatus.OK);
	}
	
	@GetMapping(path = "/cerca/descrizione/{descrizione}")
	public ResponseEntity<PaginatedResponseList<ArticoliDto>> listArtByDesc(@PathVariable("descrizione") String descrizione,
			@RequestParam(value = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		
		logger.info("******** Otteniamo l'articolo con descrizione %s ********".formatted(descrizione));
		
		PaginatedResponseList<ArticoliDto> articoli = articoliService.getByDescrizione(descrizione, currentPage, pageSize);
		return new ResponseEntity<PaginatedResponseList<ArticoliDto>>(articoli, HttpStatus.OK);
	}

	@PostMapping("/inserisci")
	public ResponseEntity<Articoli> insArt(@RequestBody ArticoliDto articoloDto)
			throws ItemAlreadyExistsException, NotFoundException {

		logger.info("******** Inserimento dell'articolo %s ********".formatted(articoloDto.getCodArt()));

		articoliService.create(articoloDto);
		return new ResponseEntity<Articoli>(HttpStatus.CREATED);
	}
}
