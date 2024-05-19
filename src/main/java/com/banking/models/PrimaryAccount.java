package com.banking.models;

import com.banking.exceptions.InsufficientFundsException;
import com.banking.enums.TransactionType;

public class PrimaryAccount extends Account {
    public PrimaryAccount(String accountId, Customer owner) {
        super(accountId, owner);
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add(new Transaction("T" + transactions.size(), amount, TransactionType.DEPOSIT, this));
        }
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction("T" + transactions.size(), amount, TransactionType.WITHDRAWAL, this));
        } else {
            throw new InsufficientFundsException("Insufficient funds for withdrawal.");
        }
    }

    @Override
    public String toString() {
        return "PrimaryAccount{" +
                "accountId='" + accountId + '\'' +
                ", balance=" + balance +
                ", owner=" + owner.getId() +
                ", transactions=" + transactions.size() +
                ", cards=" + cards.stream().map(Card::getCardNumber).toList() +
                '}';
    }
}
