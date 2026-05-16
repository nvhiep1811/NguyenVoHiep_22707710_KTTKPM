package com.fooddelivery.userfood.config;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fooddelivery.userfood.model.FoodDocument;
import com.fooddelivery.userfood.repository.FoodRepository;

@Configuration
public class FoodDataSeeder {

    @Bean
    CommandLineRunner seedFoods(FoodRepository foodRepository) {
        return args -> {
            if (foodRepository.count() > 0) {
                return;
            }

            foodRepository.saveAll(List.of(
                    food("Cơm gà xối mỡ", "Cơm gà giòn ngon kèm rau dưa", 45000, "Món chính", "Quán Ăn Ngon",
                            "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?auto=format&fit=crop&w=900&q=80"),
                    food("Bún bò Huế", "Tô bún bò cay thơm đúng vị Huế", 55000, "Món nước", "Huế Bistro",
                            "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&w=900&q=80"),
                    food("Trà sữa trân châu", "Trà sữa trân châu đường đen", 35000, "Đồ uống", "Milk Tea House",
                            "https://images.unsplash.com/photo-1558857563-b371033873b8?auto=format&fit=crop&w=900&q=80"),
                    food("Bánh mì thịt", "Bánh mì thịt nguội, pate và đồ chua", 25000, "Ăn nhanh", "Bánh Mì 24h",
                            "https://images.unsplash.com/photo-1600688640154-9619e002df30?auto=format&fit=crop&w=900&q=80"),
                    food("Phở bò", "Phở bò tái chín với nước dùng đậm đà", 60000, "Món nước", "Phở Phố",
                            "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?auto=format&fit=crop&w=900&q=80")
            ));
        };
    }

    private FoodDocument food(String name, String description, int price, String category, String restaurantName, String imageUrl) {
        Instant now = Instant.now();
        FoodDocument food = new FoodDocument();
        food.setName(name);
        food.setDescription(description);
        food.setPrice(BigDecimal.valueOf(price));
        food.setCategory(category);
        food.setRestaurantName(restaurantName);
        food.setImageUrl(imageUrl);
        food.setAvailable(true);
        food.setCreatedAt(now);
        food.setUpdatedAt(now);
        return food;
    }
}
