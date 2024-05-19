package com.banking.services;

import com.banking.enums.AccountType;
import com.banking.enums.TransactionType;
import com.banking.exceptions.InsufficientFundsException;
import com.banking.exceptions.InvalidAccountException;
import com.banking.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class BankService implements BankServiceInterface {
    private static final Logger logger = LogManager.getLogger(BankService.class);
    private static final AuditService auditService = AuditService.getInstance();
    private static int customerIndex = 0;

    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Account> accounts = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();
    private List<Card> cards = new ArrayList<>();
    private final Random random = new Random();

    public BankService() {
        loadData();
    }

    private void loadData() {
        StorageService storageService = StorageService.getInstance();

        List<Customer> loadedCustomers = storageService.loadCustomers();
        for (Customer customer : loadedCustomers) {
            customers.put(customer.getId(), customer);
            customerIndex = Math.max(customerIndex, Integer.parseInt(customer.getId()));
        }

        List<Account> loadedAccounts = storageService.loadAccounts(customers);
        for (Account account : loadedAccounts) {
            accounts.put(account.getAccountId(), account);
        }

        transactions = storageService.loadTransactions(accounts);

        cards = storageService.loadCards(accounts);

        logger.info("Loaded all data from CSV files.");
        auditService.logAction("loadData");
    }

    @Override
    public void createCustomer(String name, String surname, int age) {
        String customerId = String.format("%04d", ++customerIndex);
        Customer customer = new Customer(customerId, name, surname, age);
        customers.put(customerId, customer);
        logger.info("Created customer: {}", customerId);
        auditService.logAction("createCustomer");
        saveData("customers");
    }

    @Override
    public void createAccount(String customerId, AccountType type) throws InvalidAccountException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            logger.error("Customer not found: {}", customerId);
            throw new InvalidAccountException("Customer not found");
        }

        String accountId = "RO49AAAA1B31007" + (100000000 + random.nextInt(900000000));
        Account account;
        if (type == AccountType.PRIMARY) {
            account = new PrimaryAccount(accountId, customer);
        } else if (type == AccountType.SAVINGS) {
            account = new SavingsAccount(accountId, customer, 1.5); // Assuming 1.5% interest rate
        } else {
            logger.error("Invalid account type: {}", type);
            throw new InvalidAccountException("Invalid account type");
        }

        accounts.put(accountId, account);
        customer.addAccount(account);
        logger.info("Created account: {} for customer: {}", accountId, customerId);
        auditService.logAction("createAccount");
        saveData("accounts");
    }

    @Override
    public List<Account> getAccountsByCustomer(String customerId) throws InvalidAccountException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            logger.error("Customer not found: {}", customerId);
            throw new InvalidAccountException("Customer not found");
        }
        logger.info("Retrieved accounts for customer: {}", customerId);
        return customer.getAccounts();
    }

    @Override
    public BankStatement generateBankStatement(String accountId, Date startDate, Date endDate) throws InvalidAccountException {
        Account account = accounts.get(accountId);
        if (account == null) {
            logger.error("Account not found: {}", accountId);
            throw new InvalidAccountException("Account not found");
        }

        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : account.getTransactions()) {
            if (!transaction.getTimestamp().before(startDate) && !transaction.getTimestamp().after(endDate)) {
                filteredTransactions.add(transaction);
            }
        }

        double closingBalance = account.getBalance();
        String statementId = UUID.randomUUID().toString();

        logger.info("Generated bank statement: {} for account: {} from {} to {}", statementId, accountId, startDate, endDate);
        return new BankStatement(statementId, account, startDate, endDate, filteredTransactions, closingBalance);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Customer getCustomerById(String customerId) throws InvalidAccountException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            logger.error("Customer not found: {}", customerId);
            throw new InvalidAccountException("Customer not found");
        }
        return customer;
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
        logger.info("Updated customer: {}", customer.getId());
        auditService.logAction("updateCustomer");
        saveData("customers");
    }

    @Override
    public void deleteCustomer(String customerId) throws InvalidAccountException {
        Customer customer = customers.remove(customerId);
        if (customer == null) {
            logger.error("Customer not found: {}", customerId);
            throw new InvalidAccountException("Customer not found");
        }
        logger.info("Deleted customer: {}", customerId);
        auditService.logAction("deleteCustomer");
        saveData("customers");
    }

    @Override
    public void deleteAccount(String accountId) throws InvalidAccountException {
        Account account = accounts.remove(accountId);
        if (account == null) {
            logger.error("Account not found: {}", accountId);
            throw new InvalidAccountException("Account not found");
        }
        account.getOwner().getAccounts().remove(account);
        logger.info("Deleted account: {}", accountId);
        auditService.logAction("deleteAccount");
        saveData("accounts");
    }

    @Override
    public void addCard(Card card) {
        cards.add(card);
        logger.info("Added card: {}", card.getCardNumber());
        auditService.logAction("addCard");
        saveData("cards");
    }

    @Override
    public void removeCard(Card card) {
        cards.remove(card);
        logger.info("Removed card: {}", card.getCardNumber());
        auditService.logAction("removeCard");
        saveData("cards");
    }

    @Override
    public void transfer(String fromAccountId, String toAccountId, double amount) throws InsufficientFundsException, InvalidAccountException {
        Account fromAccount = accounts.get(fromAccountId);
        Account toAccount = accounts.get(toAccountId);
        if (fromAccount == null) {
            logger.error("Source account not found: {}", fromAccountId);
            throw new InvalidAccountException("Source account not found");
        }
        if (toAccount == null) {
            logger.error("Destination account not found: {}", toAccountId);
            throw new InvalidAccountException("Destination account not found");
        }
        if (amount <= 0) {
            logger.error("Transfer amount must be positive: {}", amount);
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), amount, TransactionType.WITHDRAWAL, fromAccount);
        transactions.add(transaction);
        transaction = new Transaction(UUID.randomUUID().toString(), amount, TransactionType.DEPOSIT, toAccount);
        transactions.add(transaction);
        logger.info("Transferred {} from account {} to account {}", amount, fromAccountId, toAccountId);
        auditService.logAction("transfer");
        saveData("transactions");
    }

    @Override
    public void withdraw(String accountId, double amount) throws InsufficientFundsException, InvalidAccountException {
        Account account = accounts.get(accountId);
        if (account == null) {
            logger.error("Account not found: {}", accountId);
            throw new InvalidAccountException("Account not found");
        }
        if (amount <= 0) {
            logger.error("Withdrawal amount must be positive: {}", amount);
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        account.withdraw(amount);
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), amount, TransactionType.WITHDRAWAL, account);
        transactions.add(transaction);
        logger.info("Withdrew {} from account {}", amount, accountId);
        auditService.logAction("withdraw");
        saveData("transactions");
    }

    @Override
    public void deposit(String accountId, double amount) throws InvalidAccountException {
        Account account = accounts.get(accountId);
        if (account == null) {
            logger.error("Account not found: {}", accountId);
            throw new InvalidAccountException("Account not found");
        }
        if (amount <= 0) {
            logger.error("Deposit amount must be positive: {}", amount);
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        account.deposit(amount);
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), amount, TransactionType.DEPOSIT, account);
        transactions.add(transaction);
        logger.info("Deposited {} to account {}", amount, accountId);
        auditService.logAction("deposit");
        saveData("transactions");
    }

    @Override
    public Account getAccountById(String accountId) {
        return accounts.get(accountId);
    }

    private void saveData(String type) {
        StorageService storageService = StorageService.getInstance();
        switch (type) {
            case "customers" -> {
                storageService.saveCustomers(customers.values());
                auditService.logAction("saveCustomers");
            }
            case "accounts" -> {
                storageService.saveAccounts(accounts.values());
                auditService.logAction("saveAccounts");
            }
            case "transactions" -> {
                storageService.saveTransactions(transactions);
                auditService.logAction("saveTransactions");
            }
            case "cards" -> {
                storageService.saveCards(cards);
                auditService.logAction("saveCards");
            }
            default -> {
                auditService.logAction("Wrong_save_data");
                logger.error("Wrong call to save data");
            }
        }

    }
}
