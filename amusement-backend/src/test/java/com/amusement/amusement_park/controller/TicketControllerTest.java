package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.ticket.TicketController;
import com.amusement.amusement_park.dto.ticket.TicketCreateRequest;
import com.amusement.amusement_park.dto.ticket.TicketResponse;
import com.amusement.amusement_park.entity.ticket.Ticket;
import com.amusement.amusement_park.entity.ticket.TicketType;
import com.amusement.amusement_park.exception.NotFoundException;
import com.amusement.amusement_park.service.ticket.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private TicketType ticketType;
    private Ticket ticket;
    private TicketCreateRequest ticketCreateRequest;
    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();

        // Setup test data
        ticketType = new TicketType();
        ticketType.setTicketTypeId(1);
        ticketType.setName("VIP Pass");
        ticketType.setDescription("Premium access to all attractions");
        ticketType.setPrice(new BigDecimal("150.00"));
        ticketType.setValidityDays(1);
        ticketType.setIsVip(true);

        ticket = new Ticket();
        ticket.setTicketId(1L);
        ticket.setUserId(1L);
        ticket.setTicketType(ticketType);
        ticket.setTicketCode("TKT-2024-001");
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setValidFrom(LocalDate.now());
        ticket.setValidTo(LocalDate.now().plusDays(1));
        ticket.setTotalAmount(new BigDecimal("150.00"));
        ticket.setPaymentStatus(Ticket.PaymentStatus.PAID);
        ticket.setPaymentMode(Ticket.PaymentMode.CARD);
        ticket.setInvoiceId("INV-2024-001");

        ticketCreateRequest = new TicketCreateRequest();
        ticketCreateRequest.setUserId(1L);
        TicketCreateRequest.TicketTypeRequest typeRequest = new TicketCreateRequest.TicketTypeRequest();
        typeRequest.setTicketTypeId(1);
        ticketCreateRequest.setTicketType(typeRequest);
        ticketCreateRequest.setValidFrom(LocalDate.now());
        ticketCreateRequest.setTotalAmount(new BigDecimal("150.00"));
        ticketCreateRequest.setPaymentStatus("PAID");
        ticketCreateRequest.setPaymentMode("CARD");

        ticketResponse = new TicketResponse(
                1L, 1, "VIP Pass", "Premium access to all attractions",
                new BigDecimal("150.00"), 1, true, "TKT-2024-001",
                LocalDateTime.now(), LocalDate.now(), LocalDate.now().plusDays(1),
                new BigDecimal("150.00"), true, "PAID", "INV-2024-001");
    }

    @Test
    void getAllTickets_shouldReturnListOfTickets() throws Exception {
        // Given
        List<Ticket> tickets = Arrays.asList(ticket);
        when(ticketService.getAll()).thenReturn(tickets);

        // When & Then
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ticketId").value(1))
                .andExpect(jsonPath("$[0].ticketCode").value("TKT-2024-001"))
                .andExpect(jsonPath("$[0].totalAmount").value(150.00));

        verify(ticketService, times(1)).getAll();
    }

    @Test
    void getTicketById_whenTicketExists_shouldReturnTicket() throws Exception {
        // Given
        when(ticketService.getById(1L)).thenReturn(ticket);

        // When & Then
        mockMvc.perform(get("/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.ticketCode").value("TKT-2024-001"))
                .andExpect(jsonPath("$.totalAmount").value(150.00));

        verify(ticketService, times(1)).getById(1L);
    }

    @Test
    void getTicketById_whenTicketNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(ticketService.getById(999L)).thenThrow(new NotFoundException("Ticket not found"));

        // When & Then
        mockMvc.perform(get("/tickets/999"))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).getById(999L);
    }

    @Test
    void createTicket_whenValidRequest_shouldReturnCreatedTicket() throws Exception {
        // Given
        when(ticketService.createTicket(any(TicketCreateRequest.class))).thenReturn(ticketResponse);

        // When & Then
        mockMvc.perform(post("/tickets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.ticketCode").value("TKT-2024-001"))
                .andExpect(jsonPath("$.totalAmount").value(150.00))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        verify(ticketService, times(1)).createTicket(any(TicketCreateRequest.class));
    }

    @Test
    void createTicket_whenTicketTypeNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(ticketService.createTicket(any(TicketCreateRequest.class)))
                .thenThrow(new NotFoundException("Ticket type not found"));

        // When & Then
        mockMvc.perform(post("/tickets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).createTicket(any(TicketCreateRequest.class));
    }

    @Test
    void createTicket_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        // Given
        TicketCreateRequest invalidRequest = new TicketCreateRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/tickets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).createTicket(any(TicketCreateRequest.class));
    }

    @Test
    void updateTicket_whenValidRequest_shouldReturnUpdatedTicket() throws Exception {
        // Given
        when(ticketService.updateTicket(eq(1L), any(TicketCreateRequest.class))).thenReturn(ticketResponse);

        // When & Then
        mockMvc.perform(put("/tickets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.ticketCode").value("TKT-2024-001"))
                .andExpect(jsonPath("$.totalAmount").value(150.00));

        verify(ticketService, times(1)).updateTicket(eq(1L), any(TicketCreateRequest.class));
    }

    @Test
    void updateTicket_whenTicketNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(ticketService.updateTicket(eq(999L), any(TicketCreateRequest.class)))
                .thenThrow(new NotFoundException("Ticket not found"));

        // When & Then
        mockMvc.perform(put("/tickets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).updateTicket(eq(999L), any(TicketCreateRequest.class));
    }

    @Test
    void updateTicket_whenTicketTypeNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(ticketService.updateTicket(eq(1L), any(TicketCreateRequest.class)))
                .thenThrow(new NotFoundException("Ticket type not found"));

        // When & Then
        mockMvc.perform(put("/tickets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).updateTicket(eq(1L), any(TicketCreateRequest.class));
    }

    @Test
    void deleteTicket_whenTicketExists_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(ticketService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/tickets/1"))
                .andExpect(status().isNoContent());

        verify(ticketService, times(1)).delete(1L);
    }

    @Test
    void deleteTicket_whenTicketNotFound_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new NotFoundException("Ticket not found")).when(ticketService).delete(999L);

        // When & Then
        mockMvc.perform(delete("/tickets/999"))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).delete(999L);
    }

    @Test
    void createTicket_withDifferentPaymentModes_shouldWorkCorrectly() throws Exception {
        // Given
        TicketCreateRequest upiRequest = new TicketCreateRequest();
        upiRequest.setUserId(1L);
        TicketCreateRequest.TicketTypeRequest typeRequest = new TicketCreateRequest.TicketTypeRequest();
        typeRequest.setTicketTypeId(1);
        upiRequest.setTicketType(typeRequest);
        upiRequest.setTotalAmount(new BigDecimal("150.00"));
        upiRequest.setPaymentStatus("PAID");
        upiRequest.setPaymentMode("UPI");

        when(ticketService.createTicket(any(TicketCreateRequest.class))).thenReturn(ticketResponse);

        // When & Then
        mockMvc.perform(post("/tickets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(upiRequest)))
                .andExpect(status().isCreated());

        verify(ticketService, times(1)).createTicket(any(TicketCreateRequest.class));
    }

    @Test
    void createTicket_withDifferentPaymentStatuses_shouldWorkCorrectly() throws Exception {
        // Given
        TicketCreateRequest pendingRequest = new TicketCreateRequest();
        pendingRequest.setUserId(1L);
        TicketCreateRequest.TicketTypeRequest typeRequest = new TicketCreateRequest.TicketTypeRequest();
        typeRequest.setTicketTypeId(1);
        pendingRequest.setTicketType(typeRequest);
        pendingRequest.setTotalAmount(new BigDecimal("150.00"));
        pendingRequest.setPaymentStatus("PENDING");
        pendingRequest.setPaymentMode("CARD");

        when(ticketService.createTicket(any(TicketCreateRequest.class))).thenReturn(ticketResponse);

        // When & Then
        mockMvc.perform(post("/tickets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pendingRequest)))
                .andExpect(status().isCreated());

        verify(ticketService, times(1)).createTicket(any(TicketCreateRequest.class));
    }
}
