package com.finance.infrastructure.repository;

import com.finance.core.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User persistence operations.
 */
public interface UserRepository {
  /**
   * Saves a user.
   *
   * @param user the user to save
   */
  void save(User user);

  /**
   * Finds a user by username.
   *
   * @param username the username to search for
   * @return Optional containing the user if found
   */
  Optional<User> findByUsername(String username);

  /**
   * Gets all users.
   *
   * @return list of all users
   */
  List<User> findAll();

  /**
   * Checks if a user exists by username.
   *
   * @param username the username to check
   * @return true if user exists
   */
  boolean exists(String username);

  /**
   * Deletes a user by username.
   *
   * @param username the username to delete
   */
  void delete(String username);
}
