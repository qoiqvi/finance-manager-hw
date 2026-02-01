package com.finance.core.service;

import com.finance.core.model.User;
import com.finance.exception.AuthenticationException;
import com.finance.infrastructure.repository.UserRepository;
import com.finance.infrastructure.repository.WalletRepository;
import java.io.IOException;
import java.util.Optional;

/**
 * Service for user authentication and registration.
 */
public class AuthService {
  private final UserRepository userRepository;
  private final WalletRepository walletRepository;
  private User currentUser;

  /**
   * Creates a new AuthService.
   *
   * @param userRepository the user repository
   * @param walletRepository the wallet repository
   */
  public AuthService(UserRepository userRepository, WalletRepository walletRepository) {
    this.userRepository = userRepository;
    this.walletRepository = walletRepository;
    this.currentUser = null;
  }

  /**
   * Registers a new user.
   *
   * @param username the username
   * @param password the password
   * @return the created user
   * @throws AuthenticationException if username already exists
   */
  public User register(String username, String password) {
    if (username == null || username.trim().isEmpty()) {
      throw new AuthenticationException("Username cannot be empty");
    }

    if (password == null || password.length() < 6) {
      throw new AuthenticationException("Password must be at least 6 characters");
    }

    if (!isValidUsername(username)) {
      throw new AuthenticationException(
          "Username must be 3-20 alphanumeric characters");
    }

    if (userRepository.exists(username)) {
      throw new AuthenticationException("Username already exists");
    }

    User user = new User(username, password);
    userRepository.save(user);
    return user;
  }

  /**
   * Logs in a user.
   *
   * @param username the username
   * @param password the password
   * @return the authenticated user
   * @throws AuthenticationException if authentication fails
   */
  public User login(String username, String password) {
    Optional<User> userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      throw new AuthenticationException("Invalid username or password");
    }

    User user = userOpt.get();
    if (!user.authenticate(password)) {
      throw new AuthenticationException("Invalid username or password");
    }

    try {
      user.setWallet(walletRepository.load(username));
    } catch (IOException e) {
      throw new AuthenticationException("Failed to load user wallet", e);
    }

    this.currentUser = user;
    return user;
  }

  /**
   * Logs out the current user and saves their wallet.
   *
   * @throws IOException if wallet save fails
   */
  public void logout() throws IOException {
    if (currentUser != null) {
      walletRepository.save(currentUser.getWallet());
      currentUser = null;
    }
  }

  /**
   * Gets the currently logged-in user.
   *
   * @return the current user or null if not logged in
   */
  public User getCurrentUser() {
    return currentUser;
  }

  /**
   * Checks if a user is currently logged in.
   *
   * @return true if user is logged in
   */
  public boolean isLoggedIn() {
    return currentUser != null;
  }

  /**
   * Validates username format.
   *
   * @param username the username to validate
   * @return true if valid
   */
  private boolean isValidUsername(String username) {
    if (username == null) {
      return false;
    }
    String trimmed = username.trim();
    return trimmed.length() >= 3
        && trimmed.length() <= 20
        && trimmed.matches("[a-zA-Z0-9]+");
  }
}
