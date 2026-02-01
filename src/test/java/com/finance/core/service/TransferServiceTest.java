package com.finance.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.finance.core.model.Category;
import com.finance.core.model.TransactionType;
import com.finance.core.model.User;
import com.finance.core.model.Wallet;
import com.finance.exception.InsufficientFundsException;
import com.finance.infrastructure.repository.UserRepository;
import com.finance.infrastructure.repository.WalletRepository;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransferServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private WalletRepository walletRepository;

  private TransferService transferService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transferService = new TransferService(userRepository, walletRepository);
  }

  @Test
  void testTransferSuccess() throws IOException {
    User sender = new User("alice", "password123");
    sender.getWallet().addTransaction(
        new com.finance.core.model.Transaction(
            10000.0, new Category("Salary", TransactionType.INCOME), TransactionType.INCOME, ""));

    User recipient = new User("bob", "password123");
    Wallet recipientWallet = new Wallet("bob");

    when(userRepository.findByUsername("bob")).thenReturn(Optional.of(recipient));
    when(walletRepository.load("bob")).thenReturn(recipientWallet);

    transferService.transfer(sender, "bob", 500.0, "Payment");

    assertEquals(9500.0, sender.getWallet().getBalance(), 0.01);
    assertEquals(500.0, recipientWallet.getBalance(), 0.01);

    verify(walletRepository).save(sender.getWallet());
    verify(walletRepository).save(recipientWallet);
  }

  @Test
  void testTransferInsufficientFunds() {
    User sender = new User("alice", "password123");
    sender.getWallet().addTransaction(
        new com.finance.core.model.Transaction(
            100.0, new Category("Salary", TransactionType.INCOME), TransactionType.INCOME, ""));

    assertThrows(
        InsufficientFundsException.class,
        () -> transferService.transfer(sender, "bob", 500.0, "Payment"));
  }

  @Test
  void testTransferToNonexistentUser() {
    User sender = new User("alice", "password123");
    sender.getWallet().addTransaction(
        new com.finance.core.model.Transaction(
            1000.0, new Category("Salary", TransactionType.INCOME), TransactionType.INCOME, ""));

    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> transferService.transfer(sender, "nonexistent", 100.0, "Payment"));
  }

  @Test
  void testTransferToSelf() {
    User sender = new User("alice", "password123");
    sender.getWallet().addTransaction(
        new com.finance.core.model.Transaction(
            1000.0, new Category("Salary", TransactionType.INCOME), TransactionType.INCOME, ""));

    assertThrows(
        IllegalArgumentException.class,
        () -> transferService.transfer(sender, "alice", 100.0, "Payment"));
  }

  @Test
  void testTransferInvalidAmount() {
    User sender = new User("alice", "password123");

    assertThrows(
        IllegalArgumentException.class,
        () -> transferService.transfer(sender, "bob", 0, "Payment"));

    assertThrows(
        IllegalArgumentException.class,
        () -> transferService.transfer(sender, "bob", -100, "Payment"));
  }

  @Test
  void testTransferNullRecipient() {
    User sender = new User("alice", "password123");

    assertThrows(
        IllegalArgumentException.class, () -> transferService.transfer(sender, null, 100, "Payment"));

    assertThrows(
        IllegalArgumentException.class, () -> transferService.transfer(sender, "", 100, "Payment"));
  }
}
