package com.finance.exception;

/**
 * Exception thrown when a transaction would result in insufficient funds.
 */
public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message) {
    super(message);
  }

  public InsufficientFundsException(String message, Throwable cause) {
    super(message, cause);
  }
}
