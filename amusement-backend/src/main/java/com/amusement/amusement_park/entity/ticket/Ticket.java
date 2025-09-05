package com.amusement.amusement_park.entity.ticket;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Entity representing a purchased ticket.
 */
@Entity
@Table(name = "tickets")
public class Ticket {

    // Primary key, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    // ID of the user who purchased the ticket
    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private Long userId;

    // Many-to-one relationship to TicketType
    @NotNull(message = "Ticket type is required")
    @ManyToOne
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    // Unique ticket code
    @NotBlank(message = "Ticket code is required and cannot be blank")
    @Column(unique = true, nullable = false, length = 100)
    private String ticketCode;

    // Purchase date and time (defaults to current timestamp)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime purchaseDate = LocalDateTime.now();

    // Date from which the ticket is valid
    @NotNull(message = "Valid from date is required")
    @Column(nullable = false)
    private LocalDate validFrom;

    // Date until which the ticket is valid
    @NotNull(message = "Valid to date is required")
    @Column(nullable = false)
    private LocalDate validTo;

    // Total amount paid for the ticket
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive and greater than zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Current payment status of the ticket
    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING','PAID','FAILED') DEFAULT 'PENDING'")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // Method used for payment
    @NotNull(message = "Payment mode is required")
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    // Unique invoice identifier
    @NotBlank(message = "Invoice ID is required and cannot be blank")
    @Column(unique = true, length = 50)
    private String invoiceId;

    // === Enum Types ===
    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED
    }

    public enum PaymentMode {
        UPI,
        CARD,
        WALLET,
        CASH
    }

    // === Getters and Setters ===

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}
