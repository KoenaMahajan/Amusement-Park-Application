package com.amusement.amusement_park.service.foodmerchorder;

import com.amusement.amusement_park.entity.foodmerchorder.MerchandiseItem;
import java.util.List;

public interface MerchandiseService {
    
    MerchandiseItem addMerchItem(MerchandiseItem item);

    void deleteMerchItem(Long id);

    List<MerchandiseItem> getAllItems();
}
