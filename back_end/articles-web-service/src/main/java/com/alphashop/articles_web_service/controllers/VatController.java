package com.alphashop.articles_web_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alphashop.articles_web_service.dtos.VatResponseDto;
import com.alphashop.articles_web_service.exceptions.NotFoundException;
import com.alphashop.articles_web_service.mappers.VatMapper;
import com.alphashop.articles_web_service.services.VatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/vat")
public class VatController {

	@Autowired
	VatService vatService;

	@Autowired
	VatMapper vatMapper;
	

	@GetMapping
	public ResponseEntity<List<VatResponseDto>> listAll() throws NotFoundException {
		
		log.info("[VatController] listAll() | Get Vat List");
		
		List<VatResponseDto> vatList = vatService.getAll();
		
		return new ResponseEntity<List<VatResponseDto>>(vatList, HttpStatus.OK);
	}
}
