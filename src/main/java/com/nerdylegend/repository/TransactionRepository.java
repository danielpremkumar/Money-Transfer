package com.nerdylegend.repository;

import com.nerdylegend.model.Account;
import org.joda.money.Money;

public interface TransactionRepository {
    boolean withdraw(Account account, Money money);
    boolean deposit(Account account, Money money);
    boolean transfer(Account account, Money money, long beneficiary);
}
