package com.finance.core.service;

import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for calculating financial statistics.
 */
public class StatisticsService {

  /**
   * Gets total income from all transactions.
   *
   * @param wallet the wallet
   * @return total income amount
   */
  public double getTotalIncome(Wallet wallet) {
    return wallet.getTransactionsByType(TransactionType.INCOME).stream()
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  /**
   * Gets total expenses from all transactions.
   *
   * @param wallet the wallet
   * @return total expenses amount
   */
  public double getTotalExpenses(Wallet wallet) {
    return wallet.getTransactionsByType(TransactionType.EXPENSE).stream()
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  /**
   * Gets income grouped by category.
   *
   * @param wallet the wallet
   * @return map of category to total income
   */
  public Map<Category, Double> getIncomeByCategory(Wallet wallet) {
    Map<Category, Double> incomeByCategory = new HashMap<>();

    for (Transaction transaction : wallet.getTransactionsByType(TransactionType.INCOME)) {
      Category category = transaction.getCategory();
      incomeByCategory.merge(category, transaction.getAmount(), Double::sum);
    }

    return incomeByCategory;
  }

  /**
   * Gets expenses grouped by category.
   *
   * @param wallet the wallet
   * @return map of category to total expenses
   */
  public Map<Category, Double> getExpensesByCategory(Wallet wallet) {
    Map<Category, Double> expensesByCategory = new HashMap<>();

    for (Transaction transaction : wallet.getTransactionsByType(TransactionType.EXPENSE)) {
      Category category = transaction.getCategory();
      expensesByCategory.merge(category, transaction.getAmount(), Double::sum);
    }

    return expensesByCategory;
  }

  /**
   * Gets all budgets with their current status.
   *
   * @param wallet the wallet
   * @return map of budgets
   */
  public Map<Category, Budget> getBudgetSummary(Wallet wallet) {
    return new HashMap<>(wallet.getBudgets());
  }

  /**
   * Gets income for specific categories.
   *
   * @param wallet the wallet
   * @param categoryNames list of category names
   * @return total income for specified categories
   */
  public double getIncomeByCategories(Wallet wallet, List<String> categoryNames) {
    if (categoryNames == null || categoryNames.isEmpty()) {
      return 0.0;
    }

    List<String> normalizedNames =
        categoryNames.stream().map(String::trim).collect(Collectors.toList());

    return wallet.getTransactionsByType(TransactionType.INCOME).stream()
        .filter(t -> normalizedNames.contains(t.getCategory().getName()))
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  /**
   * Gets expenses for specific categories.
   *
   * @param wallet the wallet
   * @param categoryNames list of category names
   * @return total expenses for specified categories
   */
  public double getExpensesByCategories(Wallet wallet, List<String> categoryNames) {
    if (categoryNames == null || categoryNames.isEmpty()) {
      return 0.0;
    }

    List<String> normalizedNames =
        categoryNames.stream().map(String::trim).collect(Collectors.toList());

    return wallet.getTransactionsByType(TransactionType.EXPENSE).stream()
        .filter(t -> normalizedNames.contains(t.getCategory().getName()))
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  /**
   * Gets transactions within a date range.
   *
   * @param wallet the wallet
   * @param startDate the start date (inclusive)
   * @param endDate the end date (inclusive)
   * @return list of transactions in the period
   */
  public List<Transaction> getTransactionsByPeriod(
      Wallet wallet, LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null || endDate == null) {
      return wallet.getTransactions();
    }

    return wallet.getTransactions().stream()
        .filter(
            t ->
                !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
        .collect(Collectors.toList());
  }

  /**
   * Checks if there are any category names not found in wallet.
   *
   * @param wallet the wallet
   * @param categoryNames category names to check
   * @return list of category names not found
   */
  public List<String> findMissingCategories(Wallet wallet, List<String> categoryNames) {
    if (categoryNames == null) {
      return List.of();
    }

    List<String> existingCategories =
        wallet.getTransactions().stream()
            .map(t -> t.getCategory().getName())
            .distinct()
            .collect(Collectors.toList());

    return categoryNames.stream()
        .filter(name -> !existingCategories.contains(name.trim()))
        .collect(Collectors.toList());
  }
}
