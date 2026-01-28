package com.alphashop.articles_web_service.mappers;

import org.springframework.stereotype.Component;

import com.alphashop.articles_web_service.dtos.BarcodeDto;
import com.alphashop.articles_web_service.entities.Barcode;

@Component
public class BarcodeMapper {

	public BarcodeDto toModel(Barcode entity) {
		BarcodeDto barcodeDto = new BarcodeDto();
		barcodeDto.setBarcode(entity.getBarcode());
		barcodeDto.setIdTypeArt(entity.getIdTypeArt());
		
		return barcodeDto;
	}

	public Barcode toEntity(BarcodeDto model) {
		Barcode barcode = new Barcode();
		barcode.setBarcode(model.getBarcode());
		barcode.setIdTypeArt(model.getIdTypeArt());
		
		return barcode;
	}

}
