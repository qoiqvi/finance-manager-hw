package com.finance.cli;

import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.User;
import com.finance.core.service.AuthService;
import com.finance.core.service.BudgetService;
import com.finance.core.service.StatisticsService;
import com.finance.core.service.TransactionService;
import com.finance.core.service.TransferService;
import com.finance.core.service.WalletService;
import com.finance.exception.AuthenticationException;
import com.finance.exception.InsufficientFundsException;
import com.finance.exception.ValidationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Handles CLI commands and coordinates services.
 */
public class CommandHandler {
  private final AuthService authService;
  private final WalletService walletService;
  private final TransactionService transactionService;
  private final BudgetService budgetService;
  private final StatisticsService statisticsService;
  private final TransferService transferService;
  private final InputValidator validator;
  private final OutputFormatter formatter;

  /**
   * Creates a new CommandHandler.
   */
  public CommandHandler(
      AuthService authService,
      WalletService walletService,
      TransactionService transactionService,
      BudgetService budgetService,
      StatisticsService statisticsService,
      TransferService transferService) {
    this.authService = authService;
    this.walletService = walletService;
    this.transactionService = transactionService;
    this.budgetService = budgetService;
    this.statisticsService = statisticsService;
    this.transferService = transferService;
    this.validator = new InputValidator();
    this.formatter = new OutputFormatter();
  }

  /**
   * Handles the register command.
   */
  public void handleRegister(String username, String password) {
    try {
      if (!validator.validateUsername(username)) {
        System.out.println("✗ Invalid username. Must be 3-20 alphanumeric characters.");
        return;
      }

      if (!validator.validatePassword(password)) {
        System.out.println("✗ Invalid password. Must be at least 6 characters.");
        return;
      }

      authService.register(username, password);
      System.out.println("✓ User registered successfully: " + username);
    } catch (AuthenticationException e) {
      System.out.println("✗ Registration failed: " + e.getMessage());
    }
  }

  /**
   * Handles the login command.
   */
  public void handleLogin(String username, String password) {
    try {
      authService.login(username, password);
      System.out.println("✓ Login successful. Welcome, " + username + "!");
      System.out.println(
          "Current balance: " + formatter.formatCurrency(authService.getCurrentUser().getWallet().getBalance()));
    } catch (AuthenticationException e) {
      System.out.println("✗ Login failed: " + e.getMessage());
    }
  }

  /**
   * Handles the logout command.
   */
  public void handleLogout() {
    try {
      if (!authService.isLoggedIn()) {
        System.out.println("✗ Not logged in.");
        return;
      }

      String username = authService.getCurrentUser().getUsername();
      authService.logout();
      System.out.println("✓ Logged out successfully. Goodbye, " + username + "!");
    } catch (IOException e) {
      System.out.println("✗ Error saving wallet: " + e.getMessage());
    }
  }

  /**
   * Handles the add-income command.
   */
  public void handleAddIncome(String amountStr, String categoryName, String description) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      double amount = validator.validateAmount(amountStr);
      String category = validator.validateCategory(categoryName);

      User user = authService.getCurrentUser();
      Category cat = new Category(category, TransactionType.INCOME);
      transactionService.addIncome(user.getWallet(), amount, cat, description != null ? description : "");

