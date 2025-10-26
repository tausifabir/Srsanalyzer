package com.example.srsanalyzer.Service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AnalyzerServiceImpl implements AnalyzerService {

  private final GeminiService gemini;
  private final DocumentReaderService documentReader;

  /** Constructor.*/
  public AnalyzerServiceImpl(GeminiService gemini,
                             DocumentReaderService documentReader) {
    this.gemini = gemini;
    this.documentReader = documentReader;
  }

  @Override
  public String compare(MultipartFile fileA, MultipartFile fileB, String prompt) throws IOException {
    String srsA = documentReader.readMultipart(fileA);
    String srsB = documentReader.readMultipart(fileB);
    return gemini.analyze(srsA, srsB, prompt);
  }
}
