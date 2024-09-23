package com.alphashop.articles_web_service.mappers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alphashop.articles_web_service.common.mappers.BaseAlphaMapper;
import com.alphashop.articles_web_service.dtos.IngredientsDto;
import com.alphashop.articles_web_service.entities.Ingredients;

@Component
public class IngredientsMapper extends BaseAlphaMapper<Ingredients, IngredientsDto> {

	@Override
	public IngredientsDto toModel(Ingredients entity, IngredientsDto model) {
		IngredientsDto ingredientsDto = null;
		
		if (entity != null) {
			ingredientsDto = Optional.ofNullable(model).orElseGet(IngredientsDto::new);
			ingredientsDto.setCodArt(entity.getCodArt());
			ingredientsDto.setInfo(entity.getInfo());
		}
		
		return ingredientsDto;
	}

	@Override
	public Ingredients toEntity(IngredientsDto model, Ingredients entity) {
		Ingredients ingredients = null;
		
		if (model != null) {
			ingredients = Optional.ofNullable(entity).orElseGet(Ingredients::new);
			ingredients.setCodArt(model.getCodArt());
			ingredients.setInfo(model.getInfo());
		}
		
		return ingredients;
	}

}
