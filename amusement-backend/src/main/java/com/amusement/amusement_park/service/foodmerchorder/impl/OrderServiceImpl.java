package com.amusement.amusement_park.service.foodmerchorder.impl;

import com.amusement.amusement_park.entity.foodmerchorder.Order;
import com.amusement.amusement_park.entity.foodmerchorder.OrderItem;
import com.amusement.amusement_park.entity.foodmerchorder.ItemType;
import com.amusement.amusement_park.repository.foodmerchorder.OrderRepository;
import com.amusement.amusement_park.service.foodmerchorder.OrderService;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;

    // -------------------------------
    // 1. Place a new order
    // -------------------------------
    @Override
    public Order placeOrder(Order order, String couponCode) {
        // 🔒 SECURITY FIX: Validate that the order belongs to the authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Ensure the order belongs to the authenticated user
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("Cannot place order for another user");
        }

        // Calculate total from order items
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItemList()) {
            // Calculate total for this item
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        // Apply coupon discount if applicable
        if ("DISCOUNT10".equalsIgnoreCase(couponCode)) {
            total = total.multiply(BigDecimal.valueOf(0.9)); // 10% off
        }

        // Set order properties
        order.setTotalAmount(total);
        order.setStatus("PLACED");
        order.setOrderTime(LocalDateTime.now());

        // Save and return the order
        return orderRepository.save(order);
    }

    // -------------------------------
    // 2. Get an order by ID
    // -------------------------------
    @Override
    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        
        // 🔒 SECURITY FIX: Validate that the authenticated user can access this order
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            String userEmail = auth.getName();
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if user is ADMIN or if the order belongs to the current user
            if (!"ADMIN".equals(currentUser.getRole()) && !order.getUserId().equals(currentUser.getId())) {
                throw new RuntimeException("Access denied: Cannot access order from another user");
            }
        }
        
        return order;
    }

    // -------------------------------
    // 3. Get all orders by a user (with optional filtering and sorting)
    // -------------------------------
    @Override
    public List<Order> getOrdersByUserId(Long userId, String status, String sort) {
        // 🔒 SECURITY FIX: Validate that the authenticated user can access these orders
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            String userEmail = auth.getName();
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if user is ADMIN or if they're requesting their own orders
            if (!"ADMIN".equals(currentUser.getRole()) && !userId.equals(currentUser.getId())) {
                throw new RuntimeException("Access denied: Cannot access orders from another user");
            }
        }
        
        List<Order> orders = orderRepository.findByUserId(userId);

        // Filter by status if provided
        if (status != null && !status.trim().isEmpty()) {
            orders = orders.stream()
                    .filter(order -> status.equalsIgnoreCase(order.getStatus()))
                    .collect(Collectors.toList());
        }

        // Sort based on ID (or other fields)
        Comparator<Order> comparator = Comparator.comparing(Order::getId);
        if ("desc".equalsIgnoreCase(sort)) {
            comparator = comparator.reversed();
        }

        return orders.stream().sorted(comparator).collect(Collectors.toList());
    }

    // -------------------------------
    // 4. Update order status
    // -------------------------------
    @Override
    public Order updateStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status.trim());
        return orderRepository.save(order);
    }

    // -------------------------------
    // 5. Delete order by ID
    // -------------------------------
    @Override
    public void deleteOrderById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order with ID " + id + " does not exist.");
        }
        orderRepository.deleteById(id);
    }

    // -------------------------------
    // 6. Get all orders (Admin dashboard)
    // -------------------------------
    @Override
    public List<Order> getAllOrders() {
        // 🔒 SECURITY FIX: Only ADMIN users can access all orders
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            String userEmail = auth.getName();
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!"ADMIN".equals(currentUser.getRole())) {
                throw new RuntimeException("Access denied: Only ADMIN users can view all orders");
            }
        }
        
        return orderRepository.findAll();
    }

    // -------------------------------
    // 7. Get orders by status (e.g., PREPARING, READY)
    // -------------------------------
    @Override
    public List<Order> getOrdersByStatus(String status) {
        // 🔒 SECURITY FIX: Only ADMIN users can access orders by status
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            String userEmail = auth.getName();
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!"ADMIN".equals(currentUser.getRole())) {
                throw new RuntimeException("Access denied: Only ADMIN users can view orders by status");
            }
        }
        
        return orderRepository.findByStatusIgnoreCase(status);
    }

    // -------------------------------
    // 8. Get available pickup locations (Mocked for now)
    // -------------------------------
    @Override
    public List<String> getAvailablePickupLocations() {
        return List.of(
                "Zone A - Burger Shack",
                "Zone B - Cold Drinks Corner",
                "Zone C - Gift Shop",
                "Main Entrance Kiosk"
        );
    }
}
