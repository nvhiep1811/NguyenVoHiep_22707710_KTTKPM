package com.fooddelivery.userfood.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fooddelivery.userfood.model.UserDocument;

public interface UserRepository extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByEmail(String email);

    boolean existsByEmail(String email);
}

