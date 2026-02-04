package com.alphashop.articles_web_service.mappers;

import org.springframework.stereotype.Component;

import com.alphashop.articles_web_service.dtos.IngredientsDto;
import com.alphashop.articles_web_service.entities.Ingredients;

@Component
public class IngredientsMapper {

	public IngredientsDto toModel(Ingredients entity) {
		if (entity == null) return null;
		
		IngredientsDto ingredientsDto = new IngredientsDto();
		ingredientsDto.setCodart(entity.getCodart());
		ingredientsDto.setInfo(entity.getInfo());
		
		return ingredientsDto;
	}

	public Ingredients toEntity(IngredientsDto model) {
		if (model == null) return null;
		
		Ingredients ingredients = new Ingredients();
		ingredients.setCodart(model.getCodart());
		ingredients.setInfo(model.getInfo());
		
		return ingredients;
	}

}
