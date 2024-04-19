package com.xantrix.webapp.dtos;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticoliDto {

	@Size(min = 5, max = 20, message = "{Size.ArticoliDto.codArt.Validation}")
	@NotNull(message = "{NotNull.ArticoliDto.codArt.Validation}")
	private String codArt;
	
	@Size(min = 6, max = 80, message = "{Size.ArticoliDto.descrizione.Validation}")
	private String descrizione;
	private String um;
	private String codStat;
	
	@Max(value = 99, message = "{Max.ArticoliDto.pzCart.Validation}")
	private Integer pzCart;
	
	@Min(value = (long) 0.01, message = "{Min.ArticoliDto.pesoNetto.Validation}")
	private double pesoNetto;
	private String idStatoArt;
	private Date dataCreazione;
	private double prezzo = 0;

	private Set<BarcodeDto> barcode = new HashSet<>();
	private IngredientiDto ingredienti;
	private CategoriaDto famAssort;
	private IvaDto iva;
}
