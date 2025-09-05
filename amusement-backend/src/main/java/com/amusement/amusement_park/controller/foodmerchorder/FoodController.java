package com.amusement.amusement_park.controller.foodmerchorder;

import com.amusement.amusement_park.entity.foodmerchorder.FoodItem;
import com.amusement.amusement_park.service.foodmerchorder.FoodItemService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodItemService foodItemService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FoodItem> addItem(@Valid @RequestBody FoodItem item) {
        FoodItem created = foodItemService.addFoodItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.ok("Food item deleted successfully.");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<FoodItem> getAllItems() {
        return foodItemService.getAllItems();
    }
}
