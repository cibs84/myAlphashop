package com.alphashop.mappers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alphashop.common.mappers.BaseAlphaMapper;
import com.alphashop.dtos.CategoryDto;
import com.alphashop.entities.FamAssort;

@Component
public class FamAssortMapper extends BaseAlphaMapper<FamAssort, CategoryDto> {

	@Override
	public CategoryDto toModel(FamAssort entity, CategoryDto model) {
		CategoryDto categoryDto = null;
		
		if (entity != null) {
			categoryDto = Optional.ofNullable(model).orElseGet(CategoryDto::new);
			categoryDto.setDescription(entity.getDescription());
			categoryDto.setId(entity.getId());
		}
		
		return categoryDto;
	}

	@Override
	public FamAssort toEntity(CategoryDto model, FamAssort entity) {
		FamAssort famAssort = null;
		
		if (model != null) {
			famAssort = Optional.ofNullable(entity).orElseGet(FamAssort::new);
			famAssort.setDescription(model.getDescription());
			famAssort.setId(model.getId());
		}
		
		return famAssort;
	}

}
