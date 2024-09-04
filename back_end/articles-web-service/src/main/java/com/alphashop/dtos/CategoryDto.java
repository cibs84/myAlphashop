package com.alphashop.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CategoryDto {
	
	@NotNull(message = "{NotNull.CategoryDto.id.Validation}")
	@PositiveOrZero(message = "{PositiveOrZero.Validation}")
	private Integer id;
	
	@NotBlank(message = "{NotBlank.CategoryDto.description.Validation}")
	private String description;
}
