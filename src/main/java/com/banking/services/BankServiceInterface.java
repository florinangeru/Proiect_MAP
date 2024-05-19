package com.banking.services;

import com.banking.enums.AccountType;
import com.banking.exceptions.InsufficientFundsException;
import com.banking.exceptions.InvalidAccountException;
import com.banking.models.*;

import java.util.Date;
import java.util.List;

public interface BankServiceInterface {
    void createCustomer(String name, String surname, int age);
    void createAccount(String customerId, AccountType type) throws InvalidAccountException;
    List<Account> getAccountsByCustomer(String customerId) throws InvalidAccountException;
    BankStatement generateBankStatement(String accountId, Date startDate, Date endDate) throws InvalidAccountException;
    List<Customer> getAllCustomers();
    List<Account> getAllAccounts();
    Customer getCustomerById(String customerId) throws InvalidAccountException;
    void updateCustomer(Customer customer);
    void deleteCustomer(String customerId) throws InvalidAccountException;
    void deleteAccount(String accountId) throws InvalidAccountException;
    void addCard(Card card);
    void removeCard(Card card);
    void transfer(String fromAccountId, String toAccountId, double amount) throws InsufficientFundsException, InvalidAccountException;
    void withdraw(String accountId, double amount) throws InsufficientFundsException, InvalidAccountException;
    void deposit(String accountId, double amount) throws InvalidAccountException;
    Account getAccountById(String accountId);
}
