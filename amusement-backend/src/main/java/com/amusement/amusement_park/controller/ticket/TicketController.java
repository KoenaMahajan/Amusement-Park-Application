package com.amusement.amusement_park.controller.ticket;

import com.amusement.amusement_park.dto.ticket.TicketCreateRequest;
import com.amusement.amusement_park.dto.ticket.TicketResponse;
import com.amusement.amusement_park.exception.NotFoundException;
import com.amusement.amusement_park.entity.ticket.Ticket;
import com.amusement.amusement_park.service.ticket.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService service;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAll() {
        List<Ticket> tickets = service.getAll();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getByUserId(@PathVariable Long userId) {
        List<Ticket> tickets = service.getByUserId(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        try {
            Ticket ticket = service.getById(id);
            return ResponseEntity.ok(ticket);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketCreateRequest request) {
        try {
            TicketResponse createdTicket = service.createTicket(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable Long id,
            @Valid @RequestBody TicketCreateRequest request) {
        try {
            TicketResponse updatedTicket = service.updateTicket(id, request);
            return ResponseEntity.ok(updatedTicket);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
