package com.alphashop.dtos;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleDto {

	@NotBlank(message = "{NotBlank.ArticlesDto.codArt.Validation}")
	@Size(min = 5, max = 20, message = "{Size.ArticlesDto.codArt.Validation}")
	private String codArt;
	
	@NotBlank(message = "{NotBlank.ArticlesDto.description.Validation}")
	@Size(min = 6, max = 80, message = "{Size.ArticlesDto.description.Validation}")
	private String description;
	
	@NotBlank(message = "{NotBlank.ArticlesDto.um.Validation}")
	private String um;
	private String codStat;
	
	@PositiveOrZero(message = "{PositiveOrZero.Validation}")
	@Max(value = 100, message = "{Max.ArticlesDto.pcsCart.Validation}")
	private Integer pcsCart;
	
	@PositiveOrZero(message = "{PositiveOrZero.Validation}")
	private Double netWeight;
	
	@PositiveOrZero(message = "{PositiveOrZero.Validation}")
	private String idArtStatus;
	private LocalDate creationDate;
	
	@PositiveOrZero(message = "{PositiveOrZero.Validation}")
	private Double price;

	@Valid
	private Set<BarcodeDto> barcodes = new HashSet<>();
	
	@Valid
	private IngredientsDto ingredients;
	
	@Valid
	private CategoryDto category;
	
	@Valid
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
