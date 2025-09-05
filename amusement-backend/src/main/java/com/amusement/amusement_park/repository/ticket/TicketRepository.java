package com.amusement.amusement_park.repository.ticket;

import com.amusement.amusement_park.entity.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket findByTicketCode(String ticketCode);

    Ticket findByInvoiceId(String invoiceId);
    
    List<Ticket> findByUserId(Long userId);
}
