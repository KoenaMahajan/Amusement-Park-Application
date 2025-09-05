package com.amusement.amusement_park.entity.foodmerchorder;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import com.amusement.amusement_park.entity.foodmerchorder.ItemType; 

import java.math.BigDecimal;

@Embeddable
public class OrderItem {

    @NotNull(message = "Item ID is required")
    @Positive(message = "Item ID must be a positive number")
    private Long itemId;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name is too long")
    private String itemName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Invalid unit price format")
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    // --- Getters and Setters ---

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public ItemType getItemType() {
    return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

}
