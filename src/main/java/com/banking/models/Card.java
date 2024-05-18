package com.banking.models;

import java.util.Date;

public class Card {
    private String cardNumber;
    private Date expirationDate;
    private Account account;
    private boolean blocked;

    public Card(String cardNumber, Date expirationDate, Account account) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.account = account;
        this.blocked = false;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public Account getAccount() {
        return account;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void block() {
        this.blocked = true;
    }

    public void unblock() {
        this.blocked = false;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", expirationDate=" + expirationDate +
                ", blocked=" + blocked +
                '}';
    }
}
