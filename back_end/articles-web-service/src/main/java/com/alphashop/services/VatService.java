package com.alphashop.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alphashop.dtos.VatDto;
import com.alphashop.entities.Vat;
import com.alphashop.exceptions.NotFoundException;
import com.alphashop.mappers.VatMapper;
import com.alphashop.repositories.VatRepository;

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
