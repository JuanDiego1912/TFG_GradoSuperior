package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.dao.TransactionDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.model.enums.AccountType;
import jdrb.banco.simulador.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceImplTest {

    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private TransactionDAO transactionDAO;

    private AccountServiceImpl accountService;
    private CustomerServiceImpl customerService;
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setup() {
        accountDAO = mock(AccountDAO.class);
        customerDAO = mock(CustomerDAO.class);
        transactionDAO = mock(TransactionDAO.class);

        accountService = new AccountServiceImpl(accountDAO);
        customerService = new CustomerServiceImpl(customerDAO);
        transactionService = new TransactionServiceImpl(transactionDAO);
    }

    // ----------------------- AccountService Tests -------------------------

    @Test
    void registerAccount_validAccount_shouldSucceed() {
        Account acc = new Account("A1", "C1", 100.0f, AccountType.SAVINGS, System.currentTimeMillis());
        when(accountDAO.registerAccount(acc)).thenReturn(true);
        assertTrue(accountService.registerAccount(acc));
        verify(accountDAO).registerAccount(acc);
    }

    @Test
    void registerAccount_nullAccount_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.registerAccount(null));
        assertEquals("Account cannot be null or empty", ex.getMessage());
    }

    @Test
    void getAccountById_emptyId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountById(""));
    }

    @Test
    void updateAccount_invalidAccount_shouldThrow() {
        Account acc = new Account(null, null, 0, null, 0L);
        assertThrows(IllegalArgumentException.class, () -> accountService.updateAccount(acc));
    }

    // ----------------------- CustomerService Tests -------------------------

    @Test
    void registerCustomer_valid_shouldSucceed() {
        Customer c = new Customer("C1", "Juan", "Perez", "12345678", "jperez@example.com",  "643461329");
        when(customerDAO.registerCustomer(c)).thenReturn(true);
        assertTrue(customerService.registerCustomer(c));
        verify(customerDAO).registerCustomer(c);
    }

    @Test
    void registerCustomer_invalid_shouldThrow() {
        Customer c = new Customer(null, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(c));
    }

    @Test
    void getCustomerById_null_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerById(null));
    }

    // ---------------------- TransactionService Tests -----------------------

    @Test
    void registerTransaction_valid_shouldSucceed() {
        Transaction t = new Transaction("T1", "A1", "A2", 500f, TransactionType.TRANSFER, System.currentTimeMillis());
        when(transactionDAO.registerTransaction(t)).thenReturn(true);
        assertTrue(transactionService.registerTransaction(t));
        verify(transactionDAO).registerTransaction(t);
    }

    @Test
    void registerTransaction_negativeAmount_shouldThrow() {
        Transaction t = new Transaction("T1", "A1", "A2", -5f, TransactionType.TRANSFER, System.currentTimeMillis());
        assertThrows(RuntimeException.class, () -> transactionService.registerTransaction(t));
    }

    @Test
    void getTransactionsBetweenDates_invalidDates_shouldThrow() {
        Date now = new Date();
        Date before = new Date(now.getTime() - 10000);
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsBetweenDates("A1", now, before)
        );
    }

    @Test
    void getTransactionById_null_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(null));
    }

    // ---------------- General validations -------------------

    @Test
    void deleteCustomer_validId_shouldSucceed() {
        when(customerDAO.deleteCustomer("C1")).thenReturn(true);
        assertTrue(customerService.deleteCustomer("C1"));
    }

    @Test
    void deleteAccount_invalidId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccount(""));
    }

    @Test
    void deleteAccountForClient_nullIds_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccountForClient(null, null));
    }

    @Test
    void getAccountsByClient_valid_shouldReturnList() {
        Account a1 = new Account("A1", "C1", 100f, AccountType.SAVINGS, System.currentTimeMillis());
        when(accountDAO.getAccountsByClient("C1")).thenReturn(Arrays.asList(a1));
        List<Account> result = accountService.getAccountsByClient("C1");
        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).getId());
    }
}
