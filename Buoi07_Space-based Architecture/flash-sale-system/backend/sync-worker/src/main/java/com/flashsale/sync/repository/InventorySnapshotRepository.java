package com.flashsale.sync.repository;

import com.flashsale.sync.model.InventorySnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventorySnapshotRepository extends MongoRepository<InventorySnapshot, String> {
}
