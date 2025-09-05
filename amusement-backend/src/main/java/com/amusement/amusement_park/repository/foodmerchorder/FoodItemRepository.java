package com.amusement.amusement_park.repository.foodmerchorder;

import com.amusement.amusement_park.entity.foodmerchorder.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    boolean existsByNameIgnoreCase(String name);
}
