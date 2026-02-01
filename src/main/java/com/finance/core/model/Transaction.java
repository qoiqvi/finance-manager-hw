package com.finance.core.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a financial transaction (income or expense).
 */
public class Transaction {
  private final String id;
  private final double amount;
  private final Category category;
  private final TransactionType type;
  private final LocalDateTime date;
  private final String description;

  /**
   * Creates a new transaction with generated ID and current timestamp.
   *
   * @param amount the transaction amount (must be positive)
   * @param category the transaction category
   * @param type the transaction type
   * @param description optional description
   */
  public Transaction(double amount, Category category, TransactionType type, String description) {
    this(UUID.randomUUID().toString(), amount, category, type, LocalDateTime.now(), description);
  }

  /**
   * Creates a new transaction with all fields specified (for deserialization).
   *
   * @param id the unique transaction ID
   * @param amount the transaction amount
   * @param category the transaction category
   * @param type the transaction type
   * @param date the transaction date/time
   * @param description optional description
   */
  public Transaction(
      String id,
      double amount,
      Category category,
      TransactionType type,
      LocalDateTime date,
      String description) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Transaction amount must be positive");
    }
    if (category == null) {
      throw new IllegalArgumentException("Category cannot be null");
    }
    if (type == null) {
      throw new IllegalArgumentException("Transaction type cannot be null");
    }

    this.id = id != null ? id : UUID.randomUUID().toString();
    this.amount = amount;
    this.category = category;
    this.type = type;
    this.date = date != null ? date : LocalDateTime.now();
    this.description = description != null ? description : "";
  }

  public String getId() {
    return id;
  }

  public double getAmount() {
    return amount;
  }

  public Category getCategory() {
    return category;
  }

  public TransactionType getType() {
    return type;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Transaction that = (Transaction) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format(
        "%s: %.2f (%s) - %s [%s]", type, amount, category.getName(), description, date);
  }
}
