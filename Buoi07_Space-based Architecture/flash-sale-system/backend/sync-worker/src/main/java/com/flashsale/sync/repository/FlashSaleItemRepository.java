package com.flashsale.sync.repository;

import com.flashsale.sync.model.FlashSaleItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashSaleItemRepository extends MongoRepository<FlashSaleItem, String> {
    List<FlashSaleItem> findByFlashSaleIdAndStatus(String flashSaleId, String status);
}
