package com.alphashop.articles_web_service.converters;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdArtStatusConverter implements AttributeConverter<Integer, String> {

	@Override
	public String convertToDatabaseColumn(Integer attribute) {
		return attribute != null ? String.valueOf(attribute) : null;
	}

	@Override
	public Integer convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		
		String trimmedData = dbData.trim();
		
		try {
			return Integer.valueOf(trimmedData);
		} catch (Exception e) {
			log.error("Found non-numeric data '{}' in idArtStatus DB column. Returning null.", trimmedData, e);
		}
		
		return null;
	}

}
