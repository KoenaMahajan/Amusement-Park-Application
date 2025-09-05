package com.amusement.amusement_park.controller.foodmerchorder;


import com.amusement.amusement_park.entity.foodmerchorder.MerchandiseItem;
import com.amusement.amusement_park.service.foodmerchorder.MerchandiseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merch")
public class MerchandiseController {

    @Autowired
    private MerchandiseService merchService;

    // ✅ Accessible by ROLE_USER and ROLE_ADMIN
    @GetMapping("/store")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<MerchandiseItem> getStoreItems() {
        return merchService.getAllItems();
    }

    // ✅ Accessible only by ROLE_ADMIN
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public MerchandiseItem addMerch(@Valid @RequestBody MerchandiseItem item) {
        return merchService.addMerchItem(item);
    }

    // ✅ Accessible only by ADMIN
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteMerch(@PathVariable Long id) {
        merchService.deleteMerchItem(id);
        return "Merchandise item with ID " + id + " deleted successfully.";
        }



}
