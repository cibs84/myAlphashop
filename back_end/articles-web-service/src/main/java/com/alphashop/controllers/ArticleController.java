package com.alphashop.controllers;

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

import com.alphashop.common.PaginatedResponseList;
import com.alphashop.dtos.ArticleDto;
import com.alphashop.dtos.InfoMsg;
import com.alphashop.entities.Article;
import com.alphashop.exceptions.BindingException;
import com.alphashop.exceptions.ItemAlreadyExistsException;
import com.alphashop.exceptions.NotDeletableException;
import com.alphashop.exceptions.NotFoundException;
import com.alphashop.mappers.ArticleMapper;
import com.alphashop.services.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200/")
public class ArticleController {

	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

	@Autowired
	ArticleService articleService;

	@Autowired
	ArticleMapper articleMapper;
	
	@Autowired
	private ResourceBundleMessageSource errMessageSource;
	
	@Value("${codartArtNotDeletable}")
	private String codartArtNotDeletable;

	@GetMapping("/articles")
	public ResponseEntity<PaginatedResponseList<ArticleDto>> listAll(
			@RequestParam(name = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(name = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		
		logger.info("******** Get all articles ********");

		PaginatedResponseList<ArticleDto> article = articleService.getAll(currentPage, pageSize);
		
		return new ResponseEntity<PaginatedResponseList<ArticleDto>>(article, HttpStatus.OK);
	}

	@GetMapping("/article/findByBarcode/{ean}")
	public ResponseEntity<ArticleDto> listArtByEan(@PathVariable("ean") String ean) throws NotFoundException {

		logger.info("******** Get article with barcode %s ********".formatted(ean));

		ArticleDto articleDto = articleService.getByBarcode(ean);
		
		return new ResponseEntity<ArticleDto>(articleDto, HttpStatus.OK);
	}

	@GetMapping("/article/findByCodart/{codart}")
	public ResponseEntity<ArticleDto> listArtByCodArt(@PathVariable("codart") String codArt) throws NotFoundException {

		logger.info("******** Get article with codart %s ********".formatted(codArt));

		ArticleDto articleDto = articleService.getByCodArt(codArt);

		if (articleDto == null) {
			String errorMessage = "The article with codart " + codArt + " was not found!";
			logger.warn(errorMessage);

			throw new NotFoundException(errorMessage);
		}
		
		return new ResponseEntity<ArticleDto>(articleDto, HttpStatus.OK);
	}
	
	@GetMapping(path = {
			"/articles/findByDescription/{description}"
	})
	public ResponseEntity<PaginatedResponseList<ArticleDto>> listArtByDesc(@PathVariable("description") String description,
			@RequestParam(value = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		
		logger.info("******** Get articles by description %s ********".formatted(description));
		
		PaginatedResponseList<ArticleDto> article = articleService.getByDescription(description, currentPage, pageSize);
		return new ResponseEntity<PaginatedResponseList<ArticleDto>>(article, HttpStatus.OK);
	}

	@PostMapping("/article/create")
	public ResponseEntity<InfoMsg> insArt(@Valid @RequestBody ArticleDto articleDto,
											BindingResult bindingResult)
			throws ItemAlreadyExistsException, NotFoundException, BindingException {

		logger.info("******** Created article %s ********".formatted(articleDto.getCodArt()));

		// Check article data validity
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessageSource.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(MsgErr);
			
			throw new BindingException(MsgErr);
		}
		
		// Check if the article to be created already exists
		ArticleDto article = articleService.getByCodArt(articleDto.getCodArt());
		if (article != null) {
			String errorMessage = "Article '%s - %s' already exists"
					.formatted(articleDto.getCodArt(), articleDto.getDescription());
			logger.warn(errorMessage);

			throw new ItemAlreadyExistsException(errorMessage);
		}
		
		articleService.create(articleDto);
		return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(), 
				String.format("Article creation '%s' performed successfully", articleDto.getCodArt())), HttpStatus.CREATED);
	}
	
	@PutMapping("/article/update")
	public ResponseEntity<InfoMsg> updArticle(@Valid @RequestBody ArticleDto articleDto,
												BindingResult bindingResult) throws BindingException, NotFoundException, ItemAlreadyExistsException {
		
		logger.info("******** Update of article %s ********".formatted(articleDto.getCodArt()));
		
		if (bindingResult.hasErrors()) {
			
			String msgErr = errMessageSource.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(msgErr);
			
			throw new BindingException(msgErr);
		}
		
		ArticleDto articleCheck = articleService.getByCodArt(articleDto.getCodArt());
		
		if (articleCheck == null) {
			String errMsg = String.format("Article '%s' already exists", articleDto.getCodArt());

			logger.warn(errMsg);
	
			throw new NotFoundException(errMsg);
		}
		
		articleService.create(articleDto);
		
		return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
				String.format("Update article '%s' performed successfully", articleDto.getCodArt())), HttpStatus.CREATED);
	}
	
	
	@DeleteMapping("/article/delete/{codart}")
	public ResponseEntity<ObjectNode> delArt(@PathVariable("codart") String codArt) throws NotFoundException, NotDeletableException {
		
		ArticleDto articleDto = articleService.getByCodArt(codArt);
		
		if (articleDto == null) {
			String errMessage = String.format("Article to be deleted '%s' was not found", codArt);
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		if (articleDto.getCodArt().equals(codartArtNotDeletable)) {
			String errMessage = String.format("Article '%s' not deletable", this.codartArtNotDeletable);
			logger.warn(errMessage);
			throw new NotDeletableException(errMessage);
		}

		Article article = articleMapper.toEntity(articleDto);
		articleService.delete(article);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode responseNode = mapper.createObjectNode();
		
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", String.format("Deleting article '%s - %s' performed successfully", article.getCodArt(), article.getDescription()));
		
		return new ResponseEntity<ObjectNode>(responseNode, HttpStatus.OK);
	}
}
