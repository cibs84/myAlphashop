package com.alphashop.articles_web_service.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.alphashop.articles_web_service.dtos.ArticleCreateRequestDto;
import com.alphashop.articles_web_service.dtos.ArticleResponseDto;
import com.alphashop.articles_web_service.dtos.ArticleUpdateRequestDto;
import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.exceptions.ItemAlreadyExistsException;
import com.alphashop.articles_web_service.exceptions.NotDeletableException;
import com.alphashop.articles_web_service.mappers.ArticleMapper;
import com.alphashop.articles_web_service.services.ArticleService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

	@Autowired
	ArticleService articleService;

	@Autowired
	ArticleMapper articleMapper;
	
	@Value("${codartNotDeletable}")
	private String codartNotDeletable;

	@GetMapping
	public ResponseEntity<PaginatedResponseList<Article, ArticleResponseDto>> find(
			@RequestParam(value = "description", required = false) Optional<String> description,
			@RequestParam(value = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize) {
		
		log.info("******** Search article by {} ********", description);
		
		String desc = description.filter(s -> !s.isBlank()).orElse("");
		
		PaginatedResponseList<Article, ArticleResponseDto> articlePagList = articleService.findByDescription(desc, currentPage, pageSize);
		return new ResponseEntity<PaginatedResponseList<Article, ArticleResponseDto>>(articlePagList, HttpStatus.OK);
	}

	@GetMapping(path = "/by-barcode/{barcode}")
	public ResponseEntity<ArticleResponseDto> findByBarcode(@PathVariable(name = "barcode") String barcode) {
		
		log.info("******** Get article with {} ********", barcode);

		ArticleResponseDto articleDto = articleService.findByBarcode(barcode);
		
		return new ResponseEntity<ArticleResponseDto>(articleDto, HttpStatus.OK);
	}

	@GetMapping(path = "/{codart}")
	public ResponseEntity<ArticleResponseDto> findByCodart(@PathVariable(name = "codart") String codart) {
		
		log.info("******** Get article with {} ********", codart);

		ArticleResponseDto articleDto = articleService.findByCodart(codart);
		
		return new ResponseEntity<ArticleResponseDto>(articleDto, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<ArticleResponseDto> create(
			@Valid @RequestBody ArticleCreateRequestDto articleDto) {

		log.info("******** Creation of article with codart {} ********", articleDto.getCodart());

		// Check if the article to be created already exists
		if (articleService.existsByCodart(articleDto.getCodart())) {
			String errorMessage = "Article '%s - %s' already exists"
					.formatted(articleDto.getCodart(), articleDto.getDescription());
			log.warn(errorMessage);

			throw new ItemAlreadyExistsException(errorMessage);
		}
		
		Article articleCreated = articleService.create(articleDto);
		ArticleResponseDto articleDtoCreated = articleMapper.toModel(articleCreated);
		return new ResponseEntity<ArticleResponseDto>(articleDtoCreated, HttpStatus.CREATED);
	}
	
	@PutMapping(path = "/{codart}")
	public ResponseEntity<ArticleResponseDto> update(
			@Valid @RequestBody ArticleUpdateRequestDto articleDto,
			@PathVariable(name = "codart") String codart) {
		
		log.info("******** Update article with codart {} ********", codart);
				
		Article updatedArt = articleService.update(articleDto, codart);
		ArticleResponseDto updatedArtDto = articleMapper.toModel(updatedArt);
		
		return new ResponseEntity<ArticleResponseDto>(updatedArtDto, HttpStatus.OK);
	}
	
	
	@DeleteMapping(path = "/{codart}")
	public ResponseEntity<Void> delete(@PathVariable(name = "codart") String codart) {
		
		log.info("******** Deleting of artilce with codart {} ********", codart);
		
		ArticleResponseDto articleDto = articleService.findByCodart(codart);
		
		if (articleDto.getCodart().equals(codartNotDeletable)) {
			String errMessage = String.format("Article '%s' not deletable", this.codartNotDeletable);
			log.warn(errMessage);
			throw new NotDeletableException(errMessage);
		}

		articleService.delete(codart);
		
		log.warn(String.format("Deleting article '%s - %s' performed successfully", articleDto.getCodart(), articleDto.getDescription()));
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
