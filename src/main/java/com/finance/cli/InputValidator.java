package com.finance.cli;

import com.finance.exception.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validates user input from CLI.
 */
public class InputValidator {
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Validates and parses an amount string.
   *
   * @param amountStr the amount string
   * @return the parsed amount
   * @throws ValidationException if invalid
   */
  public double validateAmount(String amountStr) {
    if (amountStr == null || amountStr.trim().isEmpty()) {
      throw new ValidationException("Amount cannot be empty");
    }

    try {
      double amount = Double.parseDouble(amountStr.trim());
      if (amount <= 0) {
        throw new ValidationException("Amount must be positive");
      }
      return amount;
    } catch (NumberFormatException e) {
      throw new ValidationException("Invalid amount format: " + amountStr);
    }
  }

  /**
   * Validates a category name.
   *
   * @param category the category name
   * @return the trimmed category name
   * @throws ValidationException if invalid
   */
  public String validateCategory(String category) {
    if (category == null || category.trim().isEmpty()) {
      throw new ValidationException("Category cannot be empty");
    }
    return category.trim();
  }

  /**
   * Validates a username.
   *
   * @param username the username
   * @return true if valid
   */
  public boolean validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return false;
    }
    String trimmed = username.trim();
    return trimmed.length() >= 3
        && trimmed.length() <= 20
        && trimmed.matches("[a-zA-Z0-9]+");
  }

  /**
   * Validates a password.
   *
   * @param password the password
   * @return true if valid
   */
  public boolean validatePassword(String password) {
    return password != null && password.length() >= 6;
  }

  /**
   * Validates and parses a date string.
   *
   * @param dateStr the date string in yyyy-MM-dd format
   * @return the parsed LocalDateTime
   * @throws ValidationException if invalid
   */
  public LocalDateTime validateDate(String dateStr) {
    if (dateStr == null || dateStr.trim().isEmpty()) {
      throw new ValidationException("Date cannot be empty");
    }

    try {
      return LocalDateTime.parse(dateStr.trim() + "T00:00:00");
    } catch (DateTimeParseException e) {
      throw new ValidationException("Invalid date format. Use: yyyy-MM-dd");
    }
  }

  /**
   * Validates a filepath.
   *
   * @param filepath the filepath
   * @return the trimmed filepath
   * @throws ValidationException if invalid
   */
  public String validateFilepath(String filepath) {
    if (filepath == null || filepath.trim().isEmpty()) {
      throw new ValidationException("Filepath cannot be empty");
    }
    return filepath.trim();
  }
}
