package com.alphashop.articles_web_service.dtos;

import lombok.Data;

@Data
public class VatResponseDto {
	private Integer idVat;
	private String description;
	private Integer taxRate;
}
