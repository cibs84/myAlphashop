package com.xantrix.webapp.mappers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.xantrix.webapp.common.mappers.BaseAlphaMapper;
import com.xantrix.webapp.dtos.IvaDto;
import com.xantrix.webapp.entities.Iva;

@Component
public class IvaMapper extends BaseAlphaMapper<Iva, IvaDto> {

	@Override
	public IvaDto toModel(Iva entity, IvaDto model) {
		IvaDto ivaDto = null;
		
		if (entity != null) {
			ivaDto = Optional.ofNullable(model).orElseGet(IvaDto::new);
			ivaDto.setAliquota(entity.getAliquota());
			ivaDto.setDescrizione(entity.getDescrizione());
			ivaDto.setIdIva(entity.getIdIva());
		}
		
		return ivaDto;
	}

	@Override
	public Iva toEntity(IvaDto model, Iva entity) {
		Iva iva = null;
		
		if (model != null) {
			iva = Optional.ofNullable(entity).orElseGet(Iva::new);
			iva.setAliquota(model.getAliquota());
			iva.setDescrizione(model.getDescrizione());
			iva.setIdIva(model.getIdIva());
		}
		
		return iva;
	}

}
