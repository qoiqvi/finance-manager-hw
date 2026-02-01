package com.finance.infrastructure.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.finance.core.model.Budget;
import com.finance.core.model.Category;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;
import com.finance.infrastructure.storage.JsonStorageManager;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON-based implementation of WalletRepository.
 */
public class JsonWalletRepository implements WalletRepository {
  private static final String WALLET_FILE_SUFFIX = "_wallet.json";
  private final JsonStorageManager storageManager;
  private final ObjectMapper objectMapper;

  /** Creates a new JsonWalletRepository. */
  public JsonWalletRepository() {
    this.storageManager = new JsonStorageManager();
    this.objectMapper = storageManager.getObjectMapper();
  }

  @Override
  public void save(Wallet wallet) throws IOException {
    if (wallet == null) {
      throw new IllegalArgumentException("Wallet cannot be null");
    }

    String filename = wallet.getUserId() + WALLET_FILE_SUFFIX;
    ObjectNode walletNode = objectMapper.createObjectNode();

    walletNode.put("userId", wallet.getUserId());
    walletNode.put("balance", wallet.getBalance());

    ArrayNode transactionsArray = objectMapper.createArrayNode();
    for (Transaction transaction : wallet.getTransactions()) {
      ObjectNode transactionNode = objectMapper.createObjectNode();
      transactionNode.put("id", transaction.getId());
      transactionNode.put("amount", transaction.getAmount());
      transactionNode.put("category", transaction.getCategory().getName());
      transactionNode.put("type", transaction.getType().name());
      transactionNode.put("date", transaction.getDate().toString());
      transactionNode.put("description", transaction.getDescription());
      transactionsArray.add(transactionNode);
    }
    walletNode.set("transactions", transactionsArray);

    ArrayNode budgetsArray = objectMapper.createArrayNode();
    for (Map.Entry<Category, Budget> entry : wallet.getBudgets().entrySet()) {
      Budget budget = entry.getValue();
      ObjectNode budgetNode = objectMapper.createObjectNode();
      budgetNode.put("category", budget.getCategory().getName());
      budgetNode.put("categoryType", budget.getCategory().getType().name());
      budgetNode.put("limit", budget.getLimit());
      budgetNode.put("spent", budget.getSpent());
      budgetsArray.add(budgetNode);
    }
    walletNode.set("budgets", budgetsArray);

    storageManager.writeToFile(filename, walletNode);
  }

  @Override
  public Wallet load(String userId) throws IOException {
    if (userId == null || userId.trim().isEmpty()) {
      throw new IllegalArgumentException("User ID cannot be empty");
    }

    String filename = userId + WALLET_FILE_SUFFIX;
    if (!storageManager.fileExists(filename)) {
      return new Wallet(userId);
    }

    JsonNode walletNode = storageManager.readFromFile(filename, JsonNode.class);

    double balance = walletNode.get("balance").asDouble();

    List<Transaction> transactions = new ArrayList<>();
    JsonNode transactionsNode = walletNode.get("transactions");
    if (transactionsNode != null && transactionsNode.isArray()) {
      for (JsonNode transactionNode : transactionsNode) {
        String id = transactionNode.get("id").asText();
        double amount = transactionNode.get("amount").asDouble();
        String categoryName = transactionNode.get("category").asText();
        TransactionType type = TransactionType.valueOf(transactionNode.get("type").asText());
        LocalDateTime date = LocalDateTime.parse(transactionNode.get("date").asText());
        String description = transactionNode.get("description").asText();

        Category category = new Category(categoryName, type);
        Transaction transaction = new Transaction(id, amount, category, type, date, description);
        transactions.add(transaction);
      }
    }

    Map<Category, Budget> budgets = new HashMap<>();
    JsonNode budgetsNode = walletNode.get("budgets");
    if (budgetsNode != null && budgetsNode.isArray()) {
      for (JsonNode budgetNode : budgetsNode) {
        String categoryName = budgetNode.get("category").asText();
        String categoryTypeStr = budgetNode.get("categoryType").asText();
        TransactionType categoryType = TransactionType.valueOf(categoryTypeStr);
        double limit = budgetNode.get("limit").asDouble();
        double spent = budgetNode.get("spent").asDouble();

        Category category = new Category(categoryName, categoryType);
        Budget budget = new Budget(category, limit, spent);
        budgets.put(category, budget);
      }
    }

    return new Wallet(userId, balance, transactions, budgets);
  }

  @Override
  public void delete(String userId) throws IOException {
    if (userId == null || userId.trim().isEmpty()) {
      throw new IllegalArgumentException("User ID cannot be empty");
    }

    String filename = userId + WALLET_FILE_SUFFIX;
    storageManager.deleteFile(filename);
  }

  @Override
  public boolean exists(String userId) {
    if (userId == null || userId.trim().isEmpty()) {
      return false;
    }
    String filename = userId + WALLET_FILE_SUFFIX;
    return storageManager.fileExists(filename);
  }
}
