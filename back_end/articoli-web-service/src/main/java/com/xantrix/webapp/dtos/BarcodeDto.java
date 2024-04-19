package com.xantrix.webapp.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BarcodeDto {

	@NotNull(message = "{NotNull.BarcodeDto.barcode.Validation}")
	private String barcode;
	@NotNull(message = "{NotNull.BarcodeDto.tipo.Validation}")
	private String tipo;
}
