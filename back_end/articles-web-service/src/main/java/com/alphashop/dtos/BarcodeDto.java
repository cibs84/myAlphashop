package com.alphashop.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BarcodeDto {

	@NotBlank(message = "{NotBlank.BarcodeDto.barcode.Validation}")
	private String barcode;
	
	@NotBlank(message = "{NotBlank.BarcodeDto.type.Validation}")
	private String type;
}
