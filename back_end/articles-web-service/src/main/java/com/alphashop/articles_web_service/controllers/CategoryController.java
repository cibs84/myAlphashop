package com.alphashop.articles_web_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alphashop.articles_web_service.dtos.CategoryResponseDto;
import com.alphashop.articles_web_service.exceptions.NotFoundException;
import com.alphashop.articles_web_service.mappers.CategoryMapper;
import com.alphashop.articles_web_service.services.CategoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	CategoryMapper categoryMapper;
	

	@GetMapping
	public ResponseEntity<List<CategoryResponseDto>> listAll() throws NotFoundException {
		
		log.info("[CategoryController] listAll() | Get All Categories");

		List<CategoryResponseDto> categories = categoryService.getAll();
		
		return new ResponseEntity<List<CategoryResponseDto>>(categories, HttpStatus.OK);
	}
}
