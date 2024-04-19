package com.xantrix.webapp.mappers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.xantrix.webapp.common.mappers.BaseAlphaMapper;
import com.xantrix.webapp.dtos.CategoriaDto;
import com.xantrix.webapp.entities.FamAssort;

@Component
public class FamAssortMapper extends BaseAlphaMapper<FamAssort, CategoriaDto> {

	@Override
	public CategoriaDto toModel(FamAssort entity, CategoriaDto model) {
		CategoriaDto categoriaDto = null;
		
		if (entity != null) {
			categoriaDto = Optional.ofNullable(model).orElseGet(CategoriaDto::new);
			categoriaDto.setDescrizione(entity.getDescrizione());
			categoriaDto.setId(entity.getId());
		}
		
		return categoriaDto;
	}

	@Override
	public FamAssort toEntity(CategoriaDto model, FamAssort entity) {
		FamAssort famAssort = null;
		
		if (model != null) {
			famAssort = Optional.ofNullable(entity).orElseGet(FamAssort::new);
			famAssort.setDescrizione(model.getDescrizione());
			famAssort.setId(model.getId());
		}
		
		return famAssort;
	}

}
