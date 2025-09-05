package com.amusement.amusement_park.entity.foodmerchorder;

import java.math.BigDecimal;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class MerchandiseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Merchandise name must not be blank")
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1", inclusive = true, message = "Price must be greater than 0")
    private BigDecimal price;


    // ✅ No-arg constructor (required by JPA)
    public MerchandiseItem() {
    }

    // ✅ All-arg constructor (used in tests or elsewhere)
    public MerchandiseItem(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
