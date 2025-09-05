package com.amusement.amusement_park.service.foodmerchorder;

import com.amusement.amusement_park.entity.foodmerchorder.FoodItem;
import java.util.List;

public interface FoodItemService {
    FoodItem addFoodItem(FoodItem item);
    void deleteFoodItem(Long id);
    List<FoodItem> getAllItems();
}
