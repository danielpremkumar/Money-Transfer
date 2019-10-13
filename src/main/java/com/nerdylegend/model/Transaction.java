package com.nerdylegend.model;

import org.joda.money.Money;

import java.util.Date;

enum Transaction_Type {
    WITHDRAWAL, DEPOSIT, TRANSFER
}

public class Transaction {
    private Date transactionDate;
    private String transactionId;
    private Transaction_Type type;
    private Long fromUserAccount;
    private Long toUserAccount;
    private Money amount;

}
