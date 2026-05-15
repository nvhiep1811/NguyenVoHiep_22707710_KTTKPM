package com.flashsale.sync.repository;

import com.flashsale.sync.model.FlashSale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashSaleRepository extends MongoRepository<FlashSale, String> {
}
