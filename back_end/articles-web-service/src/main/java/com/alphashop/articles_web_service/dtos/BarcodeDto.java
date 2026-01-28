package com.alphashop.articles_web_service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BarcodeDto {

	@NotBlank(message = "{validation.required}")
	private String barcode;
	
	@NotBlank(message = "{validation.required}")
	private String idTypeArt;
}
