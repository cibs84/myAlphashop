package com.alphashop.articles_web_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IngredientsDto {
	
	@NotBlank(message = "{validation.required}")
	private String codart;
	
	@NotBlank(message = "{validation.required}")
	@Size(max = 300, message = "{validation.sizeMax}")
	private String info;
	
	public void setInfo(String info) {
		this.info = info.trim().toUpperCase();
	}
}
