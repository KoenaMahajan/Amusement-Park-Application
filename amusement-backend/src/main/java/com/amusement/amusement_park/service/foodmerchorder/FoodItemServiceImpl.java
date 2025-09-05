package com.amusement.amusement_park.service.foodmerchorder;

import com.amusement.amusement_park.entity.foodmerchorder.FoodItem;
import com.amusement.amusement_park.exception.DuplicateFoodItemException;
import com.amusement.amusement_park.exception.FoodItemNotFoundException;
import com.amusement.amusement_park.repository.foodmerchorder.FoodItemRepository;
import com.amusement.amusement_park.service.foodmerchorder.FoodItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Override
    public FoodItem addFoodItem(FoodItem item) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Food name cannot be blank or whitespace.");
        }

        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Food price must be greater than zero.");
        }

        if (foodItemRepository.existsByNameIgnoreCase(item.getName())) {
            throw new DuplicateFoodItemException(item.getName());
        }

        return foodItemRepository.save(item);
    }

    @Override
    public void deleteFoodItem(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new FoodItemNotFoundException(id);
        }
        foodItemRepository.deleteById(id);
    }

    @Override
    public List<FoodItem> getAllItems() {
        List<FoodItem> li = foodItemRepository.findAll();
        System.out.println(li);

        return foodItemRepository.findAll();

    }


}
