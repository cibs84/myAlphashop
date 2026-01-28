package com.alphashop.articles_web_service.mappers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alphashop.articles_web_service.dtos.ArticleCreateRequestDto;
import com.alphashop.articles_web_service.dtos.ArticleResponseDto;
import com.alphashop.articles_web_service.dtos.ArticleUpdateRequestDto;
import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.entities.Barcode;
import com.alphashop.articles_web_service.repositories.ArticleRepository;

@Component
public class ArticleMapper {

	private final BarcodeMapper barcodeMapper;
	private final CategoryMapper categoryMapper;
	private final IngredientsMapper ingredientsMapper;
	private final VatMapper vatMapper;

	public ArticleMapper(BarcodeMapper barcodeMapper, CategoryMapper categoryMapper,
			IngredientsMapper ingredientsMapper, VatMapper vatMapper, ArticleRepository articleRepository) {
		this.barcodeMapper = barcodeMapper;
		this.categoryMapper = categoryMapper;
		this.ingredientsMapper = ingredientsMapper;
		this.vatMapper = vatMapper;
	}

	// -------------------------------------------------------------
    // ENTITY → RESPONSE DTO
    // -------------------------------------------------------------
	public ArticleResponseDto toModel(Article entity) {

		ArticleResponseDto articleResponseDto = new ArticleResponseDto();
		
		articleResponseDto.setBarcodes(Optional.ofNullable(entity.getBarcodes()).orElseGet(HashSet::new)
					.stream()
					.map(barcodeMapper::toModel)
					.collect(Collectors.toSet()));
		articleResponseDto.setCodart(entity.getCodart());
		articleResponseDto.setCodStat(entity.getCodStat());
		articleResponseDto.setCreationDate(entity.getCreationDate());
		articleResponseDto.setDescription(entity.getDescription());
		articleResponseDto.setCategory(categoryMapper.toModel(entity.getCategory()));
		articleResponseDto.setIdArtStatus(entity.getIdArtStatus());
		articleResponseDto.setIngredients(ingredientsMapper.toModel(entity.getIngredients()));
		articleResponseDto.setVat(vatMapper.toModel(entity.getVat()));
		articleResponseDto.setNetWeight(entity.getNetWeight());
		articleResponseDto.setPcsCart(entity.getPcsCart());
		articleResponseDto.setUm(entity.getUm());

		return articleResponseDto;
	}

	// -------------------------------------------------------------
    // CREATE → ENTITY
    // -------------------------------------------------------------
	public Article createToEntity(ArticleCreateRequestDto model) {
		
		Article article = new Article();

		article.setCodart(model.getCodart());
		article.setCodStat(model.getCodStat());
		article.setDescription(model.getDescription());
		article.setCategory(categoryMapper.toEntity(model.getCategory()));
		article.setIdArtStatus(model.getIdArtStatus());
		if (model.getIngredients() != null) {
			article.setIngredients(ingredientsMapper.toEntity(model.getIngredients()));
		}
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
		return article;
	}
	
	// -------------------------------------------------------------
    // UPDATE → ENTITY
    // -------------------------------------------------------------
	public void updateToEntity(ArticleUpdateRequestDto model, Article entity) {
		if (model.getDescription() != null) {
			entity.setDescription(model.getDescription());
		}

		if (model.getUm() != null) {
			entity.setUm(model.getUm());
		}
		if (model.getCodStat() != null) {
			entity.setCodStat(model.getCodStat());	
		}
		if (model.getPcsCart() != null) {
			entity.setPcsCart(model.getPcsCart());	
		}
		if (model.getNetWeight() != null) {
			entity.setNetWeight(model.getNetWeight());	
		}
		if (model.getIdArtStatus() != null) {
			entity.setIdArtStatus(model.getIdArtStatus());	
		}
		if (model.getBarcodes() != null) {
			// 1. Pulisci la collezione esistente prima di aggiungere i nuovi elementi.
	        //    Questa azione impedisce un errore da parte di Hibernate.
	        entity.getBarcodes().clear();

	        // 2. Mappa i DTO in Entity usando uno stream e aggiungili con forEach
	        model.getBarcodes().stream()
	            .map(b -> barcodeMapper.toEntity(b)) // Mappa il DTO a Entity
	            .forEach(barcodeEntity -> {
	                // Aggiungi l'entità mappata alla collezione esistente
	                entity.getBarcodes().add(barcodeEntity);
	                // Imposta la relazione inversa (essenziale per consistenza bidirezionale)
	                barcodeEntity.setArticle(entity);
	            });
		}
		if (model.getIngredients() != null) {
			entity.setIngredients(ingredientsMapper.toEntity(model.getIngredients()));
		} else {
			if (entity.getIngredients() != null) {
				entity.setIngredients(null);
			}
		}
		if (model.getCategory() != null) {
			entity.setCategory(categoryMapper.toEntity(model.getCategory()));	
		}
		if (model.getVat() != null) {
			entity.setVat(vatMapper.toEntity(model.getVat()));
		}
		
	}
}
