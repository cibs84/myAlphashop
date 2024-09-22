package com.alphashop.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.alphashop.models.User;

public interface UserRepository extends MongoRepository<User, String> {
	
	public User findByUserId(String userId);
}
