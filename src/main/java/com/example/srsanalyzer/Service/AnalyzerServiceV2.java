package com.example.srsanalyzer.Service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface AnalyzerServiceV2 {

  String compare(MultipartFile fileA,
                 MultipartFile fileB,
                 String prompt) throws IOException;

}
