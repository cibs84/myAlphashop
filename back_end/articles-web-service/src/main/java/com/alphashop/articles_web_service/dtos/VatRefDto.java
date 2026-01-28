package com.alphashop.articles_web_service.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class VatRefDto {
	
	@NotNull(message = "{validation.required}")
	@PositiveOrZero(message = "{validation.positiveOrZero}")
	private Integer idVat;
}
