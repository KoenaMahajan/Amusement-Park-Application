package com.amusement.amusement_park.entity.ticket;

import jakarta.persistence.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "ticket_types")
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketTypeId;

    @NotBlank(message = "Name is required and cannot be blank")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive and greater than zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Validity days is required")
    @Positive(message = "Validity days must be positive")
    @Column(name = "validity_days", columnDefinition = "INT DEFAULT 1")
    private Integer validityDays = 1;

    @NotNull(message = "VIP flag is required")
    @Column(name = "is_vip", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVip = false;

    // === Getters and Setters ===

    public Integer getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(Integer ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }
}
