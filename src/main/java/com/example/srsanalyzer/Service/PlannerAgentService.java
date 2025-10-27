package com.example.srsanalyzer.Service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PlannerAgentService implements AgentService {

  private DiagramAgentService diagramAgentService;
  private TextAgentService textAgentService;

  public PlannerAgentService(TextAgentService textAgentService) {
    this.textAgentService = textAgentService;
  }

  @Override
  public String execute(String prompt, MultipartFile... files) throws IOException {

    String lowerPrompt = prompt.toLowerCase();

    // Simple routing logic (can be LLM-based later)
    if (lowerPrompt.contains("diagram") || lowerPrompt.contains("flowchart") || lowerPrompt.contains("uml")) {
      return diagramAgentService.execute(prompt, files);
    } else {
      return textAgentService.execute(prompt, files);
    }
  }
}
