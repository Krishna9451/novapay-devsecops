package com.bank.api.service;

import com.bank.api.model.Account;
import com.bank.api.repository.AccountRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
@Service
public class AccountService {
    
  private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
       if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Balance cannot be negative");
    }  
      return accountRepository.save(account);
    }
}
