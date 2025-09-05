package com.amusement.amusement_park.controller;

import com.amusement.amusement_park.controller.foodmerchorder.OrderController;
import com.amusement.amusement_park.entity.foodmerchorder.ItemType;
import com.amusement.amusement_park.entity.foodmerchorder.Order;
import com.amusement.amusement_park.entity.foodmerchorder.OrderItem;
import com.amusement.amusement_park.service.foodmerchorder.OrderService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testPlaceOrder() throws Exception {
        Order order = new Order();
        order.setUserId(101L);
        order.setPickupLocation("Main Gate");
        order.setStatus("PENDING");

        OrderItem item = new OrderItem();
        item.setItemId(1L);
        item.setItemName("Burger");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("99.99"));
        item.setItemType(ItemType.FOOD);

        order.setItemList(Arrays.asList(item));

        when(orderService.placeOrder(any(Order.class), eq(null))).thenReturn(order);

        mockMvc.perform(post("/orders/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickupLocation").value("Main Gate"))
                .andExpect(jsonPath("$.itemList[0].itemName").value("Burger"))
                .andExpect(jsonPath("$.itemList[0].quantity").value(2));
    }

    @Test
    public void testUpdateOrderStatus() throws Exception {
        Order updatedOrder = new Order();
        updatedOrder.setStatus("READY");

        when(orderService.updateStatus(1L, "READY")).thenReturn(updatedOrder);

        mockMvc.perform(put("/orders/1/status?status=READY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    public void testDeleteOrderById() throws Exception {
        doNothing().when(orderService).deleteOrderById(1L);

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order with ID 1 deleted"));
    }
}
