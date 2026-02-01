package com.finance.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;
import com.finance.infrastructure.repository.JsonWalletRepository;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonWalletRepositoryTest {
  private JsonWalletRepository repository;
  private static final String TEST_USER = "testuser_integration";

  @BeforeEach
  void setUp() {
    repository = new JsonWalletRepository();
  }

  @AfterEach
  void tearDown() throws IOException {
    if (repository.exists(TEST_USER)) {
      repository.delete(TEST_USER);
    }
  }

  @Test
  void testSaveAndLoadWallet() throws IOException {
    Wallet wallet = new Wallet(TEST_USER);

    Category salary = new Category("Salary", TransactionType.INCOME);
    Category food = new Category("Food", TransactionType.EXPENSE);

    wallet.addTransaction(new Transaction(5000.0, salary, TransactionType.INCOME, "Monthly salary"));
    wallet.addTransaction(new Transaction(500.0, food, TransactionType.EXPENSE, "Groceries"));
    wallet.setBudget(food, 3000.0);

    repository.save(wallet);

    Wallet loaded = repository.load(TEST_USER);

    assertNotNull(loaded);
    assertEquals(TEST_USER, loaded.getUserId());
    assertEquals(4500.0, loaded.getBalance(), 0.01);
    assertEquals(2, loaded.getTransactions().size());
    assertEquals(1, loaded.getBudgets().size());
  }

  @Test
  void testLoadNonexistentWallet() throws IOException {
    Wallet wallet = repository.load("nonexistent_user");

    assertNotNull(wallet);
    assertEquals("nonexistent_user", wallet.getUserId());
    assertEquals(0.0, wallet.getBalance());
    assertTrue(wallet.getTransactions().isEmpty());
  }

  @Test
  void testWalletExists() throws IOException {
    assertFalse(repository.exists(TEST_USER));

    Wallet wallet = new Wallet(TEST_USER);
    repository.save(wallet);

    assertTrue(repository.exists(TEST_USER));
  }

  @Test
  void testDeleteWallet() throws IOException {
    Wallet wallet = new Wallet(TEST_USER);
    repository.save(wallet);
    assertTrue(repository.exists(TEST_USER));

    repository.delete(TEST_USER);

    assertFalse(repository.exists(TEST_USER));
  }

  @Test
  void testSaveWalletWithBudgets() throws IOException {
    Wallet wallet = new Wallet(TEST_USER);

    Category food = new Category("Food", TransactionType.EXPENSE);
    Category transport = new Category("Transport", TransactionType.EXPENSE);

    wallet.setBudget(food, 5000.0);
    wallet.setBudget(transport, 2000.0);

    wallet.addTransaction(new Transaction(800.0, food, TransactionType.EXPENSE, ""));

    repository.save(wallet);
    Wallet loaded = repository.load(TEST_USER);

    assertEquals(2, loaded.getBudgets().size());
    Budget loadedFoodBudget = loaded.getBudget(food);
    assertNotNull(loadedFoodBudget);
    assertEquals(5000.0, loadedFoodBudget.getLimit(), 0.01);
    assertEquals(800.0, loadedFoodBudget.getSpent(), 0.01);
  }
}
