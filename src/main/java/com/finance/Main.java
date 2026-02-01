package com.finance;

import com.finance.cli.FinanceApp;

/**
 * Main entry point for the Personal Finance Manager application.
 */
public class Main {
  /**
   * Main method.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    FinanceApp app = new FinanceApp();
    app.start();
  }
}
