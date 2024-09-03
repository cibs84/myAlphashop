package com.alphashop.dtos;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleDto {

	@NotBlank(message = "{NotBlank.ArticleDto.codArt.Validation}")
	@Size(min = 5, max = 20, message = "{Size.ArticleDto.codArt.Validation}")
	private String codArt;
	
	@NotBlank(message = "{NotBlank.ArticleDto.description.Validation}")
	@Size(min = 6, max = 80, message = "{Size.ArticleDto.description.Validation}")
	private String description;
	private String um;
	private String codStat;
	
	@Nullable
	@Positive(message = "{Positive.Validation}")
	@Max(value = 100, message = "{Size.ArticleDto.pcsCart.Validation}")
	private Integer pcsCart;
	
	private double netWeight;
	private String idArtStatus;
	private LocalDate creationDate;
	private double price = 0;

	private Set<BarcodeDto> barcodes = new HashSet<>();
	private IngredientsDto ingredients;
	private CategoryDto category;
	private VatDto vat;
	
	public void setDescription(String description) {
		this.description = description.trim(); 
	}
	
	public void setUm(String um) {
		this.um = um.toUpperCase(); 
	}
	
	public void setCodStat(String codStat){
		if (codStat != null) {
			this.codStat = codStat.trim();
		}
	}
}
