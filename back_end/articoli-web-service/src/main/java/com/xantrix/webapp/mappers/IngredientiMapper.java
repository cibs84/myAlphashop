package com.xantrix.webapp.mappers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.xantrix.webapp.common.mappers.BaseAlphaMapper;
import com.xantrix.webapp.dtos.IngredientiDto;
import com.xantrix.webapp.entities.Ingredienti;

@Component
public class IngredientiMapper extends BaseAlphaMapper<Ingredienti, IngredientiDto> {

	@Override
	public IngredientiDto toModel(Ingredienti entity, IngredientiDto model) {
		IngredientiDto ingredientiDto = null;
		
		if (entity != null) {
			ingredientiDto = Optional.ofNullable(model).orElseGet(IngredientiDto::new);
			ingredientiDto.setCodArt(entity.getCodArt());
			ingredientiDto.setInfo(entity.getInfo());
		}
		
		return ingredientiDto;
	}

	@Override
	public Ingredienti toEntity(IngredientiDto model, Ingredienti entity) {
		Ingredienti ingredienti = null;
		
		if (model != null) {
			ingredienti = Optional.ofNullable(entity).orElseGet(Ingredienti::new);
			ingredienti.setCodArt(model.getCodArt());
			ingredienti.setInfo(model.getInfo());
		}
		
		return ingredienti;
	}

}
