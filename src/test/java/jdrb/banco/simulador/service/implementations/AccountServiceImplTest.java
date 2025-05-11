package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    private AccountDAO accountDAO;
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountDAO = mock(AccountDAO.class);
        accountService = new AccountServiceImpl(accountDAO);
    }

    @Test
    void registerAccount_valid_returnsTrue() {
        Account account = buildAccount("A1", "C1");
        when(accountDAO.registerAccount(account)).thenReturn(true);

        assertTrue(accountService.registerAccount(account));
    }

    @Test
    void registerAccount_null_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.registerAccount(null));
    }

    @Test
    void registerAccount_missingId_throwsException() {
        Account account = buildAccount(null, "C1");

        assertThrows(IllegalArgumentException.class, () -> accountService.registerAccount(account));
    }

    @Test
    void getAccountById_valid_returnsAccount() {
        Account account = buildAccount("A2", "C2");
        when(accountDAO.getAccountById("A2")).thenReturn(account);

        Account result = accountService.getAccountById("A2");
        assertEquals(account, result);
    }

    @Test
    void getAccountById_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountById(null));
    }

    @Test
    void getAccountsByClient_valid_returnsList() {
        List<Account> accounts = Arrays.asList(
                buildAccount("A1", "C1"),
                buildAccount("A2", "C1")
        );
        when(accountDAO.getAccountsByClient("C1")).thenReturn(accounts);

        assertEquals(accounts, accountService.getAccountsByClient("C1"));
    }

    @Test
    void getAccountsByClient_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountsByClient(null));
    }

    @Test
    void updateAccount_valid_returnsTrue() {
        Account account = buildAccount("A3", "C3");
        when(accountDAO.updateAccount(account)).thenReturn(true);

        assertTrue(accountService.updateAccount(account));
    }

    @Test
    void updateAccount_null_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.updateAccount(null));
    }

    @Test
    void deleteAccount_valid_returnsTrue() {
        Account account = buildAccount("A5", "C5");
        account.setBalance(0.0f);

        when(accountDAO.getAccountById("A5")).thenReturn(account);
        when(accountDAO.deleteAccount("A5")).thenReturn(true);

        assertTrue(accountService.deleteAccount("A5"));
    }


    @Test
    void deleteAccount_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccount(null));
    }

    @Test
    void deleteAccountForClient_valid_returnsTrue() {
        Account account = buildAccount("A5", "C5");
        account.setBalance(0.0f); // igual, cero balance

        when(accountDAO.getAccountById("A5")).thenReturn(account);
        when(accountDAO.deleteAccountForClient("C5", "A5")).thenReturn(true);

        assertTrue(accountService.deleteAccountForClient("C5", "A5"));
    }


    @Test
    void deleteAccountForClient_nullClient_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccountForClient(null, "A5"));
    }

    private Account buildAccount(String id, String customerId) {
        Account account = new Account();
        account.setId(id);
        account.setCustomerId(customerId);
        account.setBalance(100.0f);
        account.setAccountType(AccountType.SAVINGS);
        account.setCreationDate(System.currentTimeMillis());
        return account;
    }
}
