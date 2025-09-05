package com.amusement.amusement_park.service.ticket;

import com.amusement.amusement_park.dto.ticket.TicketCreateRequest;
import com.amusement.amusement_park.dto.ticket.TicketResponse;
import com.amusement.amusement_park.exception.NotFoundException;
import com.amusement.amusement_park.entity.ticket.Ticket;
import com.amusement.amusement_park.entity.ticket.TicketType;
import com.amusement.amusement_park.repository.ticket.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TicketService {

    @Autowired
    private TicketRepository repo;

    @Autowired
    private TicketTypeService ticketTypeService;

    private final AtomicInteger ticketCounter = new AtomicInteger(1);
    private final AtomicInteger invoiceCounter = new AtomicInteger(1);

    public List<Ticket> getAll() {
        return repo.findAll();
    }

    public List<Ticket> getByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    public Ticket getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Ticket not found with id: " + id);
        }
        repo.deleteById(id);
    }

    /**
     * Create a ticket with simplified request and auto-generated fields
     */
    public TicketResponse createTicket(TicketCreateRequest request) {
        // Fetch the ticket type
        TicketType ticketType = ticketTypeService.getById(request.getTicketType().getTicketTypeId());
        if (ticketType == null) {
            throw new NotFoundException("Ticket type not found with id: " + request.getTicketType().getTicketTypeId());
        }

        // Create new ticket
        Ticket ticket = new Ticket();
        ticket.setUserId(request.getUserId());
        ticket.setTicketType(ticketType);
        ticket.setTotalAmount(request.getTotalAmount());
        ticket.setPaymentStatus(Ticket.PaymentStatus.valueOf(request.getPaymentStatus()));
        ticket.setPaymentMode(Ticket.PaymentMode.valueOf(request.getPaymentMode()));
        ticket.setPurchaseDate(LocalDateTime.now());

        // Set validFrom (default to today if not provided)
        LocalDate validFrom = request.getValidFrom() != null ? request.getValidFrom() : LocalDate.now();
        ticket.setValidFrom(validFrom);

        // Calculate validTo based on ticket type validity days
        LocalDate validTo = validFrom.plusDays(ticketType.getValidityDays());
        ticket.setValidTo(validTo);

        // Auto-generate ticket code
        ticket.setTicketCode(generateTicketCode());

        // Auto-generate invoice ID
        ticket.setInvoiceId(generateInvoiceId());

        // Save the ticket
        Ticket savedTicket = repo.save(ticket);

        // Create and return the response DTO
        return new TicketResponse(
                savedTicket.getTicketId(),
                savedTicket.getTicketType().getTicketTypeId(),
                savedTicket.getTicketType().getName(),
                savedTicket.getTicketType().getDescription(),
                savedTicket.getTicketType().getPrice(),
                savedTicket.getTicketType().getValidityDays(),
                savedTicket.getTicketType().getIsVip(),
                savedTicket.getTicketCode(),
                savedTicket.getPurchaseDate(),
                savedTicket.getValidFrom(),
                savedTicket.getValidTo(),
                savedTicket.getTotalAmount(),
                savedTicket.getTicketType().getIsVip(),
                savedTicket.getPaymentStatus().toString(),
                savedTicket.getInvoiceId());
    }

    /**
     * Update an existing ticket with simplified request
     */
    public TicketResponse updateTicket(Long ticketId, TicketCreateRequest request) {
        // Check if ticket exists
        Ticket existingTicket = repo.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + ticketId));

        // Fetch the ticket type
        TicketType ticketType = ticketTypeService.getById(request.getTicketType().getTicketTypeId());
        if (ticketType == null) {
            throw new NotFoundException("Ticket type not found with id: " + request.getTicketType().getTicketTypeId());
        }

        // Update ticket fields
        existingTicket.setUserId(request.getUserId());
        existingTicket.setTicketType(ticketType);
        existingTicket.setTotalAmount(request.getTotalAmount());
        existingTicket.setPaymentStatus(Ticket.PaymentStatus.valueOf(request.getPaymentStatus()));
        existingTicket.setPaymentMode(Ticket.PaymentMode.valueOf(request.getPaymentMode()));

        // Set validFrom (default to today if not provided)
        LocalDate validFrom = request.getValidFrom() != null ? request.getValidFrom() : LocalDate.now();
        existingTicket.setValidFrom(validFrom);

        // Calculate validTo based on ticket type validity days
        LocalDate validTo = validFrom.plusDays(ticketType.getValidityDays());
        existingTicket.setValidTo(validTo);

        // Save the updated ticket
        Ticket savedTicket = repo.save(existingTicket);

        // Create and return the response DTO
        return new TicketResponse(
                savedTicket.getTicketId(),
                savedTicket.getTicketType().getTicketTypeId(),
                savedTicket.getTicketType().getName(),
                savedTicket.getTicketType().getDescription(),
                savedTicket.getTicketType().getPrice(),
                savedTicket.getTicketType().getValidityDays(),
                savedTicket.getTicketType().getIsVip(),
                savedTicket.getTicketCode(),
                savedTicket.getPurchaseDate(),
                savedTicket.getValidFrom(),
                savedTicket.getValidTo(),
                savedTicket.getTotalAmount(),
                savedTicket.getTicketType().getIsVip(),
                savedTicket.getPaymentStatus().toString(),
                savedTicket.getInvoiceId());
    }

    /**
     * Generate a unique ticket code in format TKT-YYYY-NNN
     */
    private String generateTicketCode() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        int counter = ticketCounter.getAndIncrement();
        String ticketCode = String.format("TKT-%s-%03d", year, counter);

        // Ensureing uniqueness by checking if code already exists
        while (repo.findByTicketCode(ticketCode) != null) {
            counter = ticketCounter.getAndIncrement();
            ticketCode = String.format("TKT-%s-%03d", year, counter);
        }

        return ticketCode;
    }

    /**
     * Generate a unique invoice ID in format INV-YYYY-NNN
     */
    private String generateInvoiceId() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        int counter = invoiceCounter.getAndIncrement();
        String invoiceId = String.format("INV-%s-%03d", year, counter);

        // Ensure uniqueness by checking if invoice ID already exists
        while (repo.findByInvoiceId(invoiceId) != null) {
            counter = invoiceCounter.getAndIncrement();
            invoiceId = String.format("INV-%s-%03d", year, counter);
        }

        return invoiceId;
    }
}
