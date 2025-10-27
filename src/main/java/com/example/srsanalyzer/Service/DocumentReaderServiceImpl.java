package com.example.srsanalyzer.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentReaderServiceImpl implements DocumentReaderService {


  public String readMultipart(MultipartFile file) throws IOException {
    String filename = file.getOriginalFilename();

    if (filename == null) {
      throw new IOException("File has no name.");
    }

    if (filename.endsWith(".docx")) {
      return readDocx(file.getInputStream());
    } else if (filename.endsWith(".pdf")) {
      return readPdf(file.getInputStream());
    } else {
      return new BufferedReader(new InputStreamReader(file.getInputStream()))
          .lines()
          .collect(Collectors.joining("\n"));
    }
  }

  private String readPdf(InputStream inputStream) throws IOException {
    try (PDDocument document = PDDocument.load(inputStream)) {
      PDFTextStripper pdfStripper = new PDFTextStripper();
      return pdfStripper.getText(document);
    }
  }



  @Override
  public String readFromPath(String path) throws FileNotFoundException {
    if (path.endsWith(".docx")) {
      try (FileInputStream fis = new FileInputStream(path)) {
        return readDocx(fis);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      return new BufferedReader(new FileReader(path))
          .lines()
          .collect(Collectors.joining("\n"));
    }
  }

  public static String readDocx(InputStream inputStream) throws IOException {
    try (XWPFDocument doc = new XWPFDocument(inputStream)) {
      return doc.getParagraphs().stream()
          .map(p -> p.getText().trim())
          .filter(t -> !t.isEmpty())
          .collect(Collectors.joining("\n"));
    }
  }
}
