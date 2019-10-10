package com.nerdylegend.repository;

import com.nerdylegend.model.Account;

public interface AccountRepository {
    Account createAccount(String name);
}
