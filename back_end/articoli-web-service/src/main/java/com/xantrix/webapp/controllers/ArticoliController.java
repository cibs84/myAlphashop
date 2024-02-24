package com.xantrix.webapp.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.xantrix.webapp.dtos.BarcodeDto;
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

	@GetMapping(params = { "pageNumber", "maxRecords" })
	public ResponseEntity<Iterable<ArticoliDto>> listAll(@RequestParam("pageNumber") Optional<Integer> pageNumber,
			@RequestParam("maxRecords") Optional<Integer> maxRecords) {
		logger.info("******** Otteniamo tutti gli articoli ********");

		Iterable<ArticoliDto> articoli = articoliService.getAll(pageNumber, maxRecords);
		return new ResponseEntity<Iterable<ArticoliDto>>(articoli, HttpStatus.OK);
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
	public ResponseEntity<List<ArticoliDto>> listArtByDesc(@PathVariable("descrizione") String descrizione,
			@RequestParam(value = "pageNumber", required = false) Optional<Integer> pageNumber,
			@RequestParam(value = "maxRecords", required = false) Optional<Integer> maxRecords) {
		
		logger.info("******** Otteniamo l'articolo con descrizione %s ********".formatted(descrizione));
		
		List<ArticoliDto> articoli = articoliService.getByDescrizione(descrizione, pageNumber, maxRecords);
		return new ResponseEntity<List<ArticoliDto>>(articoli, HttpStatus.OK);
	}

	@PostMapping("/inserisci")
	public ResponseEntity<Articoli> insArt(@RequestBody ArticoliDto articoloDto)
			throws ItemAlreadyExistsException, NotFoundException {

		logger.info("******** Inserimento dell'articolo %s ********".formatted(articoloDto.getCodArt()));

		articoliService.create(articoloDto);
		return new ResponseEntity<Articoli>(HttpStatus.CREATED);
	}
}
