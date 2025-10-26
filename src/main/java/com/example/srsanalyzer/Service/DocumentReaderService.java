package com.example.srsanalyzer.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentReaderService {

  String readMultipart(MultipartFile file) throws IOException;

  String readFromPath(String path) throws FileNotFoundException;
}
