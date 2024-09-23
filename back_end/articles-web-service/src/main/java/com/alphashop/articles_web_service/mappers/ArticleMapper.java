package com.alphashop.articles_web_service.mappers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alphashop.articles_web_service.common.mappers.BaseAlphaMapper;
import com.alphashop.articles_web_service.dtos.ArticleDto;
import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.entities.Barcode;
import com.alphashop.articles_web_service.repositories.ArticleRepository;

@Component
public class ArticleMapper extends BaseAlphaMapper<Article, ArticleDto> {

	private final BarcodeMapper barcodeMapper;
	private final CategoryMapper categoryMapper;
	private final IngredientsMapper ingredientsMapper;
	private final VatMapper vatMapper;
	private Article article;

	public ArticleMapper(BarcodeMapper barcodeMapper, CategoryMapper categoryMapper,
			IngredientsMapper ingredientsMapper, VatMapper vatMapper, ArticleRepository articleRepository) {
		this.barcodeMapper = barcodeMapper;
		this.categoryMapper = categoryMapper;
		this.ingredientsMapper = ingredientsMapper;
		this.vatMapper = vatMapper;
	}

	@Override
	public ArticleDto toModel(Article entity, ArticleDto model) {
		ArticleDto articleDto = null;

		if (entity != null) {
			articleDto = Optional.ofNullable(model).orElseGet(ArticleDto::new);
			articleDto.setBarcodes(Optional.ofNullable(entity.getBarcodes()).orElseGet(HashSet::new)
					.stream()
					.map(barcodeMapper::toModel)
					.collect(Collectors.toSet()));
			articleDto.setCodArt(entity.getCodArt());
			articleDto.setCodStat(entity.getCodStat());
			articleDto.setCreationDate(entity.getCreationDate());
			articleDto.setDescription(entity.getDescription());
			articleDto.setCategory(categoryMapper.toModel(entity.getCategory()));
			articleDto.setIdArtStatus(entity.getIdArtStatus());
			articleDto.setIngredients(ingredientsMapper.toModel(entity.getIngredients()));
			articleDto.setVat(vatMapper.toModel(entity.getVat()));
			articleDto.setNetWeight(entity.getNetWeight());
			articleDto.setPcsCart(entity.getPcsCart());
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
			article.setCategory(categoryMapper.toEntity(model.getCategory()));
			article.setIdArtStatus(model.getIdArtStatus());
			article.setIngredients(ingredientsMapper.toEntity(model.getIngredients()));
			article.setVat(vatMapper.toEntity(model.getVat()));
			article.setNetWeight(model.getNetWeight());
			article.setPcsCart(model.getPcsCart());
			article.setUm(model.getUm());

			// ---------------------- SET-BARCODE ----------------------
			Set<Barcode> barcodes = Optional.ofNullable(model.getBarcodes()).orElseGet(HashSet::new)
					.stream().map(barcodeDto -> barcodeMapper.toEntity(barcodeDto))
					.collect(Collectors.toSet());
			barcodes.forEach(barcode -> barcode.setArticle(article));
			article.setBarcodes(barcodes);
			// ---------------------------------------------------------
		}
		article = Optional.ofNullable(article).orElseGet(() -> new Article());
		return article;
	}

}
