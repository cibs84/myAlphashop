package com.alphashop.articles_web_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDto {
	
	@NotNull(message = "{NotNull.CategoryDto.id.Validation}")
	private Integer id;
	
	@NotBlank(message = "{NotBlank.CategoryDto.description.Validation}")
	private String description;
}
