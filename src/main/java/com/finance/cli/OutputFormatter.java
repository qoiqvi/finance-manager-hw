package com.finance.cli;

import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Formats output for CLI display.
 */
public class OutputFormatter {
  private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US);
  private static final DecimalFormat CURRENCY_FORMAT;
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  static {
    SYMBOLS.setGroupingSeparator(',');
    SYMBOLS.setDecimalSeparator('.');
    CURRENCY_FORMAT = new DecimalFormat("#,##0.0", SYMBOLS);
  }

  /**
   * Formats a currency amount.
   *
   * @param amount the amount
   * @return formatted string (e.g., "63,000.0")
   */
  public String formatCurrency(double amount) {
    return CURRENCY_FORMAT.format(amount);
  }

  /**
   * Formats a transaction for display.
   *
   * @param transaction the transaction
   * @return formatted string
   */
  public String formatTransaction(Transaction transaction) {
    return String.format(
        "[%s] %s: %s (%s) - %s",
        transaction.getDate().format(DATE_TIME_FORMATTER),
        transaction.getType(),
        formatCurrency(transaction.getAmount()),
        transaction.getCategory().getName(),
        transaction.getDescription());
  }

  /**
   * Formats a list of transactions.
   *
   * @param transactions the transactions
   * @return formatted string
   */
  public String formatTransactions(List<Transaction> transactions) {
    if (transactions == null || transactions.isEmpty()) {
      return "No transactions found.";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Transactions:\n");
    sb.append("─".repeat(80)).append("\n");

    for (Transaction t : transactions) {
      sb.append(formatTransaction(t)).append("\n");
    }

    return sb.toString();
  }

  /**
   * Formats budget summary.
   *
   * @param budgets map of budgets
   * @return formatted string
   */
  public String formatBudgetSummary(Map<Category, Budget> budgets) {
    if (budgets == null || budgets.isEmpty()) {
      return "No budgets set.";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Budget Summary:\n");
    sb.append("─".repeat(80)).append("\n");
    sb.append(
            String.format(
                "%-25s %15s %15s %15s %10s\n",
                "Category", "Limit", "Spent", "Remaining", "Usage"))
        .append("─".repeat(80)).append("\n");

    for (Budget budget : budgets.values()) {
      String remaining = formatCurrency(budget.getRemainingBudget());
      String usage = String.format("%.1f%%", budget.getUsagePercentage());

      sb.append(
          String.format(
              "%-25s %15s %15s %15s %10s\n",
              budget.getCategory().getName(),
              formatCurrency(budget.getLimit()),
              formatCurrency(budget.getSpent()),
              remaining,
              usage));
    }

    return sb.toString();
  }

  /**
   * Formats income by category.
   *
   * @param incomeByCategory map of category to amount
   * @return formatted string
   */
  public String formatIncomeByCategory(Map<Category, Double> incomeByCategory) {
    if (incomeByCategory == null || incomeByCategory.isEmpty()) {
      return "No income recorded.";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Income by Category:\n");
    sb.append("─".repeat(50)).append("\n");

    for (Map.Entry<Category, Double> entry : incomeByCategory.entrySet()) {
      sb.append(
              String.format(
                  "%-30s: %s\n", entry.getKey().getName(), formatCurrency(entry.getValue())))
          .append("");
    }

    return sb.toString();
  }

  /**
   * Formats expenses by category.
   *
   * @param expensesByCategory map of category to amount
   * @return formatted string
   */
  public String formatExpensesByCategory(Map<Category, Double> expensesByCategory) {
    if (expensesByCategory == null || expensesByCategory.isEmpty()) {
      return "No expenses recorded.";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Expenses by Category:\n");
    sb.append("─".repeat(50)).append("\n");

    for (Map.Entry<Category, Double> entry : expensesByCategory.entrySet()) {
      sb.append(
              String.format(
                  "%-30s: %s\n", entry.getKey().getName(), formatCurrency(entry.getValue())))
          .append("");
    }

    return sb.toString();
  }

  /**
   * Formats complete statistics summary.
   *
   * @param totalIncome total income
   * @param totalExpenses total expenses
   * @param incomeByCategory income by category
   * @param budgets budget summary
   * @return formatted string
   */
  public String formatStatistics(
      double totalIncome,
      double totalExpenses,
      Map<Category, Double> incomeByCategory,
      Map<Category, Budget> budgets) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    sb.append("═".repeat(80)).append("\n");
    sb.append("                         FINANCIAL STATISTICS\n");
    sb.append("═".repeat(80)).append("\n\n");

    sb.append(String.format("Total Income:     %s\n", formatCurrency(totalIncome)));
    sb.append(String.format("Total Expenses:   %s\n", formatCurrency(totalExpenses)));
    sb.append(String.format("Net Balance:      %s\n\n", formatCurrency(totalIncome - totalExpenses)));

    sb.append(formatIncomeByCategory(incomeByCategory)).append("\n");
    sb.append(formatBudgetSummary(budgets)).append("\n");

    return sb.toString();
  }
}
