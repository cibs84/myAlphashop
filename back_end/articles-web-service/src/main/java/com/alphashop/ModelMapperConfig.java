package com.alphashop;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alphashop.dtos.ArticleDto;
import com.alphashop.dtos.BarcodeDto;
import com.alphashop.entities.Article;
import com.alphashop.entities.Barcode;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapper() {
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		modelMapper.addMappings(toArticoliDtoMapping);

		modelMapper.addMappings(new PropertyMap<Barcode, BarcodeDto>() {
			@Override
			protected void configure() {
				map().setType(source.getIdTypeArt());
			}
		});
		
		modelMapper.addConverter(articoliConverter);
		
		return modelMapper;
	}
	
	PropertyMap<Article, ArticleDto> toArticoliDtoMapping = new PropertyMap<Article, ArticleDto>() {
		protected void configure() {
			map().setCreationDate(source.getCreationDate());
		}
	};

	Converter<String, String> articoliConverter = new Converter<String, String>() {
		@Override
		public String convert(MappingContext<String, String> context) {
			return context.getSource() == null ? "" : context.getSource().trim();
		}
	};

}
