package com.amusement.amusement_park.scheduler;

import com.amusement.amusement_park.entity.foodmerchorder.Order;
import com.amusement.amusement_park.repository.foodmerchorder.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderStatusScheduler {

    @Autowired
    private OrderRepository orderRepository;

    // Run every 5 minutes
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void autoUpdateOrderStatus() {
        List<Order> activeOrders = orderRepository.findByStatusIn(List.of("PLACED", "CONFIRMED", "PREPARING", "READY"));

        for (Order order : activeOrders) {
            String nextStatus = getNextStatus(order.getStatus());

            // Optionally skip if no valid next status
            if (nextStatus != null) {
                order.setStatus(nextStatus);
                System.out.println("Order ID " + order.getId() + " updated to " + nextStatus);
            }
        }

        // Save all updated orders
        orderRepository.saveAll(activeOrders);
    }

    private String getNextStatus(String current) {
        return switch (current) {
            case "PLACED" -> "CONFIRMED";
            case "CONFIRMED" -> "PREPARING";
            case "PREPARING" -> "READY";
            case "READY" -> "PICKED_UP"; // Optional: Auto-pickup
            default -> null;
        };
    }
}
