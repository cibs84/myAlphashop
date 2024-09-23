package com.alphashop.articles_web_service.controllers;

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

import com.alphashop.articles_web_service.dtos.CategoryDto;
import com.alphashop.articles_web_service.exceptions.NotFoundException;
import com.alphashop.articles_web_service.mappers.CategoryMapper;
import com.alphashop.articles_web_service.services.CategoryService;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200/")
public class CategoryController {

	private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

	@Autowired
	CategoryService categoryService;

	@Autowired
	CategoryMapper categoryMapper;
	

	@GetMapping("/find/all")
	public ResponseEntity<List<CategoryDto>> listAll() throws NotFoundException {
		
		logger.info("******** Get all categories ********");

		List<CategoryDto> article = categoryService.getAll();
		
		return new ResponseEntity<List<CategoryDto>>(article, HttpStatus.OK);
	}
}
