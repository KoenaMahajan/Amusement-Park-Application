package com.amusement.amusement_park.service.ticket;

import com.amusement.amusement_park.exception.NotFoundException;
import com.amusement.amusement_park.entity.ticket.TicketType;
import com.amusement.amusement_park.repository.ticket.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketTypeService {

    @Autowired
    private TicketTypeRepository repo;

    public List<TicketType> getAll() {
        return repo.findAll();
    }

    public TicketType getById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("TicketType not found with id: " + id));
    }

    public TicketType save(TicketType ticketType) {
        return repo.save(ticketType);
    }

    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("TicketType not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
