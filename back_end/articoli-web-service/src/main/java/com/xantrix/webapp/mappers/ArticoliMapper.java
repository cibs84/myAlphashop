package com.xantrix.webapp.mappers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.xantrix.webapp.common.mappers.BaseAlphaMapper;
import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.dtos.BarcodeDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.repository.ArticoliRepository;

@Component
public class ArticoliMapper extends BaseAlphaMapper<Articoli, ArticoliDto> {

	private final BarcodeMapper barcodeMapper;
	private final FamAssortMapper famAssortMapper;
	private final IngredientiMapper ingredientiMapper;
	private final IvaMapper ivaMapper;
	private Articoli articolo;

	public ArticoliMapper(BarcodeMapper barcodeMapper, FamAssortMapper famAssortMapper,
			IngredientiMapper ingredientiMapper, IvaMapper ivaMapper, ArticoliRepository articoliRepository) {
		this.barcodeMapper = barcodeMapper;
		this.famAssortMapper = famAssortMapper;
		this.ingredientiMapper = ingredientiMapper;
		this.ivaMapper = ivaMapper;
	}

	@Override
	public ArticoliDto toModel(Articoli entity, ArticoliDto model) {
		ArticoliDto articoloDto = null;

		if (entity != null) {
			articoloDto = Optional.ofNullable(model).orElseGet(ArticoliDto::new);
			articoloDto.setBarcode(Optional.ofNullable(entity.getBarcode()).orElseGet(HashSet::new)
					.stream()
					.map(barcodeMapper::toModel)
					.collect(Collectors.toSet()));
			articoloDto.setCodArt(entity.getCodArt());
			articoloDto.setCodStat(entity.getCodStat());
			articoloDto.setDataCreazione(entity.getDataCreaz());
			articoloDto.setDescrizione(entity.getDescrizione());
			articoloDto.setFamAssort(famAssortMapper.toModel(entity.getFamAssort()));
			articoloDto.setIdStatoArt(entity.getIdStatoArt());
			articoloDto.setIngredienti(ingredientiMapper.toModel(entity.getIngredienti()));
			articoloDto.setIva(ivaMapper.toModel(entity.getIva()));
			articoloDto.setPesoNetto(entity.getPesoNetto());
			articoloDto.setPzCart(entity.getPzCart());
			articoloDto.setUm(entity.getUm());
		}

		return articoloDto;
	}

	@Override
	public Articoli toEntity(ArticoliDto model, Articoli entity) {

		if (model != null) {

			articolo = Optional.ofNullable(entity).orElseGet(Articoli::new);

			articolo.setCodArt(model.getCodArt());
			articolo.setCodStat(model.getCodStat());
			articolo.setDataCreaz(model.getDataCreazione());
			articolo.setDescrizione(model.getDescrizione());
			articolo.setFamAssort(famAssortMapper.toEntity(model.getFamAssort()));
			articolo.setIdStatoArt(model.getIdStatoArt());
			articolo.setIngredienti(ingredientiMapper.toEntity(model.getIngredienti()));
			articolo.setIva(ivaMapper.toEntity(model.getIva()));
			articolo.setPesoNetto(model.getPesoNetto());
			articolo.setPzCart(model.getPzCart());
			articolo.setUm(model.getUm());

			// ---------------------- SET-BARCODE ----------------------
			Set<Barcode> barcodes = Optional.ofNullable(model.getBarcode()).orElseGet(HashSet::new)
					.stream().map(barcodeDto -> barcodeMapper.toEntity(barcodeDto))
					.collect(Collectors.toSet());
			barcodes.forEach(barcode -> barcode.setArticolo(articolo));
			articolo.setBarcode(barcodes);
			// ---------------------------------------------------------
		}
		articolo = Optional.ofNullable(articolo).orElseGet(() -> new Articoli());
		return articolo;
	}

}
