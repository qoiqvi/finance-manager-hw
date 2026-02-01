package com.finance.core.service;

import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.Wallet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for generating notifications and warnings.
 */
public class NotificationService {
  private static final double WARNING_THRESHOLD = 0.8; // 80%
  private final List<String> notifications;

  /** Creates a new NotificationService. */
  public NotificationService() {
    this.notifications = new ArrayList<>();
  }

  /**
   * Checks and generates notifications after an expense.
   *
   * @param wallet the wallet
   * @param category the expense category
   * @param amount the expense amount
   */
  public void checkAfterExpense(Wallet wallet, Category category, double amount) {
    Budget budget = wallet.getBudget(category);
    if (budget != null) {
      checkBudgetLimit(budget);
    }

    checkBalanceStatus(wallet);
  }

  /**
   * Checks if budget is exceeded or near limit.
   *
   * @param budget the budget to check
   */
  public void checkBudgetLimit(Budget budget) {
    if (budget == null) {
      return;
    }

    double usagePercentage = budget.getUsagePercentage();

    if (budget.isExceeded()) {
      String message =
          String.format(
              "⚠️  BUDGET EXCEEDED: Category '%s' - Spent: %.2f, Limit: %.2f, Over by: %.2f",
              budget.getCategory().getName(),
              budget.getSpent(),
              budget.getLimit(),
              budget.getSpent() - budget.getLimit());
      addNotification(message);
    } else if (usagePercentage >= WARNING_THRESHOLD * 100) {
      String message =
          String.format(
              "⚠️  BUDGET WARNING: Category '%s' is at %.1f%% (%.2f / %.2f)",
              budget.getCategory().getName(),
              usagePercentage,
              budget.getSpent(),
              budget.getLimit());
      addNotification(message);
    }
  }

  /**
   * Checks wallet balance status.
   *
   * @param wallet the wallet to check
   */
  public void checkBalanceStatus(Wallet wallet) {
    double balance = wallet.getBalance();

    if (balance < 0) {
      String message = String.format("⚠️  NEGATIVE BALANCE: Current balance is %.2f", balance);
      addNotification(message);
    }

    double totalIncome =
        wallet.getTransactions().stream()
            .filter(t -> t.getType() == com.finance.core.model.TransactionType.INCOME)
            .mapToDouble(com.finance.core.model.Transaction::getAmount)
            .sum();

    double totalExpenses =
        wallet.getTransactions().stream()
            .filter(t -> t.getType() == com.finance.core.model.TransactionType.EXPENSE)
            .mapToDouble(com.finance.core.model.Transaction::getAmount)
            .sum();

    if (totalExpenses > totalIncome && totalIncome > 0) {
      String message =
          String.format(
              "⚠️  EXPENSES EXCEED INCOME: Expenses: %.2f, Income: %.2f",
              totalExpenses, totalIncome);
      addNotification(message);
    }
  }

  /**
   * Adds a notification message.
   *
   * @param message the notification message
   */
  private void addNotification(String message) {
    notifications.add(message);
    System.out.println(message);
  }

  /**
   * Gets all notifications and clears the list.
   *
   * @return list of notification messages
   */
  public List<String> getNotificationsAndClear() {
    List<String> result = new ArrayList<>(notifications);
    notifications.clear();
    return result;
  }

  /**
   * Gets all notifications without clearing.
   *
   * @return list of notification messages
   */
  public List<String> getNotifications() {
    return new ArrayList<>(notifications);
  }

  /**
   * Clears all notifications.
   */
  public void clearNotifications() {
    notifications.clear();
  }
}
