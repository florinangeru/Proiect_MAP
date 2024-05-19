package com.banking.services;

import com.banking.models.*;
import com.banking.enums.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StorageService {
    private static final Logger logger = LogManager.getLogger(StorageService.class);
    private static StorageService instance = null;
    private static final String DATABASE_PATH = "database/";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private StorageService() {
        File directory = new File(DATABASE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static synchronized StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
        }
        return instance;
    }

    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(DATABASE_PATH + "customers.csv");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length == 4) {
                        String id = values[0];
                        String name = values[1];
                        String surname = values[2];
                        int age = Integer.parseInt(values[3]);
                        Customer customer = new Customer(id, name, surname, age);
                        customers.add(customer);
                    }
                }
                logger.info("Loaded customers from CSV.");
            } catch (IOException e) {
                logger.error("Error loading customers from CSV: {}", e.getMessage(), e);
            }
        }

        return customers;
    }

    public void saveCustomers(Collection<Customer> customers) {
        File file = new File(DATABASE_PATH + "customers.csv");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Customer customer : customers) {
                writer.println(String.join(",",
                        customer.getId(),
                        customer.getName(),
                        customer.getSurname(),
                        String.valueOf(customer.getAge())));
            }
            logger.info("Saved customers to CSV.");
        } catch (IOException e) {
            logger.error("Error saving customers to CSV: {}", e.getMessage(), e);
        }
    }

    public List<Account> loadAccounts(Map<String, Customer> customerMap) {
        List<Account> accounts = new ArrayList<>();
        File file = new File(DATABASE_PATH + "accounts.csv");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length == 4) {
                        String id = values[0];
                        String type = values[1];
                        String customerId = values[2];
                        double balance = Double.parseDouble(values[3]);

                        Customer customer = customerMap.get(customerId);
                        if (customer != null) {
                            Account account;
                            if (type.equals("PRIMARY")) {
                                account = new PrimaryAccount(id, customer);
                            } else {
                                account = new SavingsAccount(id, customer, 1.5); // Assuming 1.5% interest rate
                            }
                            account.deposit(balance); // Set the initial balance
                            accounts.add(account);
                            customer.addAccount(account);
                        }
                    }
                }
                logger.info("Loaded accounts from CSV.");
            } catch (IOException e) {
                logger.error("Error loading accounts from CSV: {}", e.getMessage(), e);
            }
        }

        return accounts;
    }

    public void saveAccounts(Collection<Account> accounts) {
        File file = new File(DATABASE_PATH + "accounts.csv");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Account account : accounts) {
                String accountType = account instanceof PrimaryAccount ? "PRIMARY" : "SAVINGS";
                writer.println(String.join(",",
                        account.getAccountId(),
                        accountType,
                        account.getOwner().getId(),
                        String.valueOf(account.getBalance())));
            }
            logger.info("Saved accounts to CSV.");
        } catch (IOException e) {
            logger.error("Error saving accounts to CSV: {}", e.getMessage(), e);
        }
    }

    public List<Transaction> loadTransactions(Map<String, Account> accountMap) {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(DATABASE_PATH + "transactions.csv");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length == 5) {
                        String id = values[0];
                        String accountId = values[1];
                        TransactionType type = TransactionType.valueOf(values[2]);
                        double amount = Double.parseDouble(values[3]);
                        Date timestamp = dateFormat.parse(values[4]);

                        Account account = accountMap.get(accountId);
                        if (account != null) {
                            Transaction transaction = new Transaction(id, amount, type, account);
                            transaction.setTimestamp(timestamp); // Set the timestamp from the file
                            account.getTransactions().add(transaction);
                            transactions.add(transaction);
                        }
                    }
                }
                logger.info("Loaded transactions from CSV.");
            } catch (IOException | ParseException e) {
                logger.error("Error loading transactions from CSV: {}", e.getMessage(), e);
            }
        }

        return transactions;
    }

    public void saveTransactions(Collection<Transaction> transactions) {
        File file = new File(DATABASE_PATH + "transactions.csv");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Transaction transaction : transactions) {
                writer.println(String.join(",",
                        transaction.getTransactionId(),
                        transaction.getAccount().getAccountId(),
                        transaction.getType().toString(),
                        String.valueOf(transaction.getAmount()),
                        dateFormat.format(transaction.getTimestamp())));
            }
            logger.info("Saved transactions to CSV.");
        } catch (IOException e) {
            logger.error("Error saving transactions to CSV: {}", e.getMessage(), e);
        }
    }

    public List<Card> loadCards(Map<String, Account> accountMap) {
        List<Card> cards = new ArrayList<>();
        File file = new File(DATABASE_PATH + "cards.csv");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length == 4) {
                        String cardNumber = values[0];
                        String accountId = values[1];
                        Date expirationDate = dateFormat.parse(values[2]);
                        boolean blocked = Boolean.parseBoolean(values[3]);

                        Account account = accountMap.get(accountId);
                        if (account != null) {
                            Card card = new Card(cardNumber, expirationDate, account);
                            if (blocked) {
                                card.block();
                            }
                            account.getCards().add(card);
                            cards.add(card);
                        }
                    }
                }
                logger.info("Loaded cards from CSV.");
            } catch (IOException | ParseException e) {
                logger.error("Error loading cards from CSV: {}", e.getMessage(), e);
            }
        }

        return cards;
    }

    public void saveCards(Collection<Card> cards) {
        File file = new File(DATABASE_PATH + "cards.csv");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Card card : cards) {
                writer.println(String.join(",",
                        card.getCardNumber(),
                        card.getAccount().getAccountId(),
                        dateFormat.format(card.getExpirationDate()),
                        String.valueOf(card.isBlocked())));
            }
            logger.info("Saved cards to CSV.");
        } catch (IOException e) {
            logger.error("Error saving cards to CSV: {}", e.getMessage(), e);
        }
    }
}
