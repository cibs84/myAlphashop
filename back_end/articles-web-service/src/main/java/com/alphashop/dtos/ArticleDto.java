package com.alphashop.dtos;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
	@Min(value = 0, message = "{MinMax.ArticleDto.pcsCart.Validation}")
	@Max(value = 100, message = "{MinMax.ArticleDto.pcsCart.Validation}")
	private Integer pcsCart;
	
	@Nullable
	@Positive(message = "{Positive.Validation}")
	@Min(value = (long) 0.01, message = "{Min.ArticleDto.netWeight.Validation}")
	private Double netWeight;
	
	@PositiveOrZero(message = "{PositiveOrZero.Validation}")
	private String idArtStatus;
	
	@Temporal(TemporalType.DATE)
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
	@NotNull
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
