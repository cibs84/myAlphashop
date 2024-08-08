package com.xantrix.webapp.mappers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xantrix.webapp.common.mappers.BaseAlphaMapper;
import com.xantrix.webapp.dtos.BarcodeDto;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.repository.ArticoloRepository;

@Component
public class BarcodeMapper extends BaseAlphaMapper<Barcode, BarcodeDto> {

	@Autowired
	ArticoloRepository articoloRepository;
	
	@Override
	public BarcodeDto toModel(Barcode entity, BarcodeDto model) {
		BarcodeDto barcodeDto = null;
		
		if (entity != null) {
			barcodeDto = Optional.ofNullable(model).orElseGet(BarcodeDto::new);
			barcodeDto.setBarcode(entity.getBarcode());
			barcodeDto.setTipo(entity.getIdTipoArt());
		}
		
		return barcodeDto;
	}

	@Override
	public Barcode toEntity(BarcodeDto model, Barcode entity) {
		Barcode barcode = null;
		
		if (model != null) {
			barcode = Optional.ofNullable(entity).orElseGet(Barcode::new);
			barcode.setBarcode(model.getBarcode());
			barcode.setIdTipoArt(model.getTipo());
		}
		
		return barcode;
	}

}
