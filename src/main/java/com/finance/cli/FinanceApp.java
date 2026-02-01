package com.finance.cli;

import com.finance.core.service.AuthService;
import com.finance.core.service.BudgetService;
import com.finance.core.service.NotificationService;
import com.finance.core.service.StatisticsService;
import com.finance.core.service.TransactionService;
import com.finance.core.service.TransferService;
import com.finance.core.service.WalletService;
import com.finance.infrastructure.repository.InMemoryUserRepository;
import com.finance.infrastructure.repository.JsonWalletRepository;
import com.finance.infrastructure.repository.UserRepository;
import com.finance.infrastructure.repository.WalletRepository;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main CLI application for Personal Finance Manager.
 */
public class FinanceApp {
  private final CommandHandler commandHandler;
  private final AuthService authService;
  private final Scanner scanner;
  private boolean running;

  /** Creates a new FinanceApp. */
  public FinanceApp() {
    UserRepository userRepository = new InMemoryUserRepository();
    WalletRepository walletRepository = new JsonWalletRepository();

    this.authService = new AuthService(userRepository, walletRepository);
    WalletService walletService = new WalletService(walletRepository);
    NotificationService notificationService = new NotificationService();
    TransactionService transactionService = new TransactionService(notificationService);
    BudgetService budgetService = new BudgetService();
    StatisticsService statisticsService = new StatisticsService();
    TransferService transferService = new TransferService(userRepository, walletRepository);

    this.commandHandler =
        new CommandHandler(
            authService,
            walletService,
            transactionService,
            budgetService,
            statisticsService,
            transferService);

    this.scanner = new Scanner(System.in);
    this.running = true;
  }

  /** Starts the application. */
  public void start() {
    printWelcome();

    while (running) {
      try {
        System.out.print("\n> ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
          continue;
        }

        processCommand(input);

      } catch (Exception e) {
        System.out.println("✗ Error: " + e.getMessage());
      }
    }

    shutdown();
  }

  /** Processes a command. */
  private void processCommand(String input) {
    String[] parts = input.split("\\s+", 2);
    String command = parts[0].toLowerCase();

    switch (command) {
      case "register":
        handleRegister(parts.length > 1 ? parts[1] : "");
        break;
      case "login":
        handleLogin(parts.length > 1 ? parts[1] : "");
        break;
      case "logout":
        commandHandler.handleLogout();
        break;
      case "add-income":
        handleAddIncome(parts.length > 1 ? parts[1] : "");
        break;
      case "add-expense":
        handleAddExpense(parts.length > 1 ? parts[1] : "");
        break;
      case "set-budget":
        handleSetBudget(parts.length > 1 ? parts[1] : "");
        break;
      case "edit-budget":
        handleEditBudget(parts.length > 1 ? parts[1] : "");
        break;
      case "delete-budget":
        handleDeleteBudget(parts.length > 1 ? parts[1] : "");
        break;
      case "show-stats":
        commandHandler.handleShowStats();
        break;
      case "show-budget":
        commandHandler.handleShowBudget();
        break;
      case "transfer":
        handleTransfer(parts.length > 1 ? parts[1] : "");
        break;
      case "stats-by-category":
        handleStatsByCategory(parts.length > 1 ? parts[1] : "");
        break;
      case "stats-by-period":
        handleStatsByPeriod(parts.length > 1 ? parts[1] : "");
        break;
      case "export-csv":
        handleExportCsv(parts.length > 1 ? parts[1] : "");
        break;
      case "export-json":
        handleExportJson(parts.length > 1 ? parts[1] : "");
        break;
      case "help":
        commandHandler.handleHelp();
        break;
      case "exit":
      case "quit":
        handleExit();
        break;
      default:
        System.out.println("✗ Unknown command. Type 'help' for available commands.");
    }
  }

  private void handleRegister(String args) {
    String[] parts = args.split("\\s+");
    if (parts.length < 2) {
      System.out.println("✗ Usage: register <username> <password>");
      return;
    }
    commandHandler.handleRegister(parts[0], parts[1]);
  }

