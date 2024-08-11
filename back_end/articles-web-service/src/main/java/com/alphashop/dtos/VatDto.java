package com.alphashop.dtos;

import lombok.Data;

@Data
public class VatDto {

	private Integer idVat;
	private String description;
	private Integer taxRate;
}
