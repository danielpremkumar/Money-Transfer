package com.nerdylegend.model;

import java.util.Date;

enum Transaction_Type {
    WITHDRAWAL, DEPOSIT, TRANSFER
}

public class Transaction {
    private Date transactionDate;
    private String transactionId;
    private Transaction_Type type;
    private Account fromUserAccount;
    private Account toUserAccount;
}
