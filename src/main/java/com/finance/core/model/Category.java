package com.finance.core.model;

import java.util.Objects;

/**
 * Represents a category for income or expense transactions.
 */
public class Category {
  private final String name;
  private final TransactionType type;

  /**
   * Creates a new category.
   *
   * @param name the category name
   * @param type the transaction type (INCOME or EXPENSE)
   */
  public Category(String name, TransactionType type) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Category name cannot be empty");
    }
    this.name = name.trim();
    this.type = type != null ? type : TransactionType.EXPENSE;
  }

  public String getName() {
    return name;
  }

  public TransactionType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Category category = (Category) o;
    return Objects.equals(name, category.name) && type == category.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }

  @Override
  public String toString() {
    return name;
  }
}
