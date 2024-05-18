package com.banking.services;

import com.banking.enums.AccountType;
import com.banking.enums.TransactionType;
import com.banking.exceptions.InsufficientFundsException;
import com.banking.exceptions.InvalidAccountException;
import com.banking.models.*;

import java.util.*;

public class BankService implements BankServiceInterface {
    private Map<String, Customer> customers = new HashMap<>();
    private Map<String, Account> accounts = new HashMap<>();

    @Override
    public void createCustomer(String name, String surname, int age) {
        String customerId = UUID.randomUUID().toString();
        Customer customer = new Customer(customerId, name, surname, age);
        customers.put(customerId, customer);
    }

    @Override
    public void createAccount(String customerId, AccountType type) throws InvalidAccountException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new InvalidAccountException("Customer not found");
        }

        String accountId = UUID.randomUUID().toString();
        Account account;
        if (type == AccountType.PRIMARY) {
            account = new PrimaryAccount(accountId, customer);
        } else if (type == AccountType.SAVINGS) {
            account = new SavingsAccount(accountId, customer, 1.5); // Assuming 1.5% interest rate
        } else {
            throw new InvalidAccountException("Invalid account type");
        }

        accounts.put(accountId, account);
        customer.addAccount(account);
    }

    @Override
    public void makeTransaction(String accountId, double amount, String transactionType) throws InsufficientFundsException, InvalidAccountException {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found");
        }

        if (TransactionType.valueOf(transactionType) == TransactionType.DEPOSIT) {
            account.deposit(amount);
        } else if (TransactionType.valueOf(transactionType) == TransactionType.WITHDRAWAL) {
            account.withdraw(amount);
        } else {
            throw new InvalidAccountException("Invalid transaction type");
        }
    }

    @Override
    public List<Account> getAccountsByCustomer(String customerId) throws InvalidAccountException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new InvalidAccountException("Customer not found");
        }
        return customer.getAccounts();
    }

    @Override
    public BankStatement generateBankStatement(String accountId, Date startDate, Date endDate) throws InvalidAccountException {
        Account account = accounts.get(accountId);
        if (account == null) {
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
            throw new InvalidAccountException("Customer not found");
        }
        return customer;
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    public void deleteCustomer(String customerId) throws InvalidAccountException {
        Customer customer = customers.remove(customerId);
        if (customer == null) {
            throw new InvalidAccountException("Customer not found");
        }
    }

    @Override
    public void deleteAccount(String accountId) throws InvalidAccountException {
        Account account = accounts.remove(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found");
        }
        account.getOwner().getAccounts().remove(account);
    }
}
