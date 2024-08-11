package com.alphashop.mappers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alphashop.common.mappers.BaseAlphaMapper;
import com.alphashop.dtos.ArticleDto;
import com.alphashop.entities.Article;
import com.alphashop.entities.Barcode;
import com.alphashop.repositories.ArticleRepository;

@Component
public class ArticleMapper extends BaseAlphaMapper<Article, ArticleDto> {

	private final BarcodeMapper barcodeMapper;
	private final FamAssortMapper famAssortMapper;
	private final IngredientsMapper ingredientsMapper;
	private final VatMapper vatMapper;
	private Article article;

	public ArticleMapper(BarcodeMapper barcodeMapper, FamAssortMapper famAssortMapper,
			IngredientsMapper ingredientsMapper, VatMapper vatMapper, ArticleRepository articleRepository) {
		this.barcodeMapper = barcodeMapper;
		this.famAssortMapper = famAssortMapper;
		this.ingredientsMapper = ingredientsMapper;
		this.vatMapper = vatMapper;
	}

	@Override
	public ArticleDto toModel(Article entity, ArticleDto model) {
		ArticleDto articleDto = null;

		if (entity != null) {
			articleDto = Optional.ofNullable(model).orElseGet(ArticleDto::new);
			articleDto.setBarcode(Optional.ofNullable(entity.getBarcode()).orElseGet(HashSet::new)
					.stream()
					.map(barcodeMapper::toModel)
					.collect(Collectors.toSet()));
			articleDto.setCodArt(entity.getCodArt());
			articleDto.setCodStat(entity.getCodStat());
			articleDto.setCreationDate(entity.getCreationDate());
			articleDto.setDescription(entity.getDescription());
			articleDto.setFamAssort(famAssortMapper.toModel(entity.getFamAssort()));
			articleDto.setIdArtStatus(entity.getIdArtStatus());
			articleDto.setIngredients(ingredientsMapper.toModel(entity.getIngredients()));
			articleDto.setVat(vatMapper.toModel(entity.getVat()));
			articleDto.setNetWeight(entity.getNetWeight());
			articleDto.setPzCart(entity.getPzCart());
			articleDto.setUm(entity.getUm());
		}

		return articleDto;
	}

	@Override
	public Article toEntity(ArticleDto model, Article entity) {

		if (model != null) {

			article = Optional.ofNullable(entity).orElseGet(Article::new);

			article.setCodArt(model.getCodArt());
			article.setCodStat(model.getCodStat());
			article.setCreationDate(model.getCreationDate());
			article.setDescription(model.getDescription());
			article.setFamAssort(famAssortMapper.toEntity(model.getFamAssort()));
			article.setIdArtStatus(model.getIdArtStatus());
			article.setIngredients(ingredientsMapper.toEntity(model.getIngredients()));
			article.setVat(vatMapper.toEntity(model.getVat()));
			article.setNetWeight(model.getNetWeight());
			article.setPzCart(model.getPzCart());
			article.setUm(model.getUm());

			// ---------------------- SET-BARCODE ----------------------
			Set<Barcode> barcodes = Optional.ofNullable(model.getBarcode()).orElseGet(HashSet::new)
					.stream().map(barcodeDto -> barcodeMapper.toEntity(barcodeDto))
					.collect(Collectors.toSet());
			barcodes.forEach(barcode -> barcode.setArticle(article));
			article.setBarcode(barcodes);
			// ---------------------------------------------------------
		}
		article = Optional.ofNullable(article).orElseGet(() -> new Article());
		return article;
	}

}
