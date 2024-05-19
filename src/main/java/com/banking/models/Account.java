package com.banking.models;

import com.banking.exceptions.InsufficientFundsException;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String accountId;
    protected double balance;
    protected Customer owner;
    protected List<Transaction> transactions;
    protected List<Card> cards;

    public Account(String accountId, Customer owner) {
        this.accountId = accountId;
        this.balance = 0.0;
        this.owner = owner;
        this.transactions = new ArrayList<>();
        this.cards = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public double getBalance() {
        return balance;
    }

    public Customer getOwner() {
        return owner;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Card> getCards() {
        return cards;
    }

    public abstract void deposit(double amount);

    public abstract void withdraw(double amount) throws InsufficientFundsException;

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", balance=" + balance +
                ", owner=" + owner.getId() +
                ", transactions=" + transactions.size() +
                ", cards=" + cards.stream().map(Card::getCardNumber).toList() +
                '}';
    }
}
