package com.alphashop.articles_web_service.dtos;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleUpdateRequestDto {

	@Nullable
    @Length(min = 6, message = "{validation.minLength}")
    @Length(max = 80, message = "{validation.maxLength}")
	private String description;
	
	@Nullable
	private String um;
	
	@Nullable
	private String codStat;
	
	@Nullable
	@Min(value = 0, message = "{validation.minMax}")
	@Max(value = 100, message = "{validation.minMax}")
	private Integer pcsCart;
	
	@Nullable
	@Positive(message = "{validation.positive}")
	@Min(value = (long) 0.01, message = "{validation.minMax}")
	private Double netWeight;
	
	@Nullable
	@PositiveOrZero(message = "{validation.positiveOrZero}")
	private Integer idArtStatus;
	
	@Nullable
	@PositiveOrZero(message = "{validation.positiveOrZero}")
	private Double price;

	@Nullable
	@Valid
	private Set<BarcodeDto> barcodes;
	
	@Nullable
	@Valid
	private IngredientsDto ingredients;
	
	@Nullable
	@Valid
	private CategoryRefDto category;
	
	@Nullable
	@Valid
	private VatRefDto vat;
	
	public void setDescription(String description) {
		this.description = description.trim().toUpperCase(); 
	}
	
	public void setUm(String um) {
		if (um != null) {
			this.um = um.toUpperCase();
		}
	}
	
	public void setCodStat(String codStat){
		if (codStat != null) {
			this.codStat = codStat.trim();
		}
	}
}
