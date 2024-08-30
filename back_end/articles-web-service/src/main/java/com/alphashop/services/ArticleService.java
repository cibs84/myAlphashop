package com.alphashop.services;

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

import com.alphashop.common.PaginatedResponseList;
import com.alphashop.dtos.ArticleDto;
import com.alphashop.entities.Article;
import com.alphashop.exceptions.ItemAlreadyExistsException;
import com.alphashop.exceptions.NotFoundException;
import com.alphashop.mappers.ArticleMapper;
import com.alphashop.repositories.ArticleRepository;

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

	public PaginatedResponseList<ArticleDto> getAll(Optional<Integer> currentPage, Optional<Integer> pageSize) throws NotFoundException {
		Pageable articlesPagination = PageRequest.of(currentPage.filter(n -> n > -1).orElse(1),
				pageSize.filter(n -> n > 0).orElse(10));
		Page<Article> article = articleRepository.findAllByOrderByCodArtAsc(articlesPagination);
		
		if (article.isEmpty()) {
			String errMessage = "No articles were found";
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		List<ArticleDto> articleDto = article.stream().map(art -> articleMapper.toModel(art))
				.collect(Collectors.toList());
		
		PaginatedResponseList<ArticleDto> articleResponse = new PaginatedResponseList<>(article, articleDto);
		
		return articleResponse;
	}

	public PaginatedResponseList<ArticleDto> getByDescription(String description, Optional<Integer> currentPage,
			Optional<Integer> pageSize) throws NotFoundException {

		description = description == "" ? " " : description;
		
		String descriptionMod = "%" + description.toUpperCase() + "%";

		Pageable articlesPagination = PageRequest.of(currentPage.map(n -> n-1).filter(n -> n > -1).orElse(0),
				pageSize.filter(n -> n > 0).orElse(10));
		
		Page<Article> article = articleRepository.findByDescriptionLikeOrderByCodArtAsc(descriptionMod, articlesPagination);
		
		if (article.isEmpty()) {
			String errMessage = description.trim() == "" ? "No articles were found" 
					: "No article with description '%s' was found".formatted(description);
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		// Converts Page<Article> to List<ArticleDto>
		List<ArticleDto> articleDto = article.stream().map(art -> articleMapper.toModel(art))
				.collect(Collectors.toList());

		PaginatedResponseList<ArticleDto> articleResponse = new PaginatedResponseList<ArticleDto>(article, articleDto);
		
		
		return articleResponse;
	}

	public ArticleDto getByCodArt(String codArt) throws NotFoundException {
		Optional<Article> article = articleRepository.findByCodArt(codArt);
		ArticleDto articleDto = null;
		
		if (article.isPresent()) {
			articleDto = articleMapper.toModel(article.get());
		}
		
		return articleDto;
	}

	public ArticleDto getByBarcode(String ean) throws NotFoundException {
		
		Optional<Article> article = articleRepository.selByEan(ean);
		
		if (article.isEmpty()) {

			// This error message may not be used in the frontend
			String errMessage = "The article with barcode '%s' was not found!".formatted(ean);
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		ArticleDto articleDto = articleMapper.toModel(article.get());
		
		return articleDto;
	}

	public boolean existsByCodArt(String codArt) {
		return articleRepository.existsByCodArt(codArt);
	}

	@Transactional
	public Article create(ArticleDto articleDto) throws ItemAlreadyExistsException {
		Article article = articleMapper.toEntity(articleDto);
		article.setDescription(article.getDescription().toUpperCase());
		return articleRepository.save(article);
	}

	@Transactional
	public void delete(Article article) {
		articleRepository.delete(article);
	}
}
