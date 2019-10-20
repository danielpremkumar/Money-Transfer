package com.nerdylegend.repository.implementation;

import com.nerdylegend.model.Account;
import com.nerdylegend.repository.AccountRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryTest {

    private AccountRepository accountRepository = new AccountRepositoryImpl();

    private String fromAccountId;

    private Account fromAccount;

    @Before
    public void setUp() {
        Money money = Money.of(CurrencyUnit.USD, new BigDecimal(22));
        fromAccount = accountRepository.createAccount("Dan", CurrencyUnit.USD);
        fromAccount.setBalance(money);
        fromAccountId = fromAccount.getAccountNumber();
    }
    @Test
    public void testCreateAccount() {
        // when
        Account account  = accountRepository.createAccount("Mike", CurrencyUnit.USD);

        // then
        Assert.assertEquals("Mike", account.getName());
        Assert.assertEquals("0.00 USD", account.getBalanceAmount());
    }

    @Test
    public void testGetAccount() {
        // when
        Optional<Account> optional  = accountRepository.getAccount(fromAccountId);

        // then
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals("Dan", optional.get().getName());
        Assert.assertEquals("22.00 USD", optional.get().getBalanceAmount());
    }

    @Test
    public void testGetAll() {
        // when
        List<Account> accounts  = accountRepository.getAll();

        // then
        Assert.assertFalse(accounts.isEmpty());
        Assert.assertEquals(1, accounts.size());
    }

    @Test
    public void testDeleteAll() {
        // when
        accountRepository.deleteAllAccount();

        // then
        Assert.assertTrue(accountRepository.getAll().isEmpty());
        Assert.assertEquals(0, accountRepository.getAll().size());
    }

    @Test
    public void testDeleteAccount() {
        // when
        accountRepository.deleteAccount(fromAccountId);

        // then
        Assert.assertTrue(accountRepository.getAll().isEmpty());
        Assert.assertEquals(0, accountRepository.getAll().size());
    }

    @Test
    public void testWithdrawal() {
        // when
        Money money = Money.of(CurrencyUnit.USD, new BigDecimal(2));
        accountRepository.withdraw(fromAccount, money);

        // then
        Assert.assertTrue(accountRepository.getAccount(fromAccountId).isPresent());
        Assert.assertEquals("20.00 USD", accountRepository.getAccount(fromAccountId).get().getBalanceAmount());
    }

    @Test
    public void testDeposit() {
        // when
        Money money = Money.of(CurrencyUnit.USD, new BigDecimal(2));
        accountRepository.deposit(fromAccount, money);

        // then
        Assert.assertTrue(accountRepository.getAccount(fromAccountId).isPresent());
        Assert.assertEquals("24.00 USD", accountRepository.getAccount(fromAccountId).get().getBalanceAmount());
    }

    @Test
    public void testTransfer() {
        // when
        Money money = Money.of(CurrencyUnit.USD, new BigDecimal(2));
        Account account  = accountRepository.createAccount("Mike", CurrencyUnit.USD);
        accountRepository.transfer(fromAccount, money, account.getAccountNumber());

        // then
        Assert.assertTrue(accountRepository.getAccount(fromAccountId).isPresent());
        Assert.assertEquals("20.00 USD", accountRepository.getAccount(fromAccountId).get().getBalanceAmount());
        Assert.assertTrue(accountRepository.getAccount(account.getAccountNumber()).isPresent());
        Assert.assertEquals("2.00 USD", accountRepository.getAccount(account.getAccountNumber()).get().getBalanceAmount());
    }
}
