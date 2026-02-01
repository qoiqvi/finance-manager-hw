package com.finance.infrastructure.repository;

import com.finance.core.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UserRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryUserRepository implements UserRepository {
  private final Map<String, User> users = new ConcurrentHashMap<>();

  @Override
  public void save(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    users.put(user.getUsername(), user);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(users.get(username.trim()));
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(users.values());
  }

  @Override
  public boolean exists(String username) {
    return username != null && users.containsKey(username.trim());
  }

  @Override
  public void delete(String username) {
    if (username != null) {
      users.remove(username.trim());
    }
  }
}
