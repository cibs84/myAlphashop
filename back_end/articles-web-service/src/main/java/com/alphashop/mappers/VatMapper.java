package com.alphashop.mappers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alphashop.common.mappers.BaseAlphaMapper;
import com.alphashop.dtos.VatDto;
import com.alphashop.entities.Vat;

@Component
public class VatMapper extends BaseAlphaMapper<Vat, VatDto> {

	@Override
	public VatDto toModel(Vat entity, VatDto model) {
		VatDto vatDto = null;
		
		if (entity != null) {
			vatDto = Optional.ofNullable(model).orElseGet(VatDto::new);
			vatDto.setTaxRate(entity.getTaxRate());
			vatDto.setDescription(entity.getDescription());
			vatDto.setIdVat(entity.getIdVat());
		}
		
		return vatDto;
	}

	@Override
	public Vat toEntity(VatDto model, Vat entity) {
		Vat vat = null;
		
		if (model != null) {
			vat = Optional.ofNullable(entity).orElseGet(Vat::new);
			vat.setTaxRate(model.getTaxRate());
			vat.setDescription(model.getDescription());
			vat.setIdVat(model.getIdVat());
		}
		
		return vat;
	}

}
