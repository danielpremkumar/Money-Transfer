package com.nerdylegend.helper;

import com.nerdylegend.model.Account;
import io.vertx.core.json.Json;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class ValidationHelper {
    private static final Predicate<BigDecimal> LESSER_THAN_ZERO = i -> i.compareTo(BigDecimal.ZERO) <= 0;
    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
    private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain; charset=utf-8";
    private static final String CONTENT_TYPE = "content-type";

    private ValidationHelper() {
    }

    public static boolean validateWithdrawal(BigDecimal amount, CurrencyUnit currencyCode, Account account) {
        return !LESSER_THAN_ZERO.test(amount) && accountHasSufficientBalance(amount, account)
                && checkCurrencyCode(currencyCode, account);
    }

    private static boolean checkCurrencyCode(CurrencyUnit currencyCode, Account account) {
        return (currencyCode != null && account.getBalance().getCurrencyUnit() == currencyCode) ;
    }

    private static boolean accountHasSufficientBalance(BigDecimal amount, Account account) {
        return (amount != null && account.getBalance().getAmount().compareTo(amount) >= 0);
    }

    public static boolean validateDeposit(BigDecimal amount, CurrencyUnit currencyCode, Account account) {
        return !LESSER_THAN_ZERO.test(amount) && checkCurrencyCode(currencyCode, account);
    }

    public static BigDecimal getAmount(RoutingContext routingContext) {
        BigDecimal amount = null;
        try {
            amount = new BigDecimal(routingContext.request().getParam("amount"));
        } catch (Exception e) {
            constructTextResponse(routingContext, 400, "Invalid currency format");
        }
        return amount;
    }

    public static CurrencyUnit getCurrency(RoutingContext routingContext) {
        CurrencyUnit currencyUnit = null;
        try {
            currencyUnit = CurrencyUnit.of(routingContext.request().getParam("currencyCode"));
        } catch (Exception e) {
            constructTextResponse(routingContext, 400, "Invalid currency code");
        }
        return currencyUnit;
    }

    public static void constructJsonResponse(RoutingContext routingContext, int statusCode, Object serializableObject) {
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8)
                .end(Json.encodePrettily(serializableObject));
    }

    public static void constructTextResponse(RoutingContext routingContext, int statusCode, String message) {
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader(CONTENT_TYPE, TEXT_PLAIN_CHARSET_UTF_8)
                .end(message);
    }

}
