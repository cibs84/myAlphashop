package com.alphashop.articles_web_service.mappers;

import java.util.Optional;
import org.springframework.stereotype.Component;
import com.alphashop.articles_web_service.common.mappers.BaseAlphaMapper;
import com.alphashop.articles_web_service.dtos.BarcodeDto;
import com.alphashop.articles_web_service.entities.Barcode;

@Component
public class BarcodeMapper extends BaseAlphaMapper<Barcode, BarcodeDto> {

	@Override
	public BarcodeDto toModel(Barcode entity, BarcodeDto model) {
		BarcodeDto barcodeDto = null;
		
		if (entity != null) {
			barcodeDto = Optional.ofNullable(model).orElseGet(BarcodeDto::new);
			barcodeDto.setBarcode(entity.getBarcode());
			barcodeDto.setType(entity.getIdTypeArt());
		}
		
		return barcodeDto;
	}

	@Override
	public Barcode toEntity(BarcodeDto model, Barcode entity) {
		Barcode barcode = null;
		
		if (model != null) {
			barcode = Optional.ofNullable(entity).orElseGet(Barcode::new);
			barcode.setBarcode(model.getBarcode());
			barcode.setIdTypeArt(model.getType());
		}
		
		return barcode;
	}

}
