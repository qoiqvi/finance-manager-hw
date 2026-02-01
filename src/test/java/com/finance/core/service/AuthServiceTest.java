package com.finance.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.finance.core.model.User;
import com.finance.core.model.Wallet;
import com.finance.exception.AuthenticationException;
import com.finance.infrastructure.repository.UserRepository;
import com.finance.infrastructure.repository.WalletRepository;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private WalletRepository walletRepository;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    authService = new AuthService(userRepository, walletRepository);
  }

  @Test
  void testRegisterSuccess() {
    when(userRepository.exists("john123")).thenReturn(false);

    User user = authService.register("john123", "password123");

    assertNotNull(user);
    assertEquals("john123", user.getUsername());
    verify(userRepository).save(user);
  }

  @Test
  void testRegisterDuplicateUsername() {
    when(userRepository.exists("john123")).thenReturn(true);

    assertThrows(
        AuthenticationException.class, () -> authService.register("john123", "password123"));
  }

  @Test
  void testRegisterInvalidUsername() {
    assertThrows(AuthenticationException.class, () -> authService.register("ab", "password123"));

    assertThrows(AuthenticationException.class, () -> authService.register("", "password123"));

    assertThrows(
        AuthenticationException.class,
        () -> authService.register("this_is_too_long_username", "password123"));
  }

  @Test
  void testRegisterInvalidPassword() {
    assertThrows(AuthenticationException.class, () -> authService.register("john123", "12345"));

    assertThrows(AuthenticationException.class, () -> authService.register("john123", ""));
  }

  @Test
  void testLoginSuccess() throws IOException {
    User user = new User("john123", "password123");
    Wallet wallet = new Wallet("john123");

    when(userRepository.findByUsername("john123")).thenReturn(Optional.of(user));
    when(walletRepository.load("john123")).thenReturn(wallet);

    User loggedInUser = authService.login("john123", "password123");

    assertNotNull(loggedInUser);
    assertEquals("john123", loggedInUser.getUsername());
    assertTrue(authService.isLoggedIn());
  }

  @Test
  void testLoginInvalidUsername() {
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThrows(
        AuthenticationException.class, () -> authService.login("nonexistent", "password123"));
  }

  @Test
  void testLoginInvalidPassword() {
    User user = new User("john123", "correctpassword");
    when(userRepository.findByUsername("john123")).thenReturn(Optional.of(user));

    assertThrows(
        AuthenticationException.class, () -> authService.login("john123", "wrongpassword"));
  }

  @Test
  void testLogout() throws IOException {
    User user = new User("john123", "password123");
    Wallet wallet = new Wallet("john123");

    when(userRepository.findByUsername("john123")).thenReturn(Optional.of(user));
    when(walletRepository.load("john123")).thenReturn(wallet);

    authService.login("john123", "password123");
    assertTrue(authService.isLoggedIn());

    authService.logout();

    assertFalse(authService.isLoggedIn());
    assertNull(authService.getCurrentUser());
    verify(walletRepository).save(wallet);
  }
}
