package com.amusement.amusement_park.service.foodmerchorder;
import java.util.List;
import com.amusement.amusement_park.entity.foodmerchorder.Order;

public interface OrderService {

    Order placeOrder(Order order, String couponCode);

    Order updateStatus(Long id, String status);

    Order getOrderById(Long id);

    List<Order> getOrdersByUserId(Long userId, String status, String sort);

    void deleteOrderById(Long id);

    // ✅ Missing ones below
    List<Order> getAllOrders();

    List<Order> getOrdersByStatus(String status);

    List<String> getAvailablePickupLocations();
}
