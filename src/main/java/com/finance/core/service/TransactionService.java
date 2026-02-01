package com.finance.core.service;

import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;
import com.finance.exception.ValidationException;

/**
 * Service for managing transactions.
 */
public class TransactionService {
  private final NotificationService notificationService;

  /**
   * Creates a new TransactionService.
   *
   * @param notificationService the notification service
   */
  public TransactionService(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  /**
   * Adds an income transaction to the wallet.
   *
   * @param wallet the wallet to add to
   * @param amount the income amount
   * @param category the income category
   * @param description optional description
   * @return the created transaction
   */
  public Transaction addIncome(
      Wallet wallet, double amount, Category category, String description) {
    validateTransaction(amount, category);

    if (category.getType() != TransactionType.INCOME) {
      category = new Category(category.getName(), TransactionType.INCOME);
    }

    Transaction transaction = new Transaction(amount, category, TransactionType.INCOME, description);
    wallet.addTransaction(transaction);

    return transaction;
  }

  /**
   * Adds an expense transaction to the wallet.
   *
   * @param wallet the wallet to add to
   * @param amount the expense amount
   * @param category the expense category
   * @param description optional description
   * @return the created transaction
   */
  public Transaction addExpense(
      Wallet wallet, double amount, Category category, String description) {
    validateTransaction(amount, category);

    if (category.getType() != TransactionType.EXPENSE) {
      category = new Category(category.getName(), TransactionType.EXPENSE);
    }

    Transaction transaction =
        new Transaction(amount, category, TransactionType.EXPENSE, description);
    wallet.addTransaction(transaction);

    notificationService.checkAfterExpense(wallet, category, amount);

    return transaction;
  }

  /**
   * Validates a transaction.
   *
   * @param amount the transaction amount
   * @param category the category
   * @throws ValidationException if validation fails
   */
  private void validateTransaction(double amount, Category category) {
    if (amount <= 0) {
      throw new ValidationException("Amount must be positive");
    }

    if (category == null) {
      throw new ValidationException("Category cannot be null");
    }

    if (category.getName() == null || category.getName().trim().isEmpty()) {
      throw new ValidationException("Category name cannot be empty");
    }
  }
}
