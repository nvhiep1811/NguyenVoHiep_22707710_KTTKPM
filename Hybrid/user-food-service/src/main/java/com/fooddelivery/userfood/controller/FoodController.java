package com.fooddelivery.userfood.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.userfood.dto.FoodRequest;
import com.fooddelivery.userfood.dto.FoodResponse;
import com.fooddelivery.userfood.service.FoodService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public List<FoodResponse> getFoods() {
        return foodService.getAvailableFoods();
    }

    @GetMapping("/{id}")
    public FoodResponse getFood(@PathVariable String id) {
        return foodService.getFood(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodResponse createFood(@Valid @RequestBody FoodRequest request) {
        return foodService.createFood(request);
    }

    @PutMapping("/{id}")
    public FoodResponse updateFood(@PathVariable String id, @Valid @RequestBody FoodRequest request) {
        return foodService.updateFood(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(@PathVariable String id) {
        foodService.softDeleteFood(id);
    }
}

