package com.finance.core.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatisticsServiceTest {
  private StatisticsService statisticsService;
  private Wallet wallet;

  @BeforeEach
  void setUp() {
    statisticsService = new StatisticsService();
    wallet = new Wallet("testuser");
  }

  @Test
  void testGetTotalIncome() {
    Category salary = new Category("Salary", TransactionType.INCOME);
    wallet.addTransaction(new Transaction(5000.0, salary, TransactionType.INCOME, ""));
    wallet.addTransaction(new Transaction(3000.0, salary, TransactionType.INCOME, ""));

    double total = statisticsService.getTotalIncome(wallet);
    assertEquals(8000.0, total, 0.01);
  }

  @Test
  void testGetTotalExpenses() {
    Category food = new Category("Food", TransactionType.EXPENSE);
    wallet.addTransaction(new Transaction(500.0, food, TransactionType.EXPENSE, ""));
    wallet.addTransaction(new Transaction(300.0, food, TransactionType.EXPENSE, ""));

    double total = statisticsService.getTotalExpenses(wallet);
    assertEquals(800.0, total, 0.01);
  }

  @Test
  void testGetIncomeByCategory() {
    Category salary = new Category("Salary", TransactionType.INCOME);
    Category bonus = new Category("Bonus", TransactionType.INCOME);

    wallet.addTransaction(new Transaction(5000.0, salary, TransactionType.INCOME, ""));
    wallet.addTransaction(new Transaction(3000.0, salary, TransactionType.INCOME, ""));
    wallet.addTransaction(new Transaction(1000.0, bonus, TransactionType.INCOME, ""));

    Map<Category, Double> incomeByCategory = statisticsService.getIncomeByCategory(wallet);

    assertEquals(8000.0, incomeByCategory.get(salary), 0.01);
    assertEquals(1000.0, incomeByCategory.get(bonus), 0.01);
  }

  @Test
  void testGetExpensesByCategory() {
    Category food = new Category("Food", TransactionType.EXPENSE);
    Category transport = new Category("Transport", TransactionType.EXPENSE);

    wallet.addTransaction(new Transaction(500.0, food, TransactionType.EXPENSE, ""));
    wallet.addTransaction(new Transaction(300.0, food, TransactionType.EXPENSE, ""));
    wallet.addTransaction(new Transaction(200.0, transport, TransactionType.EXPENSE, ""));

    Map<Category, Double> expensesByCategory = statisticsService.getExpensesByCategory(wallet);

    assertEquals(800.0, expensesByCategory.get(food), 0.01);
    assertEquals(200.0, expensesByCategory.get(transport), 0.01);
  }

  @Test
  void testGetIncomeByCategories() {
    Category salary = new Category("Salary", TransactionType.INCOME);
    Category bonus = new Category("Bonus", TransactionType.INCOME);

    wallet.addTransaction(new Transaction(5000.0, salary, TransactionType.INCOME, ""));
    wallet.addTransaction(new Transaction(1000.0, bonus, TransactionType.INCOME, ""));

    List<String> categories = Arrays.asList("Salary", "Bonus");
    double total = statisticsService.getIncomeByCategories(wallet, categories);

    assertEquals(6000.0, total, 0.01);
  }

  @Test
  void testGetExpensesByCategories() {
    Category food = new Category("Food", TransactionType.EXPENSE);
    Category transport = new Category("Transport", TransactionType.EXPENSE);

    wallet.addTransaction(new Transaction(500.0, food, TransactionType.EXPENSE, ""));
    wallet.addTransaction(new Transaction(200.0, transport, TransactionType.EXPENSE, ""));

    List<String> categories = Arrays.asList("Food", "Transport");
    double total = statisticsService.getExpensesByCategories(wallet, categories);

    assertEquals(700.0, total, 0.01);
  }

  @Test
  void testGetTransactionsByPeriod() {
    Category food = new Category("Food", TransactionType.EXPENSE);
    LocalDateTime now = LocalDateTime.now();

    Transaction t1 =
        new Transaction("1", 100.0, food, TransactionType.EXPENSE, now.minusDays(5), "");
    Transaction t2 =
        new Transaction("2", 200.0, food, TransactionType.EXPENSE, now.minusDays(2), "");
    Transaction t3 =
        new Transaction("3", 300.0, food, TransactionType.EXPENSE, now.plusDays(1), "");

    wallet.addTransaction(t1);
    wallet.addTransaction(t2);
    wallet.addTransaction(t3);

    List<Transaction> transactions =
        statisticsService.getTransactionsByPeriod(wallet, now.minusDays(3), now);

    assertEquals(1, transactions.size());
    assertEquals("2", transactions.get(0).getId());
  }

  @Test
  void testFindMissingCategories() {
    Category food = new Category("Food", TransactionType.EXPENSE);
    wallet.addTransaction(new Transaction(100.0, food, TransactionType.EXPENSE, ""));

    List<String> requested = Arrays.asList("Food", "Transport", "Entertainment");
    List<String> missing = statisticsService.findMissingCategories(wallet, requested);

    assertEquals(2, missing.size());
    assertTrue(missing.contains("Transport"));
    assertTrue(missing.contains("Entertainment"));
  }

  @Test
  void testGetBudgetSummary() {
    Category food = new Category("Food", TransactionType.EXPENSE);
    wallet.setBudget(food, 5000.0);

    Map<Category, Budget> budgets = statisticsService.getBudgetSummary(wallet);

    assertEquals(1, budgets.size());
    assertNotNull(budgets.get(food));
  }
}
