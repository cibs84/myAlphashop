package com.alphashop.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IngredientsDto {
	
	@NotBlank(message = "{NotBlank.IngredientsDto.codArt.Validation}")
	private String codArt;
	
	@NotBlank(message = "{NotBlank.IngredientsDto.info.Validation}")
	private String info;
}
