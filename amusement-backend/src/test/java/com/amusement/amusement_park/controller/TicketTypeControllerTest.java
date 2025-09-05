package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.ticket.TicketTypeController;
import com.amusement.amusement_park.entity.ticket.TicketType;
import com.amusement.amusement_park.exception.NotFoundException;
import com.amusement.amusement_park.service.ticket.TicketTypeService;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TicketTypeControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Mock
    private TicketTypeService ticketTypeService;

    @InjectMocks
    private TicketTypeController ticketTypeController;

    private TicketType ticketType;
    private TicketType vipTicketType;
    private TicketType regularTicketType;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketTypeController).build();

        // Setup test data
        ticketType = new TicketType();
        ticketType.setTicketTypeId(1);
        ticketType.setName("Standard Pass");
        ticketType.setDescription("Access to basic attractions");
        ticketType.setPrice(new BigDecimal("50.00"));
        ticketType.setValidityDays(1);
        ticketType.setIsVip(false);

        vipTicketType = new TicketType();
        vipTicketType.setTicketTypeId(2);
        vipTicketType.setName("VIP Premium Pass");
        vipTicketType.setDescription("Exclusive access to all premium attractions");
        vipTicketType.setPrice(new BigDecimal("299.99"));
        vipTicketType.setValidityDays(3);
        vipTicketType.setIsVip(true);

        regularTicketType = new TicketType();
        regularTicketType.setTicketTypeId(3);
        regularTicketType.setName("Regular Day Pass");
        regularTicketType.setDescription("Standard day access");
        regularTicketType.setPrice(new BigDecimal("75.00"));
        regularTicketType.setValidityDays(1);
        regularTicketType.setIsVip(false);
    }

    @Test
    void getAllTicketTypes_shouldReturnListOfTicketTypes() throws Exception {
        // Given
        List<TicketType> ticketTypes = Arrays.asList(ticketType, vipTicketType, regularTicketType);
        when(ticketTypeService.getAll()).thenReturn(ticketTypes);

        // When & Then
        mockMvc.perform(get("/ticket-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ticketTypeId").value(1))
                .andExpect(jsonPath("$[0].name").value("Standard Pass"))
                .andExpect(jsonPath("$[0].price").value(50.00))
                .andExpect(jsonPath("$[0].isVip").value(false))
                .andExpect(jsonPath("$[1].ticketTypeId").value(2))
                .andExpect(jsonPath("$[1].name").value("VIP Premium Pass"))
                .andExpect(jsonPath("$[1].price").value(299.99))
                .andExpect(jsonPath("$[1].isVip").value(true));

        verify(ticketTypeService, times(1)).getAll();
    }

    @Test
    void getTicketTypeById_whenTicketTypeExists_shouldReturnTicketType() throws Exception {
        // Given
        when(ticketTypeService.getById(1)).thenReturn(ticketType);

        // When & Then
        mockMvc.perform(get("/ticket-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketTypeId").value(1))
                .andExpect(jsonPath("$.name").value("Standard Pass"))
                .andExpect(jsonPath("$.description").value("Access to basic attractions"))
                .andExpect(jsonPath("$.price").value(50.00))
                .andExpect(jsonPath("$.validityDays").value(1))
                .andExpect(jsonPath("$.isVip").value(false));

        verify(ticketTypeService, times(1)).getById(1);
    }

    @Test
    void getTicketTypeById_whenTicketTypeNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(ticketTypeService.getById(999)).thenThrow(new NotFoundException("TicketType not found"));

        // When & Then
        mockMvc.perform(get("/ticket-types/999"))
                .andExpect(status().isNotFound());

        verify(ticketTypeService, times(1)).getById(999);
    }

    @Test
    void createTicketType_whenValidRequest_shouldReturnCreatedTicketType() throws Exception {
        // Given
        when(ticketTypeService.save(any(TicketType.class))).thenReturn(vipTicketType);

        // When & Then
        mockMvc.perform(post("/ticket-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vipTicketType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketTypeId").value(2))
                .andExpect(jsonPath("$.name").value("VIP Premium Pass"))
                .andExpect(jsonPath("$.description").value("Exclusive access to all premium attractions"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andExpect(jsonPath("$.validityDays").value(3))
                .andExpect(jsonPath("$.isVip").value(true));

        verify(ticketTypeService, times(1)).save(any(TicketType.class));
    }

    @Test
    void createTicketType_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        // Given
        TicketType invalidTicketType = new TicketType();
        // Missing required fields like name and price

        // When & Then
        mockMvc.perform(post("/ticket-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTicketType)))
                .andExpect(status().isBadRequest());

        verify(ticketTypeService, never()).save(any(TicketType.class));
    }

    @Test
    void createTicketType_withNegativePrice_shouldReturnBadRequest() throws Exception {
        // Given
        TicketType invalidTicketType = new TicketType();
        invalidTicketType.setName("Invalid Pass");
        invalidTicketType.setPrice(new BigDecimal("-10.00")); // Negative price

        // When & Then
        mockMvc.perform(post("/ticket-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTicketType)))
                .andExpect(status().isBadRequest());

        verify(ticketTypeService, never()).save(any(TicketType.class));
    }

    @Test
    void createTicketType_withZeroValidityDays_shouldReturnBadRequest() throws Exception {
        // Given
        TicketType invalidTicketType = new TicketType();
        invalidTicketType.setName("Invalid Pass");
        invalidTicketType.setPrice(new BigDecimal("50.00"));
        invalidTicketType.setValidityDays(0); // Zero validity days

        // When & Then
        mockMvc.perform(post("/ticket-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTicketType)))
                .andExpect(status().isBadRequest());

        verify(ticketTypeService, never()).save(any(TicketType.class));
    }

    @Test
    void updateTicketType_whenValidRequest_shouldReturnUpdatedTicketType() throws Exception {
        // Given
        TicketType updatedTicketType = new TicketType();
        updatedTicketType.setTicketTypeId(1);
        updatedTicketType.setName("Updated Standard Pass");
        updatedTicketType.setDescription("Updated access to basic attractions");
        updatedTicketType.setPrice(new BigDecimal("60.00"));
        updatedTicketType.setValidityDays(2);
        updatedTicketType.setIsVip(false);

        when(ticketTypeService.getById(1)).thenReturn(ticketType);
        when(ticketTypeService.save(any(TicketType.class))).thenReturn(updatedTicketType);

        // When & Then
        mockMvc.perform(put("/ticket-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTicketType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketTypeId").value(1))
                .andExpect(jsonPath("$.name").value("Updated Standard Pass"))
                .andExpect(jsonPath("$.description").value("Updated access to basic attractions"))
                .andExpect(jsonPath("$.price").value(60.00))
                .andExpect(jsonPath("$.validityDays").value(2))
                .andExpect(jsonPath("$.isVip").value(false));

        verify(ticketTypeService, times(1)).getById(1);
        verify(ticketTypeService, times(1)).save(any(TicketType.class));
    }

    @Test
    void updateTicketType_whenTicketTypeNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(ticketTypeService.getById(999)).thenThrow(new NotFoundException("TicketType not found"));

        // When & Then
        mockMvc.perform(put("/ticket-types/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketType)))
                .andExpect(status().isNotFound());

        verify(ticketTypeService, times(1)).getById(999);
        verify(ticketTypeService, never()).save(any(TicketType.class));
    }

    @Test
    void updateTicketType_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        // Given
        TicketType invalidTicketType = new TicketType();
        // Missing required fields

        // When & Then
        mockMvc.perform(put("/ticket-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTicketType)))
                .andExpect(status().isBadRequest());

        // When validation fails, the service layer is never called
        verify(ticketTypeService, never()).getById(any());
        verify(ticketTypeService, never()).save(any(TicketType.class));
    }

    @Test
    void deleteTicketType_whenTicketTypeExists_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(ticketTypeService).delete(1);

        // When & Then
        mockMvc.perform(delete("/ticket-types/1"))
                .andExpect(status().isNoContent());

        verify(ticketTypeService, times(1)).delete(1);
    }

    @Test
    void deleteTicketType_whenTicketTypeNotFound_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new NotFoundException("TicketType not found")).when(ticketTypeService).delete(999);

        // When & Then
        mockMvc.perform(delete("/ticket-types/999"))
                .andExpect(status().isNotFound());

        verify(ticketTypeService, times(1)).delete(999);
    }

    @Test
    void createTicketType_withVIPFlag_shouldWorkCorrectly() throws Exception {
        // Given
        TicketType vipType = new TicketType();
        vipType.setName("VIP Experience");
        vipType.setDescription("Ultimate VIP experience");
        vipType.setPrice(new BigDecimal("500.00"));
        vipType.setValidityDays(7);
        vipType.setIsVip(true);

        when(ticketTypeService.save(any(TicketType.class))).thenReturn(vipType);

        // When & Then
        mockMvc.perform(post("/ticket-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vipType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("VIP Experience"))
                .andExpect(jsonPath("$.isVip").value(true))
                .andExpect(jsonPath("$.validityDays").value(7));

        verify(ticketTypeService, times(1)).save(any(TicketType.class));
    }

    @Test
    void createTicketType_withRegularFlag_shouldWorkCorrectly() throws Exception {
        // Given
        TicketType regularType = new TicketType();
        regularType.setName("Regular Pass");
        regularType.setDescription("Standard access");
        regularType.setPrice(new BigDecimal("25.00"));
        regularType.setValidityDays(1);
        regularType.setIsVip(false);

        when(ticketTypeService.save(any(TicketType.class))).thenReturn(regularType);

        // When & Then
        mockMvc.perform(post("/ticket-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regularType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Regular Pass"))
                .andExpect(jsonPath("$.isVip").value(false))
                .andExpect(jsonPath("$.validityDays").value(1));

        verify(ticketTypeService, times(1)).save(any(TicketType.class));
    }

    @Test
    void updateTicketType_withPartialUpdate_shouldWorkCorrectly() throws Exception {
        // Given
        TicketType existingTicketType = new TicketType();
        existingTicketType.setTicketTypeId(1);
        existingTicketType.setName("Original Name");
        existingTicketType.setDescription("Original Description");
        existingTicketType.setPrice(new BigDecimal("50.00"));
        existingTicketType.setValidityDays(1);
        existingTicketType.setIsVip(false);

        TicketType updateRequest = new TicketType();
        updateRequest.setName("Updated Name");
        updateRequest.setPrice(new BigDecimal("75.00"));
        // Only updating name and price, keeping other fields as they are

        when(ticketTypeService.getById(1)).thenReturn(existingTicketType);
        when(ticketTypeService.save(any(TicketType.class))).thenReturn(existingTicketType);

        // When & Then
        mockMvc.perform(put("/ticket-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(ticketTypeService, times(1)).getById(1);
        verify(ticketTypeService, times(1)).save(any(TicketType.class));
    }

    @Test
    void getAllTicketTypes_whenEmptyList_shouldReturnEmptyArray() throws Exception {
        // Given
        when(ticketTypeService.getAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/ticket-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(ticketTypeService, times(1)).getAll();
    }
}
