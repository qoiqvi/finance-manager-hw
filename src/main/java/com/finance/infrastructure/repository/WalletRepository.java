package com.finance.infrastructure.repository;

import com.finance.core.model.Wallet;
import java.io.IOException;

/**
 * Repository interface for Wallet persistence operations.
 */
public interface WalletRepository {
  /**
   * Saves a wallet to persistent storage.
   *
   * @param wallet the wallet to save
   * @throws IOException if save operation fails
   */
  void save(Wallet wallet) throws IOException;

  /**
   * Loads a wallet from persistent storage.
   *
   * @param userId the user ID whose wallet to load
   * @return the loaded wallet or a new wallet if not found
   * @throws IOException if load operation fails
   */
  Wallet load(String userId) throws IOException;

  /**
   * Deletes a wallet from persistent storage.
   *
   * @param userId the user ID whose wallet to delete
   * @throws IOException if delete operation fails
   */
  void delete(String userId) throws IOException;

  /**
   * Checks if a wallet exists for a user.
   *
   * @param userId the user ID to check
   * @return true if wallet file exists
   */
  boolean exists(String userId);
}
