package com.nerdylegend.controller;

import com.nerdylegend.helper.ValidationHelper;
import com.nerdylegend.model.Account;
import com.nerdylegend.repository.AccountRepository;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    @Spy
    private AccountController accountController = new AccountController();

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RoutingContext routingContext;

    @Mock
    private HttpServerRequest request;

    @Mock
    private HttpServerResponse response;

    private Account account;

    @Mock
    private ValidationHelper validationHelper;

    @Before
    public void setUp() {
        account = new Account("Mike", CurrencyUnit.USD);
        when(accountController.getAccountRepository()).thenReturn(accountRepository);
        when(routingContext.request()).thenReturn(request);
        when(routingContext.response()).thenReturn(response);
        when(response.putHeader(anyString(), anyString())).thenReturn(response);
        when(response.setStatusCode(anyInt())).thenReturn(response);
    }

    @Test
    public void testDeleteAccount() {
        //Given
        when(request.getParam(AccountController.ID)).thenReturn("1");
        when(accountRepository.getAccount("1")).thenReturn(Optional.of(account));

        // when
        accountController.deleteAccount(routingContext);

        // then
        verify(accountRepository, times(1)).deleteAccount(anyString());
    }

    @Test
    public void testCreateAccount() {
        //Given
        when(request.getParam("name")).thenReturn("Mike");
        when(request.getParam("currencyCode")).thenReturn("USD");

        // when
        accountController.createAccount(routingContext);

        // then
        verify(accountRepository, times(1)).createAccount("Mike", CurrencyUnit.USD);
    }

    @Test
    public void testGetAccount() {
        //Given
        when(request.getParam(AccountController.ID)).thenReturn("1");

        // when
        accountController.getAccount(routingContext);

        // then
        verify(accountRepository, times(1)).getAccount("1");
    }

    @Test
    public void testDeleteAll() {
        // when
        accountController.deleteAll(routingContext);

        // then
        verify(accountRepository, times(1)).deleteAllAccount();
    }

    @Test
    public void testWithdrawal() {
        // given
        when(request.getParam(AccountController.ID)).thenReturn("1");
        when(request.getParam("amount")).thenReturn("2.0");
        Money money = Money.of(CurrencyUnit.USD, new BigDecimal(22));
        account.setBalance(money);
        when(request.getParam("currencyCode")).thenReturn("USD");
        when(accountRepository.getAccount("1")).thenReturn(Optional.of(account));

        // when
        accountController.withdraw(routingContext);

        // then
        verify(accountRepository, times(1)).withdraw(any(account.getClass()), any(Money.class));
    }

    @Test
    public void testDeposit() {
        // given
        when(request.getParam(AccountController.ID)).thenReturn("1");
        when(request.getParam("amount")).thenReturn("2.0");
        Money money = Money.of(CurrencyUnit.USD, new BigDecimal(22));
        when(request.getParam("currencyCode")).thenReturn("USD");
        when(accountRepository.getAccount("1")).thenReturn(Optional.of(account));
        account.setBalance(money);

        // when
        accountController.deposit(routingContext);

        // then
        verify(accountRepository, times(1)).deposit(any(account.getClass()), any(Money.class));
    }

    @Test
    public void testTransfer() {
        // given
        when(request.getParam(AccountController.ID)).thenReturn("1");
        when(request.getParam("toAccountNumber")).thenReturn("3");
        when(request.getParam("amount")).thenReturn("2.0");
        Money money = Money.of(CurrencyUnit.USD, new BigDecimal(22));
        when(request.getParam("currencyCode")).thenReturn("USD");
        account.setBalance(money);
        when(accountRepository.getAccount("1")).thenReturn(Optional.of(account));

        // when
        accountController.transfer(routingContext);

        // then
        verify(accountRepository, times(1)).transfer(any(account.getClass()), any(Money.class), anyString());
    }

    @Test
    public void testGetAll() {
        // when
        accountController.getAll(routingContext);

        // then
        verify(accountRepository, times(1)).getAll();
    }
}
