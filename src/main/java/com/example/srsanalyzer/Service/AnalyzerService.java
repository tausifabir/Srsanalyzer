package com.example.srsanalyzer.Service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface AnalyzerService {

  String compare(MultipartFile fileA, MultipartFile fileB, String prompt) throws IOException;
}
