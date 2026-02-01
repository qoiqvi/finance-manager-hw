package com.finance.core.service;

import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;
import com.finance.exception.ValidationException;

/**
 * Service for managing budgets.
 */
public class BudgetService {

  /**
   * Sets a budget for a category in the wallet.
   *
   * @param wallet the wallet
   * @param categoryName the category name
   * @param limit the budget limit
   * @return the created or updated budget
   */
  public Budget setBudget(Wallet wallet, String categoryName, double limit) {
    if (wallet == null) {
      throw new IllegalArgumentException("Wallet cannot be null");
    }

    if (categoryName == null || categoryName.trim().isEmpty()) {
      throw new ValidationException("Category name cannot be empty");
    }

    if (limit < 0) {
      throw new ValidationException("Budget limit cannot be negative");
    }

    Category category = new Category(categoryName, TransactionType.EXPENSE);
    wallet.setBudget(category, limit);
    return wallet.getBudget(category);
  }

  /**
   * Gets a budget for a category.
   *
   * @param wallet the wallet
   * @param categoryName the category name
   * @return the budget or null if not set
   */
  public Budget getBudget(Wallet wallet, String categoryName) {
    if (wallet == null) {
      throw new IllegalArgumentException("Wallet cannot be null");
    }

    if (categoryName == null || categoryName.trim().isEmpty()) {
      return null;
    }

    Category category = new Category(categoryName, TransactionType.EXPENSE);
    return wallet.getBudget(category);
  }

  /**
   * Edits an existing budget.
   *
   * @param wallet the wallet
   * @param categoryName the category name
   * @param newLimit the new budget limit
   * @return the updated budget
   */
  public Budget editBudget(Wallet wallet, String categoryName, double newLimit) {
    return setBudget(wallet, categoryName, newLimit);
  }

  /**
   * Deletes a budget for a category.
   *
   * @param wallet the wallet
   * @param categoryName the category name
   */
  public void deleteBudget(Wallet wallet, String categoryName) {
    if (wallet == null) {
      throw new IllegalArgumentException("Wallet cannot be null");
    }

    if (categoryName != null && !categoryName.trim().isEmpty()) {
      Category category = new Category(categoryName, TransactionType.EXPENSE);
      wallet.removeBudget(category);
    }
  }

  /**
   * Checks if a budget is exceeded.
   *
   * @param budget the budget to check
   * @return true if exceeded
   */
  public boolean isBudgetExceeded(Budget budget) {
    return budget != null && budget.isExceeded();
  }

  /**
   * Gets the remaining budget amount.
   *
   * @param budget the budget
   * @return remaining amount (may be negative)
   */
  public double getRemainingBudget(Budget budget) {
    return budget != null ? budget.getRemainingBudget() : 0.0;
  }
}
