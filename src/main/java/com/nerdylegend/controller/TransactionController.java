package com.nerdylegend.controller;

import com.nerdylegend.repository.TransactionRepository;
import com.nerdylegend.repository.implementation.TransactionRepositoryImpl;

public class TransactionController {

    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();

}
