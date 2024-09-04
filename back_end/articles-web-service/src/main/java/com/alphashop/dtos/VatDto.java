package com.alphashop.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class VatDto {
	
	@NotNull(message = "{NotNull.VatDto.idVat.Validation}")
	@PositiveOrZero(message = "{PositiveOrZero.Validation}")
	private Integer idVat;
	
	@NotBlank(message = "{NotBlank.VatDto.description.Validation}")
	private String description;
	
	@NotNull(message = "{NotNull.VatDto.taxRate.Validation}")
	@PositiveOrZero(message = "{PositiveOrZero.Validation}")
	private Integer taxRate;
}
