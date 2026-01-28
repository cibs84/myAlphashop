package com.alphashop.articles_web_service.mappers;

import org.springframework.stereotype.Component;

import com.alphashop.articles_web_service.dtos.VatRefDto;
import com.alphashop.articles_web_service.dtos.VatResponseDto;
import com.alphashop.articles_web_service.entities.Vat;

@Component
public class VatMapper {

	public VatResponseDto toModel(Vat entity) {
		if (entity == null) return null;
		
		VatResponseDto vatDto = new VatResponseDto();
		vatDto.setTaxRate(entity.getTaxRate());
		vatDto.setDescription(entity.getDescription());
		vatDto.setIdVat(entity.getIdVat());
		
		return vatDto;
	}

	public Vat toEntity(VatRefDto model) {
		if (model == null) return null;
		
		Vat vat = new Vat();
		vat.setIdVat(model.getIdVat());
		return vat;
	}

}
