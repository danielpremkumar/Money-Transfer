package com.nerdylegend.model;

import org.joda.money.CurrencyUnit;
import org.junit.Assert;
import org.junit.Test;

public class AccountTest {

    @Test
    public void testAccount() {
        Account account = new Account("Daniel", CurrencyUnit.USD);
        Assert.assertEquals("Daniel", account.getName());
        Assert.assertNotNull(account.getBalance());
        Assert.assertNotNull(account.getAccountNumber());
        Assert.assertNotNull(account.getAccountCreationDate());
        Assert.assertEquals("0.00 USD", account.getBalanceAmount());

        account.setBalance(null);
        Assert.assertNull(account.getBalance());
    }
}
