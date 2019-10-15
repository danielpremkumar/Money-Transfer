package com.nerdylegend.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.time.LocalDateTime;
import java.util.Random;

public class Account {

    private String name;
    private String accountNumber;
    private Money balance;
    private LocalDateTime creationDate;

    public Account(String name, CurrencyUnit currencyUnit) {
        this.name = name;
        long val = new Random().nextLong();
        if (val < 0) val *= -1;
        this.accountNumber = String.valueOf(val);
        creationDate = LocalDateTime.now();
        balance = Money.zero(currencyUnit);
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    @JsonGetter("balance")
    public String getBalanceAmount() {
        return balance.getAmount() + " " + balance.getCurrencyUnit();
    }

    @JsonGetter("creationDate")
    public String getAccountCreationDate() {
        return creationDate.toLocalDate().toString();
    }

}
