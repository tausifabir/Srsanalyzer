package com.example.srsanalyzer.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AnalyzerServiceV2Impl implements AnalyzerServiceV2 {


  private final DocumentReaderService documentReader;
  private static final Gson GSON = new Gson();

  public AnalyzerServiceV2Impl(DocumentReaderService documentReader) {
    this.documentReader = documentReader;
  }

  @Override
  public String compare(MultipartFile fileA, MultipartFile fileB, String prompt) throws IOException {
    String srsA = documentReader.readMultipart(fileA);
//    String srsB = documentReader.readMultipart(fileB);

    String fullPrompt = getPromptWithGivenFiles1(srsA, prompt);

    // Call Ollama local model (e.g., llama3)
    JsonObject request = new JsonObject();
    request.addProperty("model", "llama3.2:latest"); // You can change this model name
    request.addProperty("prompt", fullPrompt);

    URL url = new URL("http://localhost:11434/api/generate");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    try (OutputStream os = conn.getOutputStream()) {
      os.write(GSON.toJson(request).getBytes(StandardCharsets.UTF_8));
      System.out.println(GSON.toJson(request));
    }

    StringBuilder response = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        // Ollama streams JSON lines
        JsonObject jsonLine = GSON.fromJson(line, JsonObject.class);
        if (jsonLine.has("response")) {
          response.append(jsonLine.get("response").getAsString());
        }
      }
    }

    conn.disconnect();
    return response.toString().trim();
  }

  private String getPromptWithGivenFiles(String srsA, String srsB, String prompt) {
    return String.format(
        srsA, srsB, prompt);
  }
  private String getPromptWithGivenFiles1(String srsA, String prompt) {
    return String.format(prompt + srsA);
  }
}
