package com.amusement.amusement_park.repository.ticket;

import com.amusement.amusement_park.entity.ticket.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {

}
