package com.alphashop.articles_web_service.controllers;

import java.util.List;
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
import org.springframework.validation.ObjectError;
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

import com.alphashop.articles_web_service.common.PaginatedResponseList;
import com.alphashop.articles_web_service.dtos.ArticleDto;
import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.exceptions.BindingException;
import com.alphashop.articles_web_service.exceptions.ItemAlreadyExistsException;
import com.alphashop.articles_web_service.exceptions.NotDeletableException;
import com.alphashop.articles_web_service.exceptions.NotFoundException;
import com.alphashop.articles_web_service.mappers.ArticleMapper;
import com.alphashop.articles_web_service.services.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/articles")
// @CrossOrigin(origins = {"http://localhost:4200", 
// 						"http://34.124.165.164:8084" , "http://localhost:8084",
// 						"http://article-management:5051",
@CrossOrigin(origins = "*")
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

	@GetMapping("/find/all")
	public ResponseEntity<PaginatedResponseList<Article, ArticleDto>> listAll(
			@RequestParam(name = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(name = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		
		logger.info("******** Get all articles ********");

		PaginatedResponseList<Article, ArticleDto> articlePagList = articleService.getAll(currentPage, pageSize);
		
		return new ResponseEntity<PaginatedResponseList<Article, ArticleDto>>(articlePagList, HttpStatus.OK);
	}

	@GetMapping(path = {"/find/barcode/{ean}",
						"/find/barcode/**"})
	public ResponseEntity<ArticleDto> listArtByEan(@PathVariable(name = "ean", required = false) String ean) throws NotFoundException {

		if (ean == null) {
			throw new NotFoundException("Insert a valid barcode!");
		}
		
		logger.info("******** Get article with barcode %s ********".formatted(ean));

		ArticleDto articleDto = articleService.getByBarcode(ean);
		
		return new ResponseEntity<ArticleDto>(articleDto, HttpStatus.OK);
	}

	@GetMapping(path = {"/find/codart/{codart}", 
						"/find/codart/**"})
	public ResponseEntity<ArticleDto> listArtByCodArt(@PathVariable(name = "codart", required = false) String codArt) throws NotFoundException {

		if (codArt == null) {
			throw new NotFoundException("Insert a valid codArt!");
		}
		
		logger.info("******** Get article with codart %s ********".formatted(codArt));

		ArticleDto articleDto = articleService.getByCodArt(codArt);

		if (articleDto == null) {
			String errorMessage = "The article with codart " + codArt + " was not found!";
			logger.warn(errorMessage);

			throw new NotFoundException(errorMessage);
		}
		
		return new ResponseEntity<ArticleDto>(articleDto, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/find/description/{description}",
						"/find/description/**"})
	public ResponseEntity<PaginatedResponseList<Article, ArticleDto>> listArtByDesc(@PathVariable(name = "description", required = false) String description,
			@RequestParam(value = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		
		if (description == null) {
			throw new NotFoundException("Insert a valid description!");
		}
		
		logger.info("******** Get articles by description %s ********".formatted(description));
		
		PaginatedResponseList<Article, ArticleDto> articlePagList = articleService.getByDescription(description, currentPage, pageSize);
		return new ResponseEntity<PaginatedResponseList<Article, ArticleDto>>(articlePagList, HttpStatus.OK);
	}

	@PostMapping("/create")
	public ResponseEntity<ArticleDto> insArt(@Valid @RequestBody ArticleDto articleDto,
											BindingResult bindingResult)
			throws ItemAlreadyExistsException, NotFoundException, BindingException {

		logger.info("******** Created article %s ********".formatted(articleDto.getCodArt()));

		// Check article data validity
		if (bindingResult.hasErrors())
		{
			String msgErr = errMessageSource.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(msgErr);
			
			List<ObjectError> errorValidationList = bindingResult.getAllErrors();
			
			throw new BindingException(msgErr, errorValidationList);
		}
		
		// Check if the article to be created already exists
		ArticleDto article = articleService.getByCodArt(articleDto.getCodArt());
		if (article != null) {
			String errorMessage = "Article '%s - %s' already exists"
					.formatted(articleDto.getCodArt(), articleDto.getDescription());
			logger.warn(errorMessage);

			throw new ItemAlreadyExistsException(errorMessage);
		}
		
		Article articleCreated = articleService.create(articleDto);
		ArticleDto articleDtoCreated = articleMapper.toModel(articleCreated);
		return new ResponseEntity<ArticleDto>(articleDtoCreated, HttpStatus.CREATED);
	}
	
	@PutMapping("/update")
	public ResponseEntity<ArticleDto> updArticle(@Valid @RequestBody ArticleDto articleDto,
												BindingResult bindingResult) throws BindingException, NotFoundException, ItemAlreadyExistsException {
		
		logger.info("******** Update of article %s ********".formatted(articleDto.getCodArt()));
		
		if (bindingResult.hasErrors()) {
			
			String msgErr = errMessageSource.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(msgErr);
			
			List<ObjectError> errorValidationList = bindingResult.getAllErrors();
			
			throw new BindingException(msgErr, errorValidationList);
		}
		
		ArticleDto articleCheck = articleService.getByCodArt(articleDto.getCodArt());
		
		if (articleCheck == null) {
			String errMsg = String.format("Article '%s' doesn't exist", articleDto.getCodArt());

			logger.warn(errMsg);
	
			throw new NotFoundException(errMsg);
		}
		
		Article updatedArt = articleService.create(articleDto);
		ArticleDto updatedArtDto = articleMapper.toModel(updatedArt);
		
		return new ResponseEntity<ArticleDto>(updatedArtDto, HttpStatus.OK);
	}
	
	
	@DeleteMapping("/delete/{codart}")
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
