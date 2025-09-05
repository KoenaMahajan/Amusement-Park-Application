package com.amusement.amusement_park.controller.ticket;

import com.amusement.amusement_park.entity.ticket.TicketType;
import com.amusement.amusement_park.service.ticket.TicketTypeService;
import com.amusement.amusement_park.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket-types")
public class TicketTypeController {

    @Autowired
    private TicketTypeService service;

    @GetMapping
    public ResponseEntity<List<TicketType>> getAll() {
        List<TicketType> ticketTypes = service.getAll();
        return ResponseEntity.ok(ticketTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketType> getById(@PathVariable Integer id) {
        try {
            TicketType ticketType = service.getById(id);
            return ResponseEntity.ok(ticketType);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<TicketType> create(@Valid @RequestBody TicketType ticketType) {
        TicketType createdTicketType = service.save(ticketType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicketType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketType> update(@PathVariable Integer id, @Valid @RequestBody TicketType ticketType) {
        try {
            TicketType existingTicketType = service.getById(id);
            existingTicketType.setName(ticketType.getName());
            existingTicketType.setDescription(ticketType.getDescription());
            existingTicketType.setPrice(ticketType.getPrice());
            existingTicketType.setValidityDays(ticketType.getValidityDays());
            existingTicketType.setIsVip(ticketType.getIsVip());
            TicketType updatedTicketType = service.save(existingTicketType);
            return ResponseEntity.ok(updatedTicketType);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
