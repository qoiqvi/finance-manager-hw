package com.finance.infrastructure.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages JSON file storage operations.
 */
public class JsonStorageManager {
  private static final String DATA_DIR = "data";
  private final ObjectMapper objectMapper;

  /** Creates a new JsonStorageManager with configured ObjectMapper. */
  public JsonStorageManager() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    ensureDataDirectoryExists();
  }

  /**
   * Ensures the data directory exists, creates it if not.
   */
  private void ensureDataDirectoryExists() {
    try {
      Path dataPath = Paths.get(DATA_DIR);
      if (!Files.exists(dataPath)) {
        Files.createDirectories(dataPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to create data directory", e);
    }
  }

  /**
   * Writes an object to a JSON file.
   *
   * @param filename the filename (without path)
   * @param object the object to serialize
   * @throws IOException if write fails
   */
  public void writeToFile(String filename, Object object) throws IOException {
    File file = new File(DATA_DIR, filename);
    objectMapper.writeValue(file, object);
  }

  /**
   * Reads an object from a JSON file.
   *
   * @param filename the filename (without path)
   * @param clazz the class type to deserialize to
   * @param <T> the type parameter
   * @return the deserialized object
   * @throws IOException if read fails
   */
  public <T> T readFromFile(String filename, Class<T> clazz) throws IOException {
    File file = new File(DATA_DIR, filename);
    if (!file.exists()) {
      throw new IOException("File not found: " + filename);
    }
    return objectMapper.readValue(file, clazz);
  }

  /**
   * Checks if a file exists in the data directory.
   *
   * @param filename the filename to check
   * @return true if file exists
   */
  public boolean fileExists(String filename) {
    File file = new File(DATA_DIR, filename);
    return file.exists() && file.isFile();
  }

  /**
   * Deletes a file from the data directory.
   *
   * @param filename the filename to delete
   * @throws IOException if delete fails
   */
  public void deleteFile(String filename) throws IOException {
    File file = new File(DATA_DIR, filename);
    if (file.exists() && !file.delete()) {
      throw new IOException("Failed to delete file: " + filename);
    }
  }

  /**
   * Gets the ObjectMapper instance.
   *
   * @return the ObjectMapper
   */
  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
