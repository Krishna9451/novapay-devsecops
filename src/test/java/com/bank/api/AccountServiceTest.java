package com.bank.api;

import com.bank.api.model.Account;
import com.bank.api.service.AccountService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import com.bank.api.repository.AccountRepository;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Test
    void shouldCreateAccountWithValidBalance() {

        AccountRepository repository = mock(AccountRepository.class);
        AccountService service = new AccountService(repository);

        Account account = new Account();
        account.setId(1L);
        account.setCustomerName("Krishna");
        account.setBalance(new BigDecimal("10000"));
           when(repository.save(account)).thenReturn(account);            

       Account result = service.createAccount(account);

        assertEquals(new BigDecimal("10000"), result.getBalance());
    }

    @Test
    void shouldRejectNegativeBalance() {

        AccountRepository repository = mock(AccountRepository.class);
    AccountService service = new AccountService(repository);

        Account account = new Account();
        account.setId(2L);
        account.setCustomerName("Rahul");
        account.setBalance(new BigDecimal("-500"));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createAccount(account)
        );
    }
}