  private void handleLogin(String args) {
    String[] parts = args.split("\\s+");
    if (parts.length < 2) {
      System.out.println("✗ Usage: login <username> <password>");
      return;
    }
    commandHandler.handleLogin(parts[0], parts[1]);
  }

  private void handleAddIncome(String args) {
    String[] parts = args.split("\\s+", 3);
    if (parts.length < 2) {
      System.out.println("✗ Usage: add-income <amount> <category> [description]");
      return;
    }
    String description = parts.length > 2 ? parts[2] : "";
    commandHandler.handleAddIncome(parts[0], parts[1], description);
  }

  private void handleAddExpense(String args) {
    String[] parts = args.split("\\s+", 3);
    if (parts.length < 2) {
      System.out.println("✗ Usage: add-expense <amount> <category> [description]");
      return;
    }
    String description = parts.length > 2 ? parts[2] : "";
    commandHandler.handleAddExpense(parts[0], parts[1], description);
  }

  private void handleSetBudget(String args) {
    String[] parts = args.split("\\s+");
    if (parts.length < 2) {
      System.out.println("✗ Usage: set-budget <category> <limit>");
      return;
    }
    commandHandler.handleSetBudget(parts[0], parts[1]);
  }

  private void handleEditBudget(String args) {
    String[] parts = args.split("\\s+");
    if (parts.length < 2) {
      System.out.println("✗ Usage: edit-budget <category> <new-limit>");
      return;
    }
    commandHandler.handleEditBudget(parts[0], parts[1]);
  }

  private void handleDeleteBudget(String args) {
    if (args.trim().isEmpty()) {
      System.out.println("✗ Usage: delete-budget <category>");
      return;
    }
    commandHandler.handleDeleteBudget(args.trim());
  }

  private void handleTransfer(String args) {
    String[] parts = args.split("\\s+", 3);
    if (parts.length < 2) {
      System.out.println("✗ Usage: transfer <recipient> <amount> [description]");
      return;
    }
    String description = parts.length > 2 ? parts[2] : "";
    commandHandler.handleTransfer(parts[0], parts[1], description);
  }

  private void handleStatsByCategory(String args) {
    if (args.trim().isEmpty()) {
      System.out.println("✗ Usage: stats-by-category <category1> <category2> ...");
      return;
    }
    String[] categories = args.trim().split("\\s+");
    commandHandler.handleStatsByCategory(categories);
  }

  private void handleStatsByPeriod(String args) {
    String[] parts = args.split("\\s+");
    if (parts.length < 2) {
      System.out.println("✗ Usage: stats-by-period <start-date> <end-date> (yyyy-MM-dd)");
      return;
    }
    commandHandler.handleStatsByPeriod(parts[0], parts[1]);
  }

  private void handleExportCsv(String args) {
    if (args.trim().isEmpty()) {
      System.out.println("✗ Usage: export-csv <filepath>");
      return;
    }
    commandHandler.handleExportCsv(args.trim());
  }

  private void handleExportJson(String args) {
    if (args.trim().isEmpty()) {
      System.out.println("✗ Usage: export-json <filepath>");
      return;
    }
    commandHandler.handleExportJson(args.trim());
  }

  private void handleExit() {
    System.out.println("\nExiting...");
    running = false;
  }

  private void printWelcome() {
    System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
    System.out.println("║                                                           ║");
    System.out.println("║          Personal Finance Management System               ║");
    System.out.println("║                                                           ║");
    System.out.println("╚═══════════════════════════════════════════════════════════╝");
    System.out.println("\nWelcome! Type 'help' to see available commands.");
    System.out.println("Type 'register <username> <password>' to create an account.");
    System.out.println("Type 'login <username> <password>' to access your wallet.\n");
  }

  private void shutdown() {
    try {
      if (authService.isLoggedIn()) {
        authService.logout();
      }
    } catch (IOException e) {
      System.out.println("✗ Error saving wallet: " + e.getMessage());
    }

    scanner.close();
    System.out.println("\nThank you for using Personal Finance Manager. Goodbye!");
  }
}
