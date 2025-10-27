package com.example.srsanalyzer.Controller;

import com.example.srsanalyzer.Service.AnalyzerService;
import com.example.srsanalyzer.Service.AnalyzerServiceV2;
import com.example.srsanalyzer.Service.PlannerAgentService;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
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
  private final AnalyzerServiceV2 analyzerServiceV2;
  private final PlannerAgentService plannerAgent;

  public AnalyzerRestController(AnalyzerService analyzerService,
                                AnalyzerServiceV2 analyzerServiceV2, PlannerAgentService plannerAgent) {
    this.analyzerService = analyzerService;
    this.analyzerServiceV2 = analyzerServiceV2;
    this.plannerAgent = plannerAgent;
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
  @PostMapping("/V2")
  public String analyzeFilesV2(@RequestParam("file1") MultipartFile file1,
                             @RequestParam(required = false) MultipartFile file2,
                             @RequestParam(required = false)
                             String prompt
  )
      throws IOException {
    return analyzerServiceV2.compare(file1, file2, prompt);
  }

  @PostMapping("/execute")
  public ResponseEntity<String> execute(
      @RequestParam("prompt") String prompt,
      @RequestParam(value = "fileA", required = false) MultipartFile fileA,
      @RequestParam(value = "fileB", required = false) MultipartFile fileB) throws IOException {

    MultipartFile[] files = (fileA != null && fileB != null) ? new MultipartFile[]{fileA, fileB} : new MultipartFile[]{};
    String result = plannerAgent.execute(prompt, files);
    return ResponseEntity.ok(result);
  }

  /**
   *
   */
  @GetMapping("")
  public String getString() {
    return "HELLo";
  }



}
