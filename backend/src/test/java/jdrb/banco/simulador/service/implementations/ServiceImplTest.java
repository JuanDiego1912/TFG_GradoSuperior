package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.dao.TransactionDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.model.enums.AccountType;
import jdrb.banco.simulador.model.enums.CustomerStates;
import jdrb.banco.simulador.model.enums.TransactionStates;
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
        transactionService = new TransactionServiceImpl(transactionDAO, accountDAO);
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
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.getAccountById(""));
        assertEquals("Account ID cannot be null or empty", ex.getMessage());
    }

    @Test
    void updateAccount_invalidAccount_shouldThrow() {
        Account acc = new Account(null, null, 0, null, 0L);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.updateAccount(acc));
        assertEquals("Account or account ID cannot be null or empty", ex.getMessage());
    }

    @Test
    void deleteAccount_invalidId_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccount(""));
        assertEquals("Account ID cannot be null or empty", ex.getMessage());
    }

    @Test
    void deleteAccountForClient_nullIds_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccountForClient(null, null));
        assertEquals("Customer ID cannot be null or empty", ex.getMessage());
    }

    @Test
    void getAccountsByClient_valid_shouldReturnList() {
        Account a1 = new Account("A1", "C1", 100f, AccountType.SAVINGS, System.currentTimeMillis());
        when(accountDAO.getAccountsByClient("C1")).thenReturn(Arrays.asList(a1));

        List<Account> result = accountService.getAccountsByClient("C1");

        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).getId());
    }

    // ----------------------- CustomerService Tests -------------------------

    @Test
    void registerCustomer_valid_shouldSucceed() {
        Customer c = new Customer("C1", "Juan", "Perez", "12345678", "jperez@example.com", "643461329", "123456", System.currentTimeMillis(), CustomerStates.ACTIVE);
        when(customerDAO.registerCustomer(c)).thenReturn(true);

        assertTrue(customerService.registerCustomer(c));
        verify(customerDAO).registerCustomer(c);
    }

    @Test
    void registerCustomer_invalid_shouldThrow() {
        Customer c = new Customer(null, null, null, null, null, null, null,0L, null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(c));
        assertEquals("Customer ID cannot be null or empty", ex.getMessage());
    }

    @Test
    void getCustomerById_null_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerById(null));
        assertEquals("Customer ID cannot be null or empty", ex.getMessage());
    }

    @Test
    void deleteCustomer_validId_shouldSucceed() {
        when(customerDAO.deleteCustomer("C1")).thenReturn(true);

        assertTrue(customerService.deleteCustomer("C1"));
        verify(customerDAO).deleteCustomer("C1");
    }

    // ---------------------- TransactionService Tests -----------------------

    @Test
    void registerTransaction_valid_shouldSucceed() {
        Transaction t = new Transaction(
                "T1",
                "A1",
                "A2",
                500f,
                TransactionType.TRANSFER,
                System.currentTimeMillis(),
                TransactionStates.COMPLETED);

        Account originAccount = new Account("A1", "C1", 1000f, AccountType.SAVINGS, System.currentTimeMillis());
        when(accountDAO.getAccountById("A1")).thenReturn(originAccount);

        when(transactionDAO.registerTransaction(t)).thenReturn(true);

        assertTrue(transactionService.registerTransaction(t));
        verify(transactionDAO).registerTransaction(t);
    }

    @Test
    void registerTransaction_negativeAmount_shouldThrow() {
        Transaction t = new Transaction(
                "T1",
                "A1",
                "A2",
                -5f,
                TransactionType.TRANSFER,
                System.currentTimeMillis(),
                TransactionStates.PEND);

        Exception ex = assertThrows(RuntimeException.class, () -> transactionService.registerTransaction(t));
        assertEquals("Transaction amount must be greater than 0", ex.getMessage());
    }

    @Test
    void getTransactionsBetweenDates_invalidDates_shouldThrow() {
        Date now = new Date();
        Date before = new Date(now.getTime() - 10000);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsBetweenDates("A1", now, before)
        );
        assertEquals("From date cannot be after to date", ex.getMessage());
    }

    @Test
    void getTransactionById_null_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(null));
        assertEquals("Transaction ID cannot be null or empty", ex.getMessage());
    }
}
