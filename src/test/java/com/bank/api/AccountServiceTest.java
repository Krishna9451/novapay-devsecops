package com.bank.api;

import com.bank.api.model.Account;
import com.bank.api.service.AccountService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    @Test
    void shouldCreateAccountWithValidBalance() {

        AccountService service = new AccountService();

        Account account = new Account();
        account.setId(1L);
        account.setCustomerName("Krishna");
        account.setBalance(10000);

        Account result = service.createAccount(account);

        assertEquals(10000, result.getBalance());
    }

    @Test
    void shouldRejectNegativeBalance() {

        AccountService service = new AccountService();

        Account account = new Account();
        account.setId(2L);
        account.setCustomerName("Rahul");
        account.setBalance(-500);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAccount(account)
        );
    }
}
