package com.alphashop.articles_web_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alphashop.articles_web_service.dtos.VatDto;
import com.alphashop.articles_web_service.entities.Vat;
import com.alphashop.articles_web_service.exceptions.NotFoundException;
import com.alphashop.articles_web_service.mappers.VatMapper;
import com.alphashop.articles_web_service.repositories.VatRepository;

@Service
@Transactional(readOnly = true)
public class VatService {

	private static final Logger logger = LoggerFactory.getLogger(VatService.class);
	private final VatRepository vatRepository;
	private final VatMapper vatMapper;

	public VatService(VatRepository vatRepository, 
						   VatMapper vatMapper) {
		this.vatRepository = vatRepository;
		this.vatMapper = vatMapper;
	}

	public List<VatDto> getAll() throws NotFoundException {
		List<Vat> vatList = vatRepository.findAll();
		
		if (vatList.isEmpty()) {
			String errMessage = "No vat was found";
			
			logger.warn(errMessage);
			
			throw new NotFoundException(errMessage);
		}
		
		List<VatDto> vatListDto = vatList.stream().map(vat -> vatMapper.toModel(vat))
				.collect(Collectors.toList());
		
		return vatListDto;
	}
}
