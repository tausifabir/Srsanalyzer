package com.example.srsanalyzer.Entity;

import java.util.Map;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Document(collection = "memory_store")
public class MemoryEntity {
  @Id
  private String id;

  private Map<String, String> memory;
}
