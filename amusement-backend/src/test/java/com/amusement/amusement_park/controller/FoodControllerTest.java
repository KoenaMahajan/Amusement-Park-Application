package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.foodmerchorder.FoodController;
import com.amusement.amusement_park.entity.foodmerchorder.FoodItem;
import com.amusement.amusement_park.service.foodmerchorder.FoodItemService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FoodControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FoodItemService foodItemService;

    @InjectMocks
    private FoodController foodController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(foodController).build();
    }

    @Test
    public void testGetMenu() throws Exception {
        FoodItem item1 = new FoodItem(1L, "Burger", new BigDecimal("99.00"));
        FoodItem item2 = new FoodItem(2L, "Pizza", new BigDecimal("199.00"));

        List<FoodItem> menu = Arrays.asList(item1, item2);
        when(foodItemService.getAllItems()).thenReturn(menu);

        mockMvc.perform(get("/food/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testAddItem() throws Exception {
        FoodItem input = new FoodItem(null, "Fries", new BigDecimal("59.00"));
        FoodItem created = new FoodItem(3L, "Fries", new BigDecimal("59.00"));

        when(foodItemService.addFoodItem(any(FoodItem.class))).thenReturn(created);

        mockMvc.perform(post("/food/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fries"));
    }

    @Test
    public void testGetAvailableFoodItems() throws Exception {
        FoodItem item1 = new FoodItem(1L, "Burger", new BigDecimal("99.00"));
        FoodItem item2 = new FoodItem(2L, "Pizza", new BigDecimal("199.00"));

        when(foodItemService.getAllItems()).thenReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/food/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
}