      System.out.println(
          String.format(
              "✓ Income added: %s to %s", formatter.formatCurrency(amount), category));
      System.out.println(
          "New balance: " + formatter.formatCurrency(user.getWallet().getBalance()));
    } catch (ValidationException e) {
      System.out.println("✗ Invalid input: " + e.getMessage());
    }
  }

  /**
   * Handles the add-expense command.
   */
  public void handleAddExpense(String amountStr, String categoryName, String description) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      double amount = validator.validateAmount(amountStr);
      String category = validator.validateCategory(categoryName);

      User user = authService.getCurrentUser();
      Category cat = new Category(category, TransactionType.EXPENSE);
      transactionService.addExpense(user.getWallet(), amount, cat, description != null ? description : "");

      System.out.println(
          String.format(
              "✓ Expense added: %s from %s", formatter.formatCurrency(amount), category));
      System.out.println(
          "New balance: " + formatter.formatCurrency(user.getWallet().getBalance()));
    } catch (ValidationException e) {
      System.out.println("✗ Invalid input: " + e.getMessage());
    }
  }

  /**
   * Handles the set-budget command.
   */
  public void handleSetBudget(String categoryName, String limitStr) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      double limit = validator.validateAmount(limitStr);
      String category = validator.validateCategory(categoryName);

      User user = authService.getCurrentUser();
      budgetService.setBudget(user.getWallet(), category, limit);

      System.out.println(
          String.format(
              "✓ Budget set for %s: %s", category, formatter.formatCurrency(limit)));
    } catch (ValidationException e) {
      System.out.println("✗ Invalid input: " + e.getMessage());
    }
  }

  /**
   * Handles the edit-budget command.
   */
  public void handleEditBudget(String categoryName, String newLimitStr) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      double newLimit = validator.validateAmount(newLimitStr);
      String category = validator.validateCategory(categoryName);

      User user = authService.getCurrentUser();
      budgetService.editBudget(user.getWallet(), category, newLimit);

      System.out.println(
          String.format(
              "✓ Budget updated for %s: %s", category, formatter.formatCurrency(newLimit)));
    } catch (ValidationException e) {
      System.out.println("✗ Invalid input: " + e.getMessage());
    }
  }

  /**
   * Handles the delete-budget command.
   */
  public void handleDeleteBudget(String categoryName) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      String category = validator.validateCategory(categoryName);
      User user = authService.getCurrentUser();
      budgetService.deleteBudget(user.getWallet(), category);

      System.out.println("✓ Budget deleted for category: " + category);
    } catch (ValidationException e) {
      System.out.println("✗ Invalid input: " + e.getMessage());
    }
  }

  /**
   * Handles the show-stats command.
   */
  public void handleShowStats() {
    if (!checkLoggedIn()) {
      return;
    }

    User user = authService.getCurrentUser();
    double totalIncome = statisticsService.getTotalIncome(user.getWallet());
    double totalExpenses = statisticsService.getTotalExpenses(user.getWallet());
    Map<Category, Double> incomeByCategory =
        statisticsService.getIncomeByCategory(user.getWallet());
    Map<Category, Budget> budgets = statisticsService.getBudgetSummary(user.getWallet());

    System.out.println(
        formatter.formatStatistics(totalIncome, totalExpenses, incomeByCategory, budgets));
  }

  /**
   * Handles the show-budget command.
   */
  public void handleShowBudget() {
    if (!checkLoggedIn()) {
      return;
    }

    User user = authService.getCurrentUser();
    Map<Category, Budget> budgets = statisticsService.getBudgetSummary(user.getWallet());
    System.out.println(formatter.formatBudgetSummary(budgets));
  }

  /**
   * Handles the transfer command.
   */
  public void handleTransfer(String recipientUsername, String amountStr, String description) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      double amount = validator.validateAmount(amountStr);
      User sender = authService.getCurrentUser();

      transferService.transfer(
          sender, recipientUsername, amount, description != null ? description : "");

      System.out.println(
          "New balance: " + formatter.formatCurrency(sender.getWallet().getBalance()));
    } catch (ValidationException | InsufficientFundsException | IllegalArgumentException e) {
      System.out.println("✗ Transfer failed: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("✗ Error during transfer: " + e.getMessage());
    }
  }

  /**
   * Handles the stats-by-category command.
   */
  public void handleStatsByCategory(String[] categoryNames) {
    if (!checkLoggedIn()) {
      return;
    }

    User user = authService.getCurrentUser();
    List<String> categories = Arrays.asList(categoryNames);

    List<String> missing = statisticsService.findMissingCategories(user.getWallet(), categories);
    if (!missing.isEmpty()) {
      System.out.println("⚠️  Warning: Categories not found: " + String.join(", ", missing));
    }

    double income = statisticsService.getIncomeByCategories(user.getWallet(), categories);
    double expenses = statisticsService.getExpensesByCategories(user.getWallet(), categories);

    System.out.println("\nStatistics for categories: " + String.join(", ", categories));
    System.out.println("Total Income:   " + formatter.formatCurrency(income));
    System.out.println("Total Expenses: " + formatter.formatCurrency(expenses));
  }

  /**
   * Handles the stats-by-period command.
   */
  public void handleStatsByPeriod(String startDateStr, String endDateStr) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      LocalDateTime startDate = validator.validateDate(startDateStr);
      LocalDateTime endDate = validator.validateDate(endDateStr).plusDays(1).minusSeconds(1);

      User user = authService.getCurrentUser();
      List<Transaction> transactions =
          statisticsService.getTransactionsByPeriod(user.getWallet(), startDate, endDate);

      System.out.println(
          String.format(
              "\nTransactions from %s to %s:", startDateStr, endDateStr));
      System.out.println(formatter.formatTransactions(transactions));
    } catch (ValidationException e) {
      System.out.println("✗ Invalid date: " + e.getMessage());
    }
  }

  /**
   * Handles the export-csv command.
   */
  public void handleExportCsv(String filepath) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      String path = validator.validateFilepath(filepath);
      User user = authService.getCurrentUser();

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
        writer.write("Date,Type,Category,Amount,Description\n");

        for (Transaction t : user.getWallet().getTransactions()) {
          writer.write(
              String.format(
                  "%s,%s,%s,%.2f,%s\n",
                  t.getDate().toString(),
                  t.getType(),
                  t.getCategory().getName(),
                  t.getAmount(),
                  t.getDescription().replace(",", ";")));
        }
      }

      System.out.println("✓ Transactions exported to: " + path);
    } catch (ValidationException e) {
      System.out.println("✗ Invalid filepath: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("✗ Export failed: " + e.getMessage());
    }
  }

  /**
   * Handles the export-json command.
   */
  public void handleExportJson(String filepath) {
    if (!checkLoggedIn()) {
      return;
    }

    try {
      String path = validator.validateFilepath(filepath);
      User user = authService.getCurrentUser();

      walletService.saveWallet(user.getWallet());

      System.out.println("✓ Wallet exported (saved) successfully");
      System.out.println("Note: Wallet is automatically saved to data/" + user.getUsername() + "_wallet.json");
    } catch (ValidationException e) {
      System.out.println("✗ Invalid filepath: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("✗ Export failed: " + e.getMessage());
    }
  }

  /**
   * Handles the help command.
   */
  public void handleHelp() {
    System.out.println("\n═══════════════════════ AVAILABLE COMMANDS ═══════════════════════");
    System.out.println("\nAuthentication:");
    System.out.println("  register <username> <password>       - Register a new user");
    System.out.println("  login <username> <password>          - Login to your account");
    System.out.println("  logout                               - Logout and save wallet");
    System.out.println("\nTransactions:");
    System.out.println("  add-income <amount> <category> [description]");
    System.out.println("  add-expense <amount> <category> [description]");
    System.out.println("\nBudget Management:");
    System.out.println("  set-budget <category> <limit>        - Set budget for a category");
    System.out.println("  edit-budget <category> <new-limit>   - Edit existing budget");
    System.out.println("  delete-budget <category>             - Delete a budget");
    System.out.println("  show-budget                          - Show all budgets");
    System.out.println("\nStatistics:");
    System.out.println("  show-stats                           - Show complete statistics");
    System.out.println("  stats-by-category <cat1> <cat2> ...  - Stats for specific categories");
    System.out.println("  stats-by-period <start> <end>        - Stats for date range (yyyy-MM-dd)");
    System.out.println("\nTransfers:");
    System.out.println("  transfer <recipient> <amount> [description]");
    System.out.println("\nExport:");
    System.out.println("  export-csv <filepath>                - Export transactions to CSV");
    System.out.println("  export-json <filepath>               - Save wallet to JSON");
    System.out.println("\nOther:");
    System.out.println("  help                                 - Show this help message");
    System.out.println("  exit                                 - Exit the application");
    System.out.println("═══════════════════════════════════════════════════════════════════\n");
  }

  /**
   * Checks if user is logged in.
   */
  private boolean checkLoggedIn() {
    if (!authService.isLoggedIn()) {
      System.out.println("✗ Please login first.");
      return false;
    }
    return true;
  }
}
