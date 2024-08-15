package com.alphashop.dtos;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleDto {

	@Size(min = 5, max = 20, message = "{Size.ArticleDto.codArt.Validation}")
	@NotNull(message = "{NotNull.ArticleDto.codArt.Validation}")
	private String codArt;
	
	@Size(min = 6, max = 80, message = "{Size.ArticleDto.description.Validation}")
	private String description;
	private String um;
	private String codStat;
	
	@Max(value = 99, message = "{Max.ArticleDto.pcsCart.Validation}")
	private Integer pcsCart;
	
	@Min(value = (long) 0.01, message = "{Min.ArticleDto.netWeight.Validation}")
	private double netWeight;
	private String idArtStatus;
	private Date creationDate;
	private double price = 0;

	private Set<BarcodeDto> barcode = new HashSet<>();
	private IngredientsDto ingredients;
	private CategoryDto category;
	private VatDto vat;
	
	public void setDescription(String description) {
		this.description = description.trim(); 
	}
}
