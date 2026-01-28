package com.alphashop.articles_web_service.mappers;

import org.springframework.stereotype.Component;

import com.alphashop.articles_web_service.dtos.CategoryRefDto;
import com.alphashop.articles_web_service.dtos.CategoryResponseDto;
import com.alphashop.articles_web_service.entities.Category;

@Component
public class CategoryMapper {

	public CategoryResponseDto toModel(Category entity) {
		if(entity == null) return null;
		
		CategoryResponseDto categoryDto = new CategoryResponseDto();
		categoryDto.setDescription(entity.getDescription());
		categoryDto.setId(entity.getId());
		
		return categoryDto;
	}

	public Category toEntity(CategoryRefDto model) {
		if (model == null) return null;
		
		Category category = new Category();
		category.setId(model.getId());
		return category;
	}

}
