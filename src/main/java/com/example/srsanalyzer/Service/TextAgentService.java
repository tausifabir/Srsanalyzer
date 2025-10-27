package com.example.srsanalyzer.Service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TextAgentService implements AgentService{
  @Override
  public String execute(String prompt, MultipartFile... files) throws IOException {
    return "";
  }
}
