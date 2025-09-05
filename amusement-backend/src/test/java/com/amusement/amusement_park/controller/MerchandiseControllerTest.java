package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.foodmerchorder.MerchandiseController;
import com.amusement.amusement_park.entity.foodmerchorder.MerchandiseItem;
import com.amusement.amusement_park.service.foodmerchorder.MerchandiseService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MerchandiseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MerchandiseService merchService;

    @InjectMocks
    private MerchandiseController merchandiseController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(merchandiseController).build();
    }

    @Test
    public void testGetStoreItems() throws Exception {
        MerchandiseItem item1 = new MerchandiseItem(1L, "Cap", new BigDecimal("299.99"));
        MerchandiseItem item2 = new MerchandiseItem(2L, "T-Shirt", new BigDecimal("499.99"));
        List<MerchandiseItem> items = Arrays.asList(item1, item2);

        when(merchService.getAllItems()).thenReturn(items);

        mockMvc.perform(get("/merch/store"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void testAddMerch() throws Exception {
        MerchandiseItem item = new MerchandiseItem(null, "Keychain", new BigDecimal("99.99"));
        MerchandiseItem savedItem = new MerchandiseItem(1L, "Keychain", new BigDecimal("99.99"));

        when(merchService.addMerchItem(any(MerchandiseItem.class))).thenReturn(savedItem);

        mockMvc.perform(post("/merch/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Keychain"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    public void testDeleteMerch() throws Exception {
        Long id = 1L;
        doNothing().when(merchService).deleteMerchItem(id);

        mockMvc.perform(delete("/merch/delete/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Merchandise item with ID 1 deleted successfully."));
    }
}
