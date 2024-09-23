package com.alphashop.articles_web_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alphashop.articles_web_service.dtos.CategoryDto;
import com.alphashop.articles_web_service.entities.Category;
import com.alphashop.articles_web_service.exceptions.NotFoundException;
import com.alphashop.articles_web_service.mappers.CategoryMapper;
import com.alphashop.articles_web_service.repositories.CategoryRepository;

@Service
@Transactional(readOnly = true)
public class CategoryService {

	private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	public CategoryService(CategoryRepository categoryRepository, 
						   CategoryMapper categoryMapper) {
		this.categoryRepository = categoryRepository;
		this.categoryMapper = categoryMapper;
	}

	public List<CategoryDto> getAll() throws NotFoundException {
		List<Category> categories = categoryRepository.findAll();
		
		if (categories.isEmpty()) {
			String errMessage = "No category was found";
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		List<CategoryDto> categoriesDto = categories.stream().map(category -> categoryMapper.toModel(category))
				.collect(Collectors.toList());
		
		return categoriesDto;
	}
}
