package com.banking.models;

import com.banking.exceptions.InsufficientFundsException;
import com.banking.enums.TransactionType;

public class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(String accountId, Customer owner, double interestRate) {
        super(accountId, owner);
        this.interestRate = interestRate;
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

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        double interest = balance * interestRate / 100;
        balance += interest;
        transactions.add(new Transaction("T" + transactions.size(), interest, TransactionType.DEPOSIT, this));
    }
}
