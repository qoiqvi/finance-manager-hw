package com.finance.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a user's wallet containing balance, transactions, and budgets.
 */
public class Wallet {
  private final String userId;
  private double balance;
  private final List<Transaction> transactions;
  private final Map<Category, Budget> budgets;

  /**
   * Creates a new wallet for a user.
   *
   * @param userId the user ID this wallet belongs to
   */
  public Wallet(String userId) {
    if (userId == null || userId.trim().isEmpty()) {
      throw new IllegalArgumentException("User ID cannot be empty");
    }
    this.userId = userId;
    this.balance = 0.0;
    this.transactions = new ArrayList<>();
    this.budgets = new HashMap<>();
  }

  /**
   * Creates a wallet with existing data (for deserialization).
   *
   * @param userId the user ID
   * @param balance the current balance
   * @param transactions list of transactions
   * @param budgets map of budgets by category
   */
  public Wallet(
      String userId, double balance, List<Transaction> transactions, Map<Category, Budget> budgets) {
    this.userId = userId;
    this.balance = balance;
    this.transactions = new ArrayList<>(transactions != null ? transactions : new ArrayList<>());
    this.budgets = new HashMap<>(budgets != null ? budgets : new HashMap<>());
  }

  public String getUserId() {
    return userId;
  }

  public double getBalance() {
    return balance;
  }

  public List<Transaction> getTransactions() {
    return new ArrayList<>(transactions);
  }

  public Map<Category, Budget> getBudgets() {
    return new HashMap<>(budgets);
  }

  /**
   * Adds a transaction to the wallet and updates balance.
   *
   * @param transaction the transaction to add
   */
  public void addTransaction(Transaction transaction) {
    if (transaction == null) {
      throw new IllegalArgumentException("Transaction cannot be null");
    }

    transactions.add(transaction);

    if (transaction.getType() == TransactionType.INCOME) {
      balance += transaction.getAmount();
    } else {
      balance -= transaction.getAmount();
      updateBudgetSpent(transaction.getCategory(), transaction.getAmount());
    }
  }

  /**
   * Sets or updates a budget for a category.
   *
   * @param category the category
   * @param limit the budget limit
   */
  public void setBudget(Category category, double limit) {
    if (category == null) {
      throw new IllegalArgumentException("Category cannot be null");
    }

    Budget existingBudget = budgets.get(category);
    if (existingBudget != null) {
      existingBudget.setLimit(limit);
    } else {
      budgets.put(category, new Budget(category, limit));
    }
  }

  /**
   * Gets the budget for a specific category.
   *
   * @param category the category
   * @return the budget or null if not set
   */
  public Budget getBudget(Category category) {
    return budgets.get(category);
  }

  /**
   * Removes a budget for a category.
   *
   * @param category the category
   */
  public void removeBudget(Category category) {
    budgets.remove(category);
  }

  /**
   * Updates the spent amount for a category's budget.
   *
   * @param category the category
   * @param amount the amount to add to spent
   */
  private void updateBudgetSpent(Category category, double amount) {
    Budget budget = budgets.get(category);
    if (budget != null) {
      budget.addSpent(amount);
    }
  }

  /**
   * Recalculates balance from all transactions.
   *
   * @return the calculated balance
   */
  public double calculateBalance() {
    double calculated = 0.0;
    for (Transaction t : transactions) {
      if (t.getType() == TransactionType.INCOME) {
        calculated += t.getAmount();
      } else {
        calculated -= t.getAmount();
      }
    }
    this.balance = calculated;
    return calculated;
  }

  /**
   * Gets transactions filtered by type.
   *
   * @param type the transaction type to filter by
   * @return list of matching transactions
   */
  public List<Transaction> getTransactionsByType(TransactionType type) {
    return transactions.stream().filter(t -> t.getType() == type).collect(Collectors.toList());
  }

  /**
   * Gets transactions for a specific category.
   *
   * @param category the category to filter by
   * @return list of matching transactions
   */
  public List<Transaction> getTransactionsByCategory(Category category) {
    return transactions.stream()
        .filter(t -> t.getCategory().equals(category))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return String.format(
        "Wallet[user=%s, balance=%.2f, transactions=%d, budgets=%d]",
        userId, balance, transactions.size(), budgets.size());
  }
}
