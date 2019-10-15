package com.nerdylegend.controller;

import com.nerdylegend.helper.ValidationHelper;
import com.nerdylegend.model.Account;
import com.nerdylegend.repository.AccountRepository;
import com.nerdylegend.repository.implementation.AccountRepositoryImpl;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;

public class AccountController {

    private static final String ID = "id";
    private static final String ACCOUNT_DOES_NOT_EXIST = "account with accountNumber %s does not exist";
    private static final String ACCOUNT_DELETED = "account with accountNumber %s deleted";

    private final AccountRepository accountRepository = new AccountRepositoryImpl();

    public void createAccount(RoutingContext routingContext) {
        String userName = routingContext.request().getParam("name");
        CurrencyUnit currencyCode = ValidationHelper.getCurrency(routingContext);
        if (currencyCode != null) {
            Account user = accountRepository.createAccount(userName, currencyCode);
            ValidationHelper.constructJsonResponse(routingContext, 201, user);
        } else {
            ValidationHelper.constructTextResponse(routingContext, 400, "Invalid currency code");
        }
    }

    public void getAccount(RoutingContext routingContext) {
        accountLookupOperation(routingContext, account ->
                ValidationHelper.constructJsonResponse(routingContext, 200, account)
        );
    }

    public void deleteAccount(RoutingContext routingContext) {
        accountLookupOperation(routingContext, account -> {
            String accountNumber = String.valueOf(account.getAccountNumber());
            accountRepository.deleteAccount(accountNumber);
            String message = String.format(ACCOUNT_DELETED,
                    accountNumber);
            ValidationHelper.constructTextResponse(routingContext, 202, message);
        });
    }

    private void accountLookupOperation(RoutingContext routingContext, Consumer<Account> consumer) {
        String accountNumber = routingContext.request().getParam(ID);
        Optional<Account> accountMaybe = accountRepository.getAccount(accountNumber);
        if (accountMaybe.isPresent()) {
            consumer.accept(accountMaybe.get());
        } else {
            String message = String.format(ACCOUNT_DOES_NOT_EXIST,
                    accountNumber);
            ValidationHelper.constructTextResponse(routingContext, 404, message);
        }
    }


    public void withdraw(RoutingContext routingContext) {
        accountLookupOperation(routingContext, account -> {
            BigDecimal amount = ValidationHelper.getAmount(routingContext);
            CurrencyUnit currencyCode = ValidationHelper.getCurrency(routingContext);
            if (ValidationHelper.validateWithdrawal(amount, currencyCode, account)) {
                accountRepository.withdraw(account, Money.of(currencyCode, amount));
                ValidationHelper.constructJsonResponse(routingContext, 200, account);
                return;
            }
            ValidationHelper.constructTextResponse(routingContext, 400, "Insufficient funds or invalid currency format");
        });
    }


    public void deposit(RoutingContext routingContext) {
        accountLookupOperation(routingContext, account -> {
            BigDecimal amount = ValidationHelper.getAmount(routingContext);
            CurrencyUnit currencyCode = ValidationHelper.getCurrency(routingContext);
            if (ValidationHelper.validateDeposit(amount, currencyCode, account)) {
                accountRepository.deposit(account, Money.of(currencyCode, amount));
                ValidationHelper.constructJsonResponse(routingContext, 200, account);
                return;
            }
            ValidationHelper.constructTextResponse(routingContext, 400, "Invalid currency format");
        });
    }


    public void transfer(RoutingContext routingContext) {
        accountLookupOperation(routingContext, account -> {
            BigDecimal amount = ValidationHelper.getAmount(routingContext);
            CurrencyUnit currencyCode = ValidationHelper.getCurrency(routingContext);
            String toAccountNumber = routingContext.request().getParam("toAccountNumber");
            if (ValidationHelper.validateWithdrawal(amount, currencyCode, account)) {
                if (accountRepository.transfer(account, Money.of(currencyCode, amount), toAccountNumber)) {
                    ValidationHelper.constructJsonResponse(routingContext, 200, account);
                    return;
                }
                ValidationHelper.constructTextResponse(routingContext, 400, "Transaction failed");
                return;
            }
            ValidationHelper.constructTextResponse(routingContext, 400, "Insufficient funds or invalid currency format");
        });

    }

    public void getAll(RoutingContext routingContext) {
        ValidationHelper.constructJsonResponse(routingContext, 200, accountRepository.getAll());
    }

    public void deleteAll(RoutingContext routingContext) {
        accountRepository.deleteAllAccount();
        ValidationHelper.constructTextResponse(routingContext, 200, "All Account Deleted");
    }
}
