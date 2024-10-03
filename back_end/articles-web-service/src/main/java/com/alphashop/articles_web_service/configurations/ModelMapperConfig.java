package com.alphashop.articles_web_service.configurations;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alphashop.articles_web_service.dtos.ArticleDto;
import com.alphashop.articles_web_service.dtos.BarcodeDto;
import com.alphashop.articles_web_service.entities.Article;
import com.alphashop.articles_web_service.entities.Barcode;

@Configuration
public class ModelMapperConfig {

	@Bean
	ModelMapper modelMapper() {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		modelMapper.addMappings(toArticleDtoMapping);

		modelMapper.addMappings(new PropertyMap<Barcode, BarcodeDto>() {
			@Override
			protected void configure() {
				map().setType(source.getIdTypeArt());
			}
		});

		modelMapper.addConverter(articleConverter);

		return modelMapper;
	}

	PropertyMap<Article, ArticleDto> toArticleDtoMapping = new PropertyMap<Article, ArticleDto>() {
		protected void configure() {
			map().setCreationDate(source.getCreationDate());
		}
	};

	Converter<String, String> articleConverter = new Converter<String, String>() {
		@Override
		public String convert(MappingContext<String, String> context) {
			return context.getSource() == null ? "" : context.getSource().trim();
		}
	};

}
