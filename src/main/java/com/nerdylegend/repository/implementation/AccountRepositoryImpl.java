package com.nerdylegend.repository.implementation;

import com.nerdylegend.model.Account;
import com.nerdylegend.repository.AccountRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepositoryImpl implements AccountRepository {
    private Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account createAccount(String name, CurrencyUnit currencyUnit) {
        Account account = new Account(name, currencyUnit);
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    @Override
    public Optional<Account> getAccount(String accountNumber) {
        try {
            return Optional.of(accounts.get(accountNumber));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteAccount(String accountNumber) {
        accounts.remove(accountNumber);
    }

    @Override
    public List<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public void withdraw(Account account, Money money) {
        account.setBalance(account.getBalance().minus(money));
    }

    @Override
    public boolean deposit(Account account, Money money) {
        account.setBalance(account.getBalance().plus(money));
        return true;
    }

    @Override
    public boolean transfer(Account account, Money money, String beneficiary) {
        Optional<Account> toOptional = getAccount(beneficiary);
        if (toOptional.isPresent()) {
            Account toAccount = toOptional.get();
            if (toAccount.getBalance().getCurrencyUnit() == money.getCurrencyUnit()) {
                withdraw(account, money);
                return deposit(toOptional.get(), money);
            }
        }
        return false;
    }

}
