package com.xantrix.webapp;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.dtos.BarcodeDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;

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
				map().setTipo(source.getIdTipoArt());
			}
		});
		
		modelMapper.addConverter(articoliConverter);
//		modelMapper.addMappings(toArticoliMapping); 
		
		
		return modelMapper;
	}

//	PropertyMap<ArticoliDto, Articoli> toArticoliMapping = new PropertyMap<ArticoliDto, Articoli>() {
//		
//		@Override
//		protected void configure() {
//			Set<Barcode> barcodeSet = new HashSet<Barcode>();
//			
//			for (BarcodeDto barcodeDto : source.getBarcode()) {
//				Barcode barcode = new Barcode();
//				barcode.setBarcode(barcodeDto.getBarcode());
//				barcode.setIdTipoArt(barcodeDto.getTipo());
//				
//				barcodeSet.add(barcode);
//			}
//			map().setBarcode(barcodeSet);
//		}
//	};
	
	PropertyMap<Articoli, ArticoliDto> toArticoliDtoMapping = new PropertyMap<Articoli, ArticoliDto>() {
		protected void configure() {
			map().setDataCreazione(source.getDataCreaz());
		}
	};

	Converter<String, String> articoliConverter = new Converter<String, String>() {
		@Override
		public String convert(MappingContext<String, String> context) {
			return context.getSource() == null ? "" : context.getSource().trim();
		}
	};

}
