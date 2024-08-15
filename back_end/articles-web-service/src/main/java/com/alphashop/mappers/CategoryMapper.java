package com.alphashop.mappers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alphashop.common.mappers.BaseAlphaMapper;
import com.alphashop.dtos.CategoryDto;
import com.alphashop.entities.Category;

@Component
public class CategoryMapper extends BaseAlphaMapper<Category, CategoryDto> {

	@Override
	public CategoryDto toModel(Category entity, CategoryDto model) {
		CategoryDto categoryDto = null;
		
		if (entity != null) {
			categoryDto = Optional.ofNullable(model).orElseGet(CategoryDto::new);
			categoryDto.setDescription(entity.getDescription());
			categoryDto.setId(entity.getId());
		}
		
		return categoryDto;
	}

	@Override
	public Category toEntity(CategoryDto model, Category entity) {
		Category category = null;
		
		if (model != null) {
			category = Optional.ofNullable(entity).orElseGet(Category::new);
			category.setDescription(model.getDescription());
			category.setId(model.getId());
		}
		
		return category;
	}

}
