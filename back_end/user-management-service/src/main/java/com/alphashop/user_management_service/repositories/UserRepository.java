package com.alphashop.user_management_service.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.alphashop.user_management_service.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
	Page<User> findAllByOrderByUserIdAsc(Pageable pageRequest);
	
	public Optional<User> findByUserId(String userId);
}
