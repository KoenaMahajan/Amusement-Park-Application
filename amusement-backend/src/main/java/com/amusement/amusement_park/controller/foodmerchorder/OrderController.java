package com.amusement.amusement_park.controller.foodmerchorder;

import com.amusement.amusement_park.entity.foodmerchorder.Order;
import com.amusement.amusement_park.service.foodmerchorder.OrderService;
import com.amusement.amusement_park.entity.user.User;
import com.amusement.amusement_park.repository.user.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserRepository userRepository;

    // 1. Place new order
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/place")
    public Order placeOrder(@Valid @RequestBody Order order,
                        @RequestParam(required = false) String couponCode) {
        // 🔒 SECURITY FIX: Set the current user's ID from authentication context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Ensure the order belongs to the authenticated user
        order.setUserId(currentUser.getId());
        return orderService.placeOrder(order, couponCode);
    }

    // 2. Update order status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable @Positive Long id,
                          @RequestParam @NotBlank String status){
        return orderService.updateStatus(id, status);
    }

    // 3. Get order by ID
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable @Positive Long id) {
        // 🔒 SECURITY FIX: Validate that user can only access their own orders (unless ADMIN)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Order order = orderService.getOrderById(id);
        
        // Check if user is ADMIN or if the order belongs to the current user
        if (!"ADMIN".equals(currentUser.getRole()) && !order.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(order);
    }

    // 4. Get all orders for a user
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable @Positive Long userId,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false, defaultValue = "asc") String sort) {
        // 🔒 SECURITY FIX: Validate that user can only access their own orders (unless ADMIN)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is ADMIN or if they're requesting their own orders
        if (!"ADMIN".equals(currentUser.getRole()) && !userId.equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Order> orders = orderService.getOrdersByUserId(userId, status, sort);
        return ResponseEntity.ok(orders);
    }

    // 5. Delete an order
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable @Positive Long id) {
        try {
            orderService.deleteOrderById(id);
            return ResponseEntity.ok("Order with ID " + id + " deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // 6. Get all orders (for dashboard)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 7. Get orders by status (e.g., READY, PREPARING)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable String status) {
        return orderService.getOrdersByStatus(status);
    }

    // 8. Manually mark an order as picked up
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}/pickup")
    public Order markOrderAsPickedUp(@PathVariable Long id) {
        return orderService.updateStatus(id, "PICKED_UP");
    }

    // 9. Get available pickup locations (static or DB-driven)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/pickup-locations")
    public List<String> getPickupLocations() {
        return orderService.getAvailablePickupLocations();
    }
    
    // 🔒 NEW SECURE ENDPOINT: Get current user's orders without exposing user ID
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "asc") String sort) {
        
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get orders for the current user only
        List<Order> orders = orderService.getOrdersByUserId(currentUser.getId(), status, sort);
        return ResponseEntity.ok(orders);
    }
}
