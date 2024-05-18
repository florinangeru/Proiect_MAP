package com.banking.models;

import java.util.Date;
import java.util.List;

public class BankStatement {
    private String statementId;
    private Account account;
    private Date startDate;
    private Date endDate;
    private List<Transaction> transactions;
    private double closingBalance;

    public BankStatement(String statementId, Account account, Date startDate, Date endDate, List<Transaction> transactions, double closingBalance) {
        this.statementId = statementId;
        this.account = account;
        this.startDate = startDate;
        this.endDate = endDate;
        this.transactions = transactions;
        this.closingBalance = closingBalance;
    }

    public String getStatementId() {
        return statementId;
    }

    public Account getAccount() {
        return account;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    @Override
    public String toString() {
        return "BankStatement{" +
                "statementId='" + statementId + '\'' +
                ", account=" + account +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", transactions=" + transactions +
                ", closingBalance=" + closingBalance +
                '}';
    }
}
