package com.amusement.amusement_park.repository.foodmerchorder;

import com.amusement.amusement_park.entity.foodmerchorder.MerchandiseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MerchandiseItemRepository extends JpaRepository<MerchandiseItem, Long> {
    boolean existsByName(String name);
    Optional<MerchandiseItem> findByName(String name);
}
