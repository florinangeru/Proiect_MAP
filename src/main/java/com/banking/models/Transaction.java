package com.banking.models;

import java.util.Date;
import com.banking.enums.TransactionType;

public class Transaction {
    private String transactionId;
    private Date timestamp;
    private double amount;
    private TransactionType type;
    private Account account;

    public Transaction(String transactionId, double amount, TransactionType type, Account account) {
        this.transactionId = transactionId;
        this.timestamp = new Date();
        this.amount = amount;
        this.type = type;
        this.account = account;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", type=" + type +
                ", account=" + account +
                '}';
    }
}
