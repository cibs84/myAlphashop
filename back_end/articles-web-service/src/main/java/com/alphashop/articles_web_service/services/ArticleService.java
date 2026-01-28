package com.alphashop.articles_web_service.services;

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

import com.alphashop.articles_web_service.common.PaginatedResponseList;
import com.alphashop.articles_web_service.dtos.ArticleCreateRequestDto;
import com.alphashop.articles_web_service.dtos.ArticleResponseDto;
import com.alphashop.articles_web_service.dtos.ArticleUpdateRequestDto;
import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.exceptions.ItemAlreadyExistsException;
import com.alphashop.articles_web_service.exceptions.NotFoundException;
import com.alphashop.articles_web_service.mappers.ArticleMapper;
import com.alphashop.articles_web_service.repositories.ArticleRepository;

@Service
@Transactional(readOnly = true)
public class ArticleService {

	private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);
	private final ArticleRepository articleRepository;
	private final ArticleMapper articleMapper;

	public ArticleService(ArticleRepository articleRepository, 
						   ArticleMapper articleMapper) {
		this.articleRepository = articleRepository;
		this.articleMapper = articleMapper;
	}

	public PaginatedResponseList<Article, ArticleResponseDto> findByDescription(String description, Optional<Integer> currentPage,
			Optional<Integer> pageSize) throws NotFoundException {

		String descriptionMod = "%" + description.toUpperCase() + "%";

		Pageable articlesPagination = PageRequest.of(
				currentPage.map(n -> n-1).filter(n -> n > -1).orElse(0),
				pageSize.filter(n -> n > 0).orElse(10)
		);
		
		Page<Article> articleList = 
				articleRepository.findByDescriptionLikeOrderByCodartAsc(descriptionMod, articlesPagination);
		
		if (articleList.isEmpty()) {
			String errMessage = description.isEmpty() 
					? "No articles were found" 
					: "No article with description '%s' was found".formatted(description);
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		// Converts Page<Article> to List<ArticleDto>
		List<ArticleResponseDto> articleDtoList = articleList.stream().map(art -> articleMapper.toModel(art))
															  .collect(Collectors.toList());

		PaginatedResponseList<Article, ArticleResponseDto> articleResponse = new PaginatedResponseList<Article, ArticleResponseDto>(articleList, articleDtoList);
		
		return articleResponse;
	}
	
	private Article findEntityByCodart(String codart) throws NotFoundException {
		return articleRepository.findByCodart(codart)
				.orElseThrow(() -> {
					String msg = "The article with codart " + codart + " was not found!";
					logger.warn(msg);
					return new NotFoundException(msg);
				});
	}

	public ArticleResponseDto findByCodart(String codart) throws NotFoundException {
		
		Article article = findEntityByCodart(codart);
		
		ArticleResponseDto articleDto = articleMapper.toModel(article);
		
		return articleDto;
	}
	
	public boolean existsByCodart(String codart) {
		if (codart == null || codart.isBlank()) {
			return false;
		}
		return articleRepository.existsByCodart(codart);
	}

	public ArticleResponseDto findByBarcode(String barcode) throws NotFoundException {

		Optional<Article> article = articleRepository.selByEan(barcode);
		
		if (article.isEmpty()) {

			// This error message may not be used in the frontend
			String errMessage = "The article with barcode '%s' was not found!".formatted(barcode);
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		ArticleResponseDto articleDto = articleMapper.toModel(article.get());
		
		return articleDto;
	}

	@Transactional
	public Article create(ArticleCreateRequestDto articleDto) throws ItemAlreadyExistsException {
		Article article = articleMapper.createToEntity(articleDto);
		return articleRepository.save(article);
	}
	
	@Transactional
	public Article update(ArticleUpdateRequestDto articleDto, String codart) throws NotFoundException {
		Article article = findEntityByCodart(codart);
		articleMapper.updateToEntity(articleDto, article);
		return articleRepository.save(article);
	}

	@Transactional
	public void delete(String codart) throws NotFoundException {
		Article article = findEntityByCodart(codart);
		articleRepository.delete(article);
	}
}
