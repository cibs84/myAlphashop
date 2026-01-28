package com.alphashop.articles_web_service.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRefDto {

	@NotNull(message = "{validation.required}")
	private Integer id;
}
