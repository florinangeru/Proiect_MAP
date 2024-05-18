package com.banking.services;

import com.banking.models.*;

import java.io.*;
import java.util.*;

public class StorageService {
    private static StorageService instance = null;

    private StorageService() { }

    public static synchronized StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
        }
        return instance;
    }

    public void loadCustomers() {
        // Load customers from CSV file
    }

    public void saveCustomers(List<Customer> customers) {
        // Save customers to CSV file
    }

    public void loadAccounts() {
        // Load accounts from CSV file
    }

    public void saveAccounts(List<Account> accounts) {
        // Save accounts to CSV file
    }

    public void loadTransactions() {
        // Load transactions from CSV file
    }

    public void saveTransactions(List<Transaction> transactions) {
        // Save transactions to CSV file
    }

    public void loadCards() {
        // Load cards from CSV file
    }

    public void saveCards(List<Card> cards) {
        // Save cards to CSV file
    }
}
