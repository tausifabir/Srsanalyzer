package com.example.srsanalyzer.Controller;

import com.example.srsanalyzer.Service.AnalyzerService;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/srs-analysis")
public class AnalyzerRestController {

  private final AnalyzerService analyzerService;

  public AnalyzerRestController(AnalyzerService analyzerService) {
    this.analyzerService = analyzerService;
  }


  /**
   *
   */
  @PostMapping("")
  public String analyzeFiles(@RequestParam("file1") MultipartFile file1,
                             @RequestParam("file2") MultipartFile file2,
                             @RequestParam(required = false)
                             String prompt
  )
      throws IOException {
    return analyzerService.compare(file1, file2, prompt);
  }

  /**
   *
   */
  @GetMapping("")
  public String getString() {
    return "HELLo";
  }



}
