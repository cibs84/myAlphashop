package com.xantrix.webapp.controllers;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.exceptions.ItemAlreadyExistsException;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.services.ArticoliService;
import com.xantrix.webapp.services.BarcodeService;

@RestController
@RequestMapping("/api/articoli")
public class ArticoliController {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticoliController.class);
	
	@Autowired
	BarcodeService barcodeService;
	
	@Autowired
	ArticoliService articoliService;
	
	@Autowired
	ModelMapper modelMapper;
	
	@GetMapping("/cerca/ean/{barcode}")
	public ResponseEntity<ArticoliDto> listArtByEan(@PathVariable("barcode") String barcode) throws NotFoundException {
		
		logger.info("******** Otteniamo l'articolo con barcode %s ********".formatted(barcode));

		Articoli articolo;
		Optional<Barcode> ean = Optional.ofNullable(barcodeService.findByBarcode(barcode));
		if(ean.isPresent()){
			
			articolo = ean.get().getArticolo();
			ArticoliDto articoloDto = modelMapper.map(articolo, ArticoliDto.class);
			
			return new ResponseEntity<ArticoliDto>(articoloDto, HttpStatus.OK); 
		} else {
			String errMessage = "Il barcode %s non è stato trovato!".formatted(barcode);
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
	}
	
	@GetMapping("/cerca/codice/{codart}")
	public ResponseEntity<ArticoliDto> listArtByCodArt(@PathVariable("codart") String codArt) throws NotFoundException {
		
		logger.info("******** Otteniamo l'articolo con codart %s ********".formatted(codArt));
		
		ArticoliDto articoloDto = articoliService.getByCodArt(codArt);
		
		return new ResponseEntity<ArticoliDto>(articoloDto, HttpStatus.OK);
	}
	
	@GetMapping("/cerca/descrizione/{descrizione}")
	public ResponseEntity<List<ArticoliDto>> listArtByDesc(@PathVariable("descrizione") String descrizione){
		
		logger.info("******** Otteniamo l'articolo con descrizione %s ********".formatted(descrizione));
		
		List<ArticoliDto> articoli = articoliService.getByDescrizione(descrizione, PageRequest.ofSize(10));
		return new ResponseEntity<List<ArticoliDto>>(articoli, HttpStatus.OK);
	}
	
	@PostMapping("/inserisci")
	public ResponseEntity<Articoli> insArt(@RequestBody ArticoliDto articoloDto) throws ItemAlreadyExistsException, NotFoundException {
		
		logger.info("******** Inserimento dell'articolo %s ********".formatted(articoloDto.getCodArt()));
		
		articoliService.create(articoloDto);
		return new ResponseEntity<Articoli>(HttpStatus.CREATED);
	}
}
