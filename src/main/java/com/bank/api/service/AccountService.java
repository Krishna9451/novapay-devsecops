package com.bank.api.service;

import com.bank.api.model.Account;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    public Account createAccount(Account account) {
       if (account.getBalance() < 0) {
        throw new IllegalArgumentException("Balance cannot be negative");
    }  
      return account;
    }
}
