package com.nerdylegend.repository.implementation;

import com.nerdylegend.model.Transaction;
import com.nerdylegend.repository.TransactionRepository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TransactionRepositoryImpl implements TransactionRepository {

    private final BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>();

}
