package com.amusement.amusement_park.service.foodmerchorder.impl;

import com.amusement.amusement_park.exception.DuplicateMerchandiseItemException;
import com.amusement.amusement_park.entity.foodmerchorder.MerchandiseItem;
import com.amusement.amusement_park.repository.foodmerchorder.MerchandiseItemRepository;
import com.amusement.amusement_park.service.foodmerchorder.MerchandiseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MerchandiseServiceImpl implements MerchandiseService {

    @Autowired
    private MerchandiseItemRepository merchRepo;

    @Override
    public MerchandiseItem addMerchItem(MerchandiseItem item) {
    // Validate name
    if (item.getName() == null || item.getName().trim().isEmpty()) {
        throw new IllegalArgumentException("Merchandise name cannot be blank or whitespace.");
    }

    // ✅ Validate price
    // ✅ Throws if negative
    if (item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
    throw new IllegalArgumentException("Merchandise price cannot be negative.");
    }


    // Check for duplicate by name
    if (merchRepo.existsByName(item.getName())) {
        throw new DuplicateMerchandiseItemException(item.getName());
    }

    return merchRepo.save(item);
}




    @Override
    public void deleteMerchItem(Long id) {
        merchRepo.deleteById(id);
    }

    @Override
    public List<MerchandiseItem> getAllItems() {
        return merchRepo.findAll();
    }
}
