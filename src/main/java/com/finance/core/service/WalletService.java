package com.finance.core.service;

import com.finance.core.model.Wallet;
import com.finance.infrastructure.repository.WalletRepository;
import java.io.IOException;

/**
 * Service for wallet management operations.
 */
public class WalletService {
  private final WalletRepository walletRepository;

  /**
   * Creates a new WalletService.
   *
   * @param walletRepository the wallet repository
   */
  public WalletService(WalletRepository walletRepository) {
    this.walletRepository = walletRepository;
  }

  /**
   * Saves a wallet to persistent storage.
   *
   * @param wallet the wallet to save
   * @throws IOException if save fails
   */
  public void saveWallet(Wallet wallet) throws IOException {
    if (wallet == null) {
      throw new IllegalArgumentException("Wallet cannot be null");
    }
    walletRepository.save(wallet);
  }

  /**
   * Loads a wallet from persistent storage.
   *
   * @param userId the user ID
   * @return the loaded wallet or new wallet if not found
   * @throws IOException if load fails
   */
  public Wallet loadWallet(String userId) throws IOException {
    if (userId == null || userId.trim().isEmpty()) {
      throw new IllegalArgumentException("User ID cannot be empty");
    }
    return walletRepository.load(userId);
  }

  /**
   * Checks if a wallet exists for a user.
   *
   * @param userId the user ID
   * @return true if wallet exists
   */
  public boolean walletExists(String userId) {
    return walletRepository.exists(userId);
  }

  /**
   * Deletes a wallet from persistent storage.
   *
   * @param userId the user ID
   * @throws IOException if delete fails
   */
  public void deleteWallet(String userId) throws IOException {
    walletRepository.delete(userId);
  }
}
