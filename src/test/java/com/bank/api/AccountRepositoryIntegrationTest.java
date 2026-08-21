package com.bank.api;

import com.bank.api.model.Account;
import com.bank.api.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountRepositoryIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldSaveAndReadAccount() {

        Account account = new Account();

        account.setId(100L);
        account.setCustomerName("Integration Test");
        account.setBalance(new BigDecimal("5000.00"));

        accountRepository.save(account);

        Account savedAccount =
                accountRepository.findById(100L).orElseThrow();

        assertEquals("Integration Test", savedAccount.getCustomerName());
        assertEquals(
                new BigDecimal("5000.00"),
                savedAccount.getBalance()
        );
    }
}
