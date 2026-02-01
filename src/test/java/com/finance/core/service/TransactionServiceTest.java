package com.finance.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;
import com.finance.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionServiceTest {
  @Mock private NotificationService notificationService;

  private TransactionService transactionService;
  private Wallet wallet;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transactionService = new TransactionService(notificationService);
    wallet = new Wallet("testuser");
  }

  @Test
  void testAddIncomeSuccess() {
    Category category = new Category("Salary", TransactionType.INCOME);
    Transaction transaction = transactionService.addIncome(wallet, 5000.0, category, "Monthly salary");

    assertNotNull(transaction);
    assertEquals(5000.0, transaction.getAmount());
    assertEquals(5000.0, wallet.getBalance());
    assertEquals(1, wallet.getTransactions().size());
  }

  @Test
  void testAddExpenseSuccess() {
    Category category = new Category("Food", TransactionType.EXPENSE);

    transactionService.addIncome(wallet, 10000.0, new Category("Salary", TransactionType.INCOME), "");

    Transaction transaction = transactionService.addExpense(wallet, 500.0, category, "Groceries");

    assertNotNull(transaction);
    assertEquals(500.0, transaction.getAmount());
    assertEquals(9500.0, wallet.getBalance());
    verify(notificationService).checkAfterExpense(wallet, category, 500.0);
  }

  @Test
  void testAddIncomeInvalidAmount() {
    Category category = new Category("Salary", TransactionType.INCOME);

    assertThrows(
        ValidationException.class, () -> transactionService.addIncome(wallet, 0, category, ""));

    assertThrows(
        ValidationException.class, () -> transactionService.addIncome(wallet, -100, category, ""));
  }

  @Test
  void testAddExpenseInvalidAmount() {
    Category category = new Category("Food", TransactionType.EXPENSE);

    assertThrows(
        ValidationException.class, () -> transactionService.addExpense(wallet, 0, category, ""));

    assertThrows(
        ValidationException.class, () -> transactionService.addExpense(wallet, -100, category, ""));
  }

  @Test
  void testAddTransactionNullCategory() {
    assertThrows(
        ValidationException.class, () -> transactionService.addIncome(wallet, 100, null, ""));

    assertThrows(
        ValidationException.class, () -> transactionService.addExpense(wallet, 100, null, ""));
  }
}
