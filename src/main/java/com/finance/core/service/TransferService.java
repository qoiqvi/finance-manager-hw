package com.finance.core.service;

import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.User;
import com.finance.exception.InsufficientFundsException;
import com.finance.infrastructure.repository.UserRepository;
import com.finance.infrastructure.repository.WalletRepository;
import java.io.IOException;
import java.util.Optional;

/**
 * Service for managing transfers between user wallets.
 */
public class TransferService {
  private static final String TRANSFER_CATEGORY = "Перевод";
  private final UserRepository userRepository;
  private final WalletRepository walletRepository;

  /**
   * Creates a new TransferService.
   *
   * @param userRepository the user repository
   * @param walletRepository the wallet repository
   */
  public TransferService(UserRepository userRepository, WalletRepository walletRepository) {
    this.userRepository = userRepository;
    this.walletRepository = walletRepository;
  }

  /**
   * Transfers money from one user to another.
   *
   * @param sender the sending user
   * @param recipientUsername the recipient's username
   * @param amount the amount to transfer
   * @param description optional description
   * @throws InsufficientFundsException if sender doesn't have enough funds
   * @throws IllegalArgumentException if recipient not found
   * @throws IOException if wallet save fails
   */
  public void transfer(User sender, String recipientUsername, double amount, String description)
      throws IOException {
    if (sender == null) {
      throw new IllegalArgumentException("Sender cannot be null");
    }

    if (recipientUsername == null || recipientUsername.trim().isEmpty()) {
      throw new IllegalArgumentException("Recipient username cannot be empty");
    }

    if (amount <= 0) {
      throw new IllegalArgumentException("Transfer amount must be positive");
    }

    if (sender.getUsername().equals(recipientUsername.trim())) {
      throw new IllegalArgumentException("Cannot transfer to yourself");
    }

    if (sender.getWallet().getBalance() < amount) {
      throw new InsufficientFundsException(
          String.format(
              "Insufficient funds. Balance: %.2f, Required: %.2f",
              sender.getWallet().getBalance(), amount));
    }

    Optional<User> recipientOpt = userRepository.findByUsername(recipientUsername.trim());
    if (recipientOpt.isEmpty()) {
      throw new IllegalArgumentException("Recipient user not found: " + recipientUsername);
    }

    User recipient = recipientOpt.get();
    try {
      recipient.setWallet(walletRepository.load(recipient.getUsername()));
    } catch (IOException e) {
      throw new IOException("Failed to load recipient wallet", e);
    }

    Category transferCategory = new Category(TRANSFER_CATEGORY, TransactionType.EXPENSE);
    String expenseDesc =
        String.format("Transfer to %s%s", recipientUsername, description != null ? ": " + description : "");
    Transaction senderTransaction =
        new Transaction(amount, transferCategory, TransactionType.EXPENSE, expenseDesc);
    sender.getWallet().addTransaction(senderTransaction);

    Category incomeCategory = new Category(TRANSFER_CATEGORY, TransactionType.INCOME);
    String incomeDesc =
        String.format("Transfer from %s%s", sender.getUsername(), description != null ? ": " + description : "");
    Transaction recipientTransaction =
        new Transaction(amount, incomeCategory, TransactionType.INCOME, incomeDesc);
    recipient.getWallet().addTransaction(recipientTransaction);

    try {
      walletRepository.save(sender.getWallet());
      walletRepository.save(recipient.getWallet());
    } catch (IOException e) {
      throw new IOException("Failed to save wallets during transfer", e);
    }

    System.out.println(
        String.format(
            "✓ Transfer successful: %.2f sent to %s", amount, recipientUsername));
  }
}
