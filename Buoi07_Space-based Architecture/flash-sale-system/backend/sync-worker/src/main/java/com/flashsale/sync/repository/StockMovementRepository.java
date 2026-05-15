package com.flashsale.sync.repository;

import com.flashsale.sync.model.StockMovement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends MongoRepository<StockMovement, String> {
}
