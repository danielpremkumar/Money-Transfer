package com.nerdylegend.repository;

import com.nerdylegend.model.Account;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    //Operations
    Account createAccount(String name, CurrencyUnit currencyUnit);
    Optional<Account> getAccount(String accountNumber);
    void deleteAccount(String accountNumber);
    List<Account> getAll();
    void deleteAllAccount();

    //Transactions
    void withdraw(Account account, Money money);
    boolean deposit(Account account, Money money);
    boolean transfer(Account account, Money money, String beneficiary);

}
