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
        Account account = buildAccount(1L, 1L);
        when(accountDAO.registerAccount(account)).thenReturn(true);

        assertTrue(accountService.registerAccount(account));
    }

    @Test
    void registerAccount_null_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.registerAccount(null));
    }

    @Test
    void registerAccount_generatesAccountNumberIfMissing() {
        Account account = buildAccount(2L, 1L);
        account.setAccountNumber(null);
        when(accountDAO.registerAccount(any(Account.class))).thenReturn(true);

        boolean result = accountService.registerAccount(account);
        assertTrue(result);
        assertNotNull(account.getAccountNumber());
        assertTrue(account.getAccountNumber().startsWith("ES"));
    }

    @Test
    void getAccountById_valid_returnsAccount() {
        Account account = buildAccount(1L, 1L);
        when(accountDAO.getAccountById(1L)).thenReturn(account);

        Account result = accountService.getAccountById(1L);
        assertEquals(account, result);
    }

    @Test
    void getAccountsByClient_valid_returnsList() {
        List<Account> accounts = Arrays.asList(
                buildAccount(1L, 1L),
                buildAccount(2L, 1L)
        );
        when(accountDAO.getAccountsByClient(1L)).thenReturn(accounts);

        assertEquals(accounts, accountService.getAccountsByClient(1L));
    }

    @Test
    void updateAccount_valid_returnsTrue() {
        Account account = buildAccount(3L, 2L);
        when(accountDAO.updateAccount(account)).thenReturn(true);

        assertTrue(accountService.updateAccount(account));
    }

    @Test
    void updateAccount_invalid_throwsException() {
        Account account = new Account(); // missing id
        assertThrows(IllegalArgumentException.class, () -> accountService.updateAccount(account));
    }

    @Test
    void deleteAccount_accountWithZeroBalance_returnsTrue() {
        Account account = buildAccount(4L, 2L);
        account.setBalance(0.0f);

        when(accountDAO.getAccountById(4L)).thenReturn(account);
        when(accountDAO.deleteAccount(4L)).thenReturn(true);

        assertTrue(accountService.deleteAccount(4L));
    }

    @Test
    void deleteAccount_accountWithBalance_throwsException() {
        Account account = buildAccount(5L, 2L);
        account.setBalance(500.0f);

        when(accountDAO.getAccountById(5L)).thenReturn(account);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> accountService.deleteAccount(5L));
        assertEquals("Cannot delete account with active balance: 500.0", ex.getMessage());
    }

    @Test
    void deleteAccount_accountNotFound_throwsException() {
        when(accountDAO.getAccountById(6L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccount(6L));
        assertEquals("Account not found with ID: 6", ex.getMessage());
    }

    @Test
    void deleteAccountForClient_valid_returnsTrue() {
        Account account = buildAccount(7L, 3L);
        account.setBalance(0.0f);

        when(accountDAO.getAccountById(7L)).thenReturn(account);
        when(accountDAO.deleteAccountForClient(3L, 7L)).thenReturn(true);

        assertTrue(accountService.deleteAccountForClient(3L, 7L));
    }

    @Test
    void deleteAccountForClient_accountWithBalance_throwsException() {
        Account account = buildAccount(8L, 3L);
        account.setBalance(100.0f);

        when(accountDAO.getAccountById(8L)).thenReturn(account);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> accountService.deleteAccountForClient(3L, 8L));
        assertEquals("Cannot delete account with active balance: 100.0", ex.getMessage());
    }

    private Account buildAccount(Long id, Long customerId) {
        Account account = new Account();
        account.setId(id);
        account.setCustomerId(customerId);
        account.setBalance(100.0f);
        account.setAccountType(AccountType.SAVINGS);
        account.setCreationDate(System.currentTimeMillis());
        account.setAccountNumber("ES1234567890123456789012");
        return account;
    }
}
