package com.amusement.amusement_park.repository.foodmerchorder;

import com.amusement.amusement_park.entity.foodmerchorder.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatusIgnoreCase(Long userId, String status);
    List<Order> findByStatusIn(List<String> statuses);
    List<Order> findByStatusIgnoreCase(String status);


}

