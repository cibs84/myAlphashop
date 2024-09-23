package com.alphashop.articles_web_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alphashop.articles_web_service.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
