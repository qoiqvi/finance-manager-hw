package com.finance.core.model;

import java.util.Objects;

/**
 * Represents a budget limit for a specific category.
 */
public class Budget {
  private final Category category;
  private double limit;
  private double spent;

  /**
   * Creates a new budget for a category.
   *
   * @param category the category this budget applies to
   * @param limit the maximum amount allowed for this category
   */
  public Budget(Category category, double limit) {
    if (category == null) {
      throw new IllegalArgumentException("Category cannot be null");
    }
    if (limit < 0) {
      throw new IllegalArgumentException("Budget limit cannot be negative");
    }
    this.category = category;
    this.limit = limit;
    this.spent = 0.0;
  }

  /**
   * Creates a budget with specified spent amount (for deserialization).
   *
   * @param category the category
   * @param limit the budget limit
   * @param spent the amount already spent
   */
  public Budget(Category category, double limit, double spent) {
    this(category, limit);
    this.spent = Math.max(0, spent);
  }

  public Category getCategory() {
    return category;
  }

  public double getLimit() {
    return limit;
  }

  public void setLimit(double limit) {
    if (limit < 0) {
      throw new IllegalArgumentException("Budget limit cannot be negative");
    }
    this.limit = limit;
  }

  public double getSpent() {
    return spent;
  }

  /**
   * Adds to the spent amount.
   *
   * @param amount the amount to add
   */
  public void addSpent(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot add negative amount to spent");
    }
    this.spent += amount;
  }

  /**
   * Gets the remaining budget amount.
   *
   * @return the remaining budget (may be negative if exceeded)
   */
  public double getRemainingBudget() {
    return limit - spent;
  }

  /**
   * Checks if the budget has been exceeded.
   *
   * @return true if spent amount exceeds the limit
   */
  public boolean isExceeded() {
    return spent > limit;
  }

  /**
   * Gets the percentage of budget used.
   *
   * @return percentage (0-100+)
   */
  public double getUsagePercentage() {
    if (limit == 0) {
      return spent > 0 ? 100.0 : 0.0;
    }
    return (spent / limit) * 100.0;
  }

  /**
   * Resets the spent amount to zero.
   */
  public void reset() {
    this.spent = 0.0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Budget budget = (Budget) o;
    return Objects.equals(category, budget.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category);
  }

  @Override
  public String toString() {
    return String.format(
        "%s: %.2f / %.2f (%.1f%%)", category.getName(), spent, limit, getUsagePercentage());
  }
}
