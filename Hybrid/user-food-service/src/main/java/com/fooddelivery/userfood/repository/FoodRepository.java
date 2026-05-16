package com.fooddelivery.userfood.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fooddelivery.userfood.model.FoodDocument;

public interface FoodRepository extends MongoRepository<FoodDocument, String> {

    List<FoodDocument> findByAvailableTrue();
}

