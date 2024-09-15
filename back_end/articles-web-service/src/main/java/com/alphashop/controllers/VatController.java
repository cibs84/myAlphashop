package com.alphashop.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alphashop.dtos.VatDto;
import com.alphashop.exceptions.NotFoundException;
import com.alphashop.mappers.VatMapper;
import com.alphashop.services.VatService;

@RestController
@RequestMapping("/api/vat")
@CrossOrigin(origins = "http://localhost:4200/")
public class VatController {

	private static final Logger logger = LoggerFactory.getLogger(VatController.class);

	@Autowired
	VatService vatService;

	@Autowired
	VatMapper vatMapper;
	

	@GetMapping("/find/all")
	public ResponseEntity<List<VatDto>> listAll() throws NotFoundException {
		
		logger.info("******** Get all categories ********");

		List<VatDto> vatList = vatService.getAll();
		
		return new ResponseEntity<List<VatDto>>(vatList, HttpStatus.OK);
	}
}
