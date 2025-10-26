package com.example.srsanalyzer.Service;

import java.io.IOException;

public interface GeminiService {

  String analyze(String srsA, String srsB,String prompt) throws IOException;
}
