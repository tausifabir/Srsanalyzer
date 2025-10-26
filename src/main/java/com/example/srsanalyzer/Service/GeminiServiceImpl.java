package com.example.srsanalyzer.Service;

import static com.example.srsanalyzer.constants.ApplicationConstants.API_KEY;
import static com.example.srsanalyzer.constants.ApplicationConstants.GEMINI_BASE_URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Service;

@Service
public class GeminiServiceImpl implements GeminiService {

  @Override
  public String analyze(String srsA, String srsB, String prompt) throws IOException {

    String payload = getPromptWithGivenFiles(srsA, srsB, prompt);

    URL url = new URL(GEMINI_BASE_URL + API_KEY);

    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    try (OutputStream os = conn.getOutputStream()) {
      os.write(payload.getBytes());
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String response = reader.lines().reduce("", (a, b) -> a + b);

    reader.close();
    conn.disconnect();

    // Extract main text using a simple regex (can be replaced by Gson)
    int textStart = response.indexOf("\"text\":");
    if (textStart > 0) {
      int start = response.indexOf("\"", textStart + 7) + 1;
      int end = response.indexOf("\"", start);
      return response.substring(start, end).replace("\\n", "\n");
    } else {
      return "No result parsed. Raw response:\n" + response;
    }
  }

  private static String getPromptWithGivenFiles(String srsA, String srsB, String givenPrompt) {
    String prompt = """
            You are an AI Business Analyst.
            Compare the two SRS documents and provide:
            1. Connected or overlapping modules
            2. Missing business modules (gaps)
            3. Observations or recommendations

            --- SRS A ---
            %s

            --- SRS B ---
            %s
            """.formatted(srsA, srsB);

    return """
        {
          "contents": [{
            "parts": [{"text": "%s"}]
          }]
        }
        """.formatted(prompt.replace("\"", "\\\""));
  }
}
