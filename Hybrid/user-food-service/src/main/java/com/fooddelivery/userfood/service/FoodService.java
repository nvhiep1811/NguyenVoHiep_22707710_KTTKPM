package com.fooddelivery.userfood.service;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fooddelivery.userfood.dto.FoodRequest;
import com.fooddelivery.userfood.dto.FoodResponse;
import com.fooddelivery.userfood.exception.ApiException;
import com.fooddelivery.userfood.model.FoodDocument;
import com.fooddelivery.userfood.repository.FoodRepository;

@Service
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public List<FoodResponse> getAvailableFoods() {
        return foodRepository.findByAvailableTrue().stream().map(this::toResponse).toList();
    }

    public FoodResponse getFood(String id) {
        FoodDocument food = findAvailableFood(id);
        return toResponse(food);
    }

    public FoodResponse createFood(FoodRequest request) {
        Instant now = Instant.now();
        FoodDocument food = new FoodDocument();
        applyRequest(food, request);
        food.setAvailable(request.available() == null || request.available());
        food.setCreatedAt(now);
        food.setUpdatedAt(now);
        return toResponse(foodRepository.save(food));
    }

    public FoodResponse updateFood(String id, FoodRequest request) {
        FoodDocument food = foodRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food not found"));
        applyRequest(food, request);
        if (request.available() != null) {
            food.setAvailable(request.available());
        }
        food.setUpdatedAt(Instant.now());
        return toResponse(foodRepository.save(food));
    }

    public void softDeleteFood(String id) {
        FoodDocument food = foodRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food not found"));
        food.setAvailable(false);
        food.setUpdatedAt(Instant.now());
        foodRepository.save(food);
    }

    private FoodDocument findAvailableFood(String id) {
        FoodDocument food = foodRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food not found"));
        if (!food.isAvailable()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Food not found");
        }
        return food;
    }

    private void applyRequest(FoodDocument food, FoodRequest request) {
        food.setName(request.name().trim());
        food.setDescription(request.description());
        food.setPrice(request.price());
        food.setImageUrl(request.imageUrl());
        food.setCategory(request.category());
        food.setRestaurantName(request.restaurantName());
    }

    private FoodResponse toResponse(FoodDocument food) {
        return new FoodResponse(
                food.getId(),
                food.getName(),
                food.getDescription(),
                food.getPrice(),
                food.getImageUrl(),
                food.getCategory(),
                food.getRestaurantName(),
                food.isAvailable(),
                food.getCreatedAt(),
                food.getUpdatedAt()
        );
    }
}

