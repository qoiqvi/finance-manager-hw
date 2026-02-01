package com.finance.core.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;
import com.finance.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BudgetServiceTest {
  private BudgetService budgetService;
  private Wallet wallet;

  @BeforeEach
  void setUp() {
    budgetService = new BudgetService();
    wallet = new Wallet("testuser");
  }

  @Test
  void testSetBudget() {
    Budget budget = budgetService.setBudget(wallet, "Food", 5000.0);

    assertNotNull(budget);
    assertEquals(5000.0, budget.getLimit());
    assertEquals("Food", budget.getCategory().getName());
  }

  @Test
  void testEditBudget() {
    budgetService.setBudget(wallet, "Food", 5000.0);
    Budget updated = budgetService.editBudget(wallet, "Food", 6000.0);

    assertNotNull(updated);
    assertEquals(6000.0, updated.getLimit());
  }

  @Test
  void testDeleteBudget() {
    budgetService.setBudget(wallet, "Food", 5000.0);
    budgetService.deleteBudget(wallet, "Food");

    Budget budget = budgetService.getBudget(wallet, "Food");
    assertNull(budget);
  }

  @Test
  void testGetBudget() {
    budgetService.setBudget(wallet, "Food", 5000.0);
    Budget budget = budgetService.getBudget(wallet, "Food");

    assertNotNull(budget);
    assertEquals(5000.0, budget.getLimit());
  }

  @Test
  void testBudgetExceeded() {
    Budget budget = new Budget(new Category("Food", TransactionType.EXPENSE), 1000.0);
    budget.addSpent(1200.0);

    assertTrue(budgetService.isBudgetExceeded(budget));
  }

  @Test
  void testBudgetNotExceeded() {
    Budget budget = new Budget(new Category("Food", TransactionType.EXPENSE), 1000.0);
    budget.addSpent(500.0);

    assertFalse(budgetService.isBudgetExceeded(budget));
  }

  @Test
  void testRemainingBudget() {
    Budget budget = new Budget(new Category("Food", TransactionType.EXPENSE), 1000.0);
    budget.addSpent(300.0);

    double remaining = budgetService.getRemainingBudget(budget);
    assertEquals(700.0, remaining, 0.01);
  }

  @Test
  void testSetBudgetInvalidCategory() {
    assertThrows(ValidationException.class, () -> budgetService.setBudget(wallet, "", 1000.0));

    assertThrows(ValidationException.class, () -> budgetService.setBudget(wallet, null, 1000.0));
  }

  @Test
  void testSetBudgetNegativeLimit() {
    assertThrows(ValidationException.class, () -> budgetService.setBudget(wallet, "Food", -100.0));
  }
}
