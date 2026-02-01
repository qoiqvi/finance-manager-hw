package com.finance.core.model;

import java.util.Objects;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Represents a user of the finance management system.
 */
public class User {
  private final String username;
  private final String passwordHash;
  private Wallet wallet;

  /**
   * Creates a new user with hashed password.
   *
   * @param username the username
   * @param password the plain text password (will be hashed)
   */
  public User(String username, String password) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
    if (password == null || password.length() < 6) {
      throw new IllegalArgumentException("Password must be at least 6 characters");
    }

    this.username = username.trim();
    this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
    this.wallet = new Wallet(username);
  }

  /**
   * Creates a user with existing password hash (for loading from storage).
   *
   * @param username the username
   * @param passwordHash the already hashed password
   * @param wallet the user's wallet
   */
  public User(String username, String passwordHash, Wallet wallet) {
    this.username = username;
    this.passwordHash = passwordHash;
    this.wallet = wallet != null ? wallet : new Wallet(username);
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Wallet getWallet() {
    return wallet;
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  /**
   * Authenticates the user with provided password.
   *
   * @param password the password to check
   * @return true if password matches
   */
  public boolean authenticate(String password) {
    if (password == null) {
      return false;
    }
    return BCrypt.checkpw(password, passwordHash);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public String toString() {
    return "User[" + username + "]";
  }
}
