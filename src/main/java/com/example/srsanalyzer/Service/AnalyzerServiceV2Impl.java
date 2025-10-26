package com.example.srsanalyzer.Service;

import static com.example.srsanalyzer.Service.GeminiServiceImpl.getPromptWithGivenFiles;
import static com.example.srsanalyzer.constants.ApplicationConstants.API_KEY;
import static com.example.srsanalyzer.constants.ApplicationConstants.GEMINI_BASE_URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AnalyzerServiceV2Impl implements AnalyzerServiceV2 {


  private final DocumentReaderService documentReader;
  private static final Gson GSON = new Gson();


  /** Constructor.*/
  public AnalyzerServiceV2Impl(DocumentReaderService documentReader) {
    this.documentReader = documentReader;
  }

  @Override
  public String compare(MultipartFile fileA, MultipartFile fileB, String prompt) throws IOException {
     // Optional: Check token count first
    // Optional: Check token count first

    String srsA = documentReader.readMultipart(fileA);
    String srsB = documentReader.readMultipart(fileB);

    String fullPrompt = getPromptWithGivenFiles(srsA, srsB, prompt);
    if (getTokenCount(fullPrompt) > 250000) {
      throw new IOException("Prompt exceeds free tier TPM (250K). Consider chunking SRS files or upgrading to paid tier.");
    }


    URL url = new URL(GEMINI_BASE_URL + API_KEY);

    for (int retry = 0; retry < 3; retry++) { // Max 3 retries
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);

      try (OutputStream os = conn.getOutputStream()) {
        os.write(fullPrompt.getBytes(StandardCharsets.UTF_8));
      }

      int responseCode = conn.getResponseCode();
      InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        String response = reader.lines().reduce("", (a, b) -> a + b);
        if (responseCode == 200) {
          JsonObject jsonResponse = GSON.fromJson(response, JsonObject.class);
          JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
          if (candidates != null && !candidates.isEmpty()) {
            JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts != null && !parts.isEmpty()) {
              return parts.get(0).getAsJsonObject().get("text").getAsString().replace("\\n", "\n");
            }
          }
          return "No result parsed. Raw response:\n" + response;
        } else if (responseCode == 429) {
          JsonObject errorJson = GSON.fromJson(response, JsonObject.class);
          String retryDelayStr = "30"; // Default
          if (errorJson.has("details")) {
            JsonArray details = errorJson.getAsJsonArray("details");
            if (details.size() > 2) {
              JsonObject retryInfo = details.get(2).getAsJsonObject();
              if (retryInfo.has("retryDelay")) {
                retryDelayStr = retryInfo.get("retryDelay").getAsString().replace("s", "");
              }
            }
          }
          long delaySeconds = (long) (Double.parseDouble(retryDelayStr) * Math.pow(2, retry)); // Exponential backoff
          System.out.println("Rate limit hit. Retrying in " + delaySeconds + "s...");
          TimeUnit.SECONDS.sleep(delaySeconds);
          continue; // Retry
        } else {
          throw new IOException("Server returned HTTP " + responseCode + ": " + response);
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } finally {
        conn.disconnect();
      }
    }
    throw new IOException("Max retries exceeded for rate limit.");
  }


  private int getTokenCount(String prompt) throws IOException {
    // Use countTokens endpoint
    URL countUrl = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:countTokens?key=" + API_KEY);
    HttpURLConnection countConn = (HttpURLConnection) countUrl.openConnection();
    countConn.setRequestMethod("POST");
    countConn.setRequestProperty("Content-Type", "application/json");
    countConn.setDoOutput(true);

    try (OutputStream os = countConn.getOutputStream()) {
      os.write(("{\"contents\": [{\"parts\": [{\"text\": \"" + prompt.replace("\"", "\\\"") + "\"}]}]}").getBytes(StandardCharsets.UTF_8));
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(countConn.getInputStream()))) {
      String response = reader.lines().reduce("", (a, b) -> a + b);
      JsonObject json = GSON.fromJson(response, JsonObject.class);
      return json.getAsJsonObject("usageMetadata").get("promptTokenCount").getAsInt();
    } finally {
      countConn.disconnect();
    }
  }
}
