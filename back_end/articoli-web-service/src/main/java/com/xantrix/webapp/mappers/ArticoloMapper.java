package com.xantrix.webapp.mappers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.xantrix.webapp.common.mappers.BaseAlphaMapper;
import com.xantrix.webapp.dtos.ArticoloDto;
import com.xantrix.webapp.entities.Articolo;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.repository.ArticoloRepository;

@Component
public class ArticoloMapper extends BaseAlphaMapper<Articolo, ArticoloDto> {

	private final BarcodeMapper barcodeMapper;
	private final FamAssortMapper famAssortMapper;
	private final IngredientiMapper ingredientiMapper;
	private final IvaMapper ivaMapper;
	private Articolo articolo;

	public ArticoloMapper(BarcodeMapper barcodeMapper, FamAssortMapper famAssortMapper,
			IngredientiMapper ingredientiMapper, IvaMapper ivaMapper, ArticoloRepository articoloRepository) {
		this.barcodeMapper = barcodeMapper;
		this.famAssortMapper = famAssortMapper;
		this.ingredientiMapper = ingredientiMapper;
		this.ivaMapper = ivaMapper;
	}

	@Override
	public ArticoloDto toModel(Articolo entity, ArticoloDto model) {
		ArticoloDto articoloDto = null;

		if (entity != null) {
			articoloDto = Optional.ofNullable(model).orElseGet(ArticoloDto::new);
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
	public Articolo toEntity(ArticoloDto model, Articolo entity) {

		if (model != null) {

			articolo = Optional.ofNullable(entity).orElseGet(Articolo::new);

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
		articolo = Optional.ofNullable(articolo).orElseGet(() -> new Articolo());
		return articolo;
	}

}
