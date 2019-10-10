package com.nerdylegend.repository.implementation;

import com.nerdylegend.model.Account;
import com.nerdylegend.repository.TransactionRepository;
import org.joda.money.Money;

public class TransactionRepositoryImpl implements TransactionRepository {

    public boolean withdraw(Account account, Money money) {
        return false;
    }

    public boolean deposit(Account account, Money money) {
        return false;
    }

    public boolean transfer(Account account, Money money, long beneficiary) {
        return false;
    }
}
