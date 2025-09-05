package com.amusement.amusement_park.entity.foodmerchorder;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    @Column(name = "user_id", nullable = false)
    private Long userId;      

    @NotBlank(message = "Pickup location must not be blank")
    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;


    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderTime;


    @NotEmpty(message = "Order must contain at least one item")
    @ElementCollection
    @Valid // ✅ Enables validation inside each OrderItem
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> itemList;

    // ✅ No-arg constructor (needed for JPA and tests)
    public Order() {
        }

    // ✅ All-args constructor (handy for test setup)
    public Order(Long id, Long userId, String status, String pickupLocation,
             BigDecimal totalAmount, LocalDateTime orderTime, List<OrderItem> itemList) {
                this.id = id;
                this.userId = userId;
                this.status = status;
                this.pickupLocation = pickupLocation;
                this.totalAmount = totalAmount;
                this.orderTime = orderTime;
                this.itemList = itemList;
    }


    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public List<OrderItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<OrderItem> itemList) {
        this.itemList = itemList;
    }
}
