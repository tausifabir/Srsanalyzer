package com.example.srsanalyzer.Service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface AgentService {
  String execute(String prompt, MultipartFile... files) throws IOException;
}
