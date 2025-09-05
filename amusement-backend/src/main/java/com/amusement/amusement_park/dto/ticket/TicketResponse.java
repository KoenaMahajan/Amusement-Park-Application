package com.amusement.amusement_park.dto.ticket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for sending ticket information in API responses.
 */
public class TicketResponse {

    // Unique identifier for the ticket
    private Long ticketId;

    // ID of the ticket type
    private Integer ticketTypeId;

    // Name of the ticket type (e.g., VIP Day Pass)
    private String ticketTypeName;

    // Description of the ticket type
    private String ticketTypeDescription;

    // Price of the ticket type
    private BigDecimal ticketTypePrice;

    // Validity days of the ticket type
    private Integer ticketTypeValidityDays;

    // Whether this is a VIP ticket type
    private Boolean ticketTypeIsVip;

    // Unique ticket code for identification
    private String ticketCode;

    // Date and time when the ticket was purchased
    private LocalDateTime purchaseDate;

    // Date from which the ticket is valid
    private LocalDate validFrom;

    // Date until which the ticket is valid
    private LocalDate validTo;

    // Total amount paid for the ticket
    private BigDecimal totalAmount;

    // Whether this is a VIP ticket
    private Boolean isVip;

    // Current payment status of the ticket
    private String paymentStatus;

    // Unique invoice identifier
    private String invoiceId;

    // Default constructor
    public TicketResponse() {
    }

    // Constructor with all fields
    public TicketResponse(Long ticketId, Integer ticketTypeId, String ticketTypeName, String ticketTypeDescription,
            BigDecimal ticketTypePrice, Integer ticketTypeValidityDays, Boolean ticketTypeIsVip,
            String ticketCode, LocalDateTime purchaseDate, LocalDate validFrom,
            LocalDate validTo, BigDecimal totalAmount, Boolean isVip,
            String paymentStatus, String invoiceId) {
        this.ticketId = ticketId;
        this.ticketTypeId = ticketTypeId;
        this.ticketTypeName = ticketTypeName;
        this.ticketTypeDescription = ticketTypeDescription;
        this.ticketTypePrice = ticketTypePrice;
        this.ticketTypeValidityDays = ticketTypeValidityDays;
        this.ticketTypeIsVip = ticketTypeIsVip;
        this.ticketCode = ticketCode;
        this.purchaseDate = purchaseDate;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.totalAmount = totalAmount;
        this.isVip = isVip;
        this.paymentStatus = paymentStatus;
        this.invoiceId = invoiceId;
    }

    // Getters and setters for all fields
    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(Integer ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public void setTicketTypeName(String ticketTypeName) {
        this.ticketTypeName = ticketTypeName;
    }

    public String getTicketTypeDescription() {
        return ticketTypeDescription;
    }

    public void setTicketTypeDescription(String ticketTypeDescription) {
        this.ticketTypeDescription = ticketTypeDescription;
    }

    public BigDecimal getTicketTypePrice() {
        return ticketTypePrice;
    }

    public void setTicketTypePrice(BigDecimal ticketTypePrice) {
        this.ticketTypePrice = ticketTypePrice;
    }

    public Integer getTicketTypeValidityDays() {
        return ticketTypeValidityDays;
    }

    public void setTicketTypeValidityDays(Integer ticketTypeValidityDays) {
        this.ticketTypeValidityDays = ticketTypeValidityDays;
    }

    public Boolean getTicketTypeIsVip() {
        return ticketTypeIsVip;
    }

    public void setTicketTypeIsVip(Boolean ticketTypeIsVip) {
        this.ticketTypeIsVip = ticketTypeIsVip;
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

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}