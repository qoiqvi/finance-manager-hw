package com.finance.cli;

import static org.junit.jupiter.api.Assertions.*;

import com.finance.exception.ValidationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InputValidatorTest {
  private InputValidator validator;

  @BeforeEach
  void setUp() {
    validator = new InputValidator();
  }

  @Test
  void testValidateAmountSuccess() {
    assertEquals(100.0, validator.validateAmount("100"), 0.01);
    assertEquals(1234.56, validator.validateAmount("1234.56"), 0.01);
  }

  @Test
  void testValidateAmountInvalid() {
    assertThrows(ValidationException.class, () -> validator.validateAmount(""));
    assertThrows(ValidationException.class, () -> validator.validateAmount("abc"));
    assertThrows(ValidationException.class, () -> validator.validateAmount("0"));
    assertThrows(ValidationException.class, () -> validator.validateAmount("-100"));
  }

  @Test
  void testValidateCategorySuccess() {
    assertEquals("Food", validator.validateCategory("Food"));
    assertEquals("Food", validator.validateCategory("  Food  "));
  }

  @Test
  void testValidateCategoryInvalid() {
    assertThrows(ValidationException.class, () -> validator.validateCategory(""));
    assertThrows(ValidationException.class, () -> validator.validateCategory("   "));
    assertThrows(ValidationException.class, () -> validator.validateCategory(null));
  }

  @Test
  void testValidateUsernameSuccess() {
    assertTrue(validator.validateUsername("john123"));
    assertTrue(validator.validateUsername("abc"));
    assertTrue(validator.validateUsername("user12345678901234"));
  }

  @Test
  void testValidateUsernameInvalid() {
    assertFalse(validator.validateUsername("ab"));
    assertFalse(validator.validateUsername("this_username_is_way_too_long"));
    assertFalse(validator.validateUsername("user@123"));
    assertFalse(validator.validateUsername(""));
    assertFalse(validator.validateUsername(null));
  }

  @Test
  void testValidatePasswordSuccess() {
    assertTrue(validator.validatePassword("123456"));
    assertTrue(validator.validatePassword("password123"));
  }

  @Test
  void testValidatePasswordInvalid() {
    assertFalse(validator.validatePassword("12345"));
    assertFalse(validator.validatePassword(""));
    assertFalse(validator.validatePassword(null));
  }

  @Test
  void testValidateDateSuccess() {
    LocalDateTime date = validator.validateDate("2024-01-15");
    assertNotNull(date);
    assertEquals(15, date.getDayOfMonth());
    assertEquals(1, date.getMonthValue());
    assertEquals(2024, date.getYear());
  }

  @Test
  void testValidateDateInvalid() {
    assertThrows(ValidationException.class, () -> validator.validateDate(""));
    assertThrows(ValidationException.class, () -> validator.validateDate("2024-13-01"));
    assertThrows(ValidationException.class, () -> validator.validateDate("invalid-date"));
  }

  @Test
  void testValidateFilepathSuccess() {
    assertEquals("output.csv", validator.validateFilepath("output.csv"));
    assertEquals("/path/to/file.json", validator.validateFilepath("/path/to/file.json"));
  }

  @Test
  void testValidateFilepathInvalid() {
    assertThrows(ValidationException.class, () -> validator.validateFilepath(""));
    assertThrows(ValidationException.class, () -> validator.validateFilepath("   "));
    assertThrows(ValidationException.class, () -> validator.validateFilepath(null));
  }
}
