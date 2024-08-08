package com.xantrix.webapp.controllers;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.common.PaginatedResponseList;
import com.xantrix.webapp.dtos.ArticoloDto;
import com.xantrix.webapp.dtos.InfoMsg;
import com.xantrix.webapp.entities.Articolo;
import com.xantrix.webapp.exceptions.BindingException;
import com.xantrix.webapp.exceptions.ItemAlreadyExistsException;
import com.xantrix.webapp.exceptions.NotErasableException;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.mappers.ArticoloMapper;
import com.xantrix.webapp.services.ArticoloService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/articolo")
@CrossOrigin(origins = "http://localhost:4200/")
public class ArticoloController {

	private static final Logger logger = LoggerFactory.getLogger(ArticoloController.class);

	@Autowired
	ArticoloService articoloService;

	@Autowired
	ArticoloMapper articoloMapper;
	
	@Autowired
	private ResourceBundleMessageSource errMessageSource;
	
	@Value("${codartArtNonEliminabile}")
	private String codartArtNonEliminabile;

	@GetMapping
	public ResponseEntity<PaginatedResponseList<ArticoloDto>> listAll(
			@RequestParam(name = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(name = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		
		logger.info("******** Otteniamo tutti gli articoli ********");

		PaginatedResponseList<ArticoloDto> articolo = articoloService.getAll(currentPage, pageSize);
		
		return new ResponseEntity<PaginatedResponseList<ArticoloDto>>(articolo, HttpStatus.OK);
	}

	@GetMapping("/cerca/barcode/{ean}")
	public ResponseEntity<ArticoloDto> listArtByEan(@PathVariable("ean") String ean) throws NotFoundException {

		logger.info("******** Otteniamo l'articolo con barcode %s ********".formatted(ean));

		ArticoloDto articoloDto = articoloService.getByBarcode(ean);
		
		return new ResponseEntity<ArticoloDto>(articoloDto, HttpStatus.OK);
	}

	@GetMapping("/cerca/codice/{codart}")
	public ResponseEntity<ArticoloDto> listArtByCodArt(@PathVariable("codart") String codArt) throws NotFoundException {

		logger.info("******** Otteniamo l'articolo con codart %s ********".formatted(codArt));

		ArticoloDto articoloDto = articoloService.getByCodArt(codArt);

		if (articoloDto == null) {
			String errorMessage = "L'articolo con codice " + codArt + " non è stato trovato!";
			logger.warn(errorMessage);

			throw new NotFoundException(errorMessage);
		}
		
		return new ResponseEntity<ArticoloDto>(articoloDto, HttpStatus.OK);
	}
	
	@GetMapping(path = "/cerca/descrizione/{descrizione}")
	public ResponseEntity<PaginatedResponseList<ArticoloDto>> listArtByDesc(@PathVariable("descrizione") String descrizione,
			@RequestParam(value = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		
		logger.info("******** Otteniamo l'articolo con descrizione %s ********".formatted(descrizione));
		
		PaginatedResponseList<ArticoloDto> articolo = articoloService.getByDescrizione(descrizione, currentPage, pageSize);
		return new ResponseEntity<PaginatedResponseList<ArticoloDto>>(articolo, HttpStatus.OK);
	}

	@PostMapping("/inserisci")
	public ResponseEntity<InfoMsg> insArt(@Valid @RequestBody ArticoloDto articoloDto,
											BindingResult bindingResult)
			throws ItemAlreadyExistsException, NotFoundException, BindingException {

		logger.info("******** Inserimento dell'articolo %s ********".formatted(articoloDto.getCodArt()));

		//controlla validità dati articolo
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessageSource.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(MsgErr);
			
			throw new BindingException(MsgErr);
		}
		
		// controlla se l'articolo da creare già esiste
		ArticoloDto articolo = articoloService.getByCodArt(articoloDto.getCodArt());
		if (articolo != null) {
			String errorMessage = "Articolo '%s - %s' già presente in anagrafica"
					.formatted(articoloDto.getCodArt(), articoloDto.getDescrizione());
			logger.warn(errorMessage);

			throw new ItemAlreadyExistsException(errorMessage);
		}
		
		articoloService.create(articoloDto);
		return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(), 
				String.format("Inserimento articolo '%s' eseguito con successo", articoloDto.getCodArt())), HttpStatus.CREATED);
	}
	
	@PutMapping("/modifica")
	public ResponseEntity<InfoMsg> updArticolo(@Valid @RequestBody ArticoloDto articoloDto,
												BindingResult bindingResult) throws BindingException, NotFoundException, ItemAlreadyExistsException {
		
		logger.info("******** Aggiornamento dell'articolo %s ********".formatted(articoloDto.getCodArt()));
		
		if (bindingResult.hasErrors()) {
			
			String msgErr = errMessageSource.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(msgErr);
			
			throw new BindingException(msgErr);
		}
		
		ArticoloDto articoloCheck = articoloService.getByCodArt(articoloDto.getCodArt());
		
		if (articoloCheck == null) {
			String errMsg = String.format("Articolo '%s' già presente in anagrafica", articoloDto.getCodArt());

			logger.warn(errMsg);
	
			throw new NotFoundException(errMsg);
		}
		
		articoloService.create(articoloDto);
		
		return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
				String.format("Modifica articolo '%s' eseguita con successo", articoloDto.getCodArt())), HttpStatus.CREATED);
	}
	
	
	@DeleteMapping("/elimina/{codart}")
	public ResponseEntity<ObjectNode> delArt(@PathVariable("codart") String codArt) throws NotFoundException, NotErasableException {
		
		ArticoloDto articoloDto = articoloService.getByCodArt(codArt);
		
		if (articoloDto == null) {
			String errMessage = String.format("Articolo da eliminare '%s' non è stato trovato", codArt);
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		if (articoloDto.getCodArt().equals(codartArtNonEliminabile)) {
			String errMessage = String.format("Articolo '%s' non eliminabile", this.codartArtNonEliminabile);
			logger.warn(errMessage);
			throw new NotErasableException(errMessage);
		}

		Articolo articolo = articoloMapper.toEntity(articoloDto);
		articoloService.delete(articolo);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode responseNode = mapper.createObjectNode();
		
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", String.format("Eliminazione articolo '%s - %s' eseguita con successo", articolo.getCodArt(), articolo.getDescrizione()));
		
		return new ResponseEntity<ObjectNode>(responseNode, HttpStatus.OK);
	}
}
