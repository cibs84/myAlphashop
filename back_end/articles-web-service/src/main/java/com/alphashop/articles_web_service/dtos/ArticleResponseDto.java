package com.alphashop.articles_web_service.dtos;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

@Data
public class ArticleResponseDto {

	private String codart;
	private String description;
	private String um;
	private String codStat;
	private Integer pcsCart;
	private Double netWeight;
	private Integer idArtStatus;
	private LocalDate creationDate;
	private Double price;
	private Set<BarcodeDto> barcodes;
	private IngredientsDto ingredients;
	private CategoryResponseDto category;
	private VatResponseDto vat;
	
	public void setDescription(String description) {
		this.description = description.trim(); 
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
