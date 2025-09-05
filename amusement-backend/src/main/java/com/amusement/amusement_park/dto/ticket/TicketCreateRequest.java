package com.amusement.amusement_park.dto.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;

public class TicketCreateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Ticket type information is required")
    private TicketTypeRequest ticketType;

    private LocalDate validFrom;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive and greater than zero")
    private BigDecimal totalAmount;

    @NotBlank(message = "Payment status is required and cannot be blank")
    @NotNull(message = "Payment status is required")
    private String paymentStatus;

    @NotBlank(message = "Payment mode is required and cannot be blank")
    @NotNull(message = "Payment mode is required")
    private String paymentMode;

    // Nested class for ticket type information
    public static class TicketTypeRequest {
        @NotNull(message = "Ticket type ID is required")
        private Integer ticketTypeId;

        public Integer getTicketTypeId() {
            return ticketTypeId;
        }

        public void setTicketTypeId(Integer ticketTypeId) {
            this.ticketTypeId = ticketTypeId;
        }
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TicketTypeRequest getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketTypeRequest ticketType) {
        this.ticketType = ticketType;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}