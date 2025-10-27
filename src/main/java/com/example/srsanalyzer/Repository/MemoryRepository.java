package com.example.srsanalyzer.Repository;

import com.example.srsanalyzer.Entity.MemoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemoryRepository extends MongoRepository<MemoryEntity, String> {
}
