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
        customerService = new CustomerServiceImpl(customerDAO, accountDAO);
        transactionService = new TransactionServiceImpl(transactionDAO, accountDAO);
    }

    // ----------------------- AccountService Tests -------------------------

    @Test
    void registerAccount_validAccount_shouldSucceed() {
        Account acc = new Account(
                1L,
                "ES12345678901234567890",
                1L,
                100.0f,
                AccountType.SAVINGS,
                System.currentTimeMillis()
        );

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
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.getAccountById(null));
        assertEquals("Account ID must be a positive number", ex.getMessage());
    }

    @Test
    void updateAccount_invalidAccount_shouldThrow() {
        Account acc = new Account(null, null, null, 0, null, 0L);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.updateAccount(acc));
        assertEquals("Account or account ID cannot be null or empty", ex.getMessage());
    }

    @Test
    void deleteAccount_invalidId_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccount(null));
        assertEquals("Account ID must be a positive number", ex.getMessage());
    }

    @Test
    void deleteAccountForClient_nullIds_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.deleteAccountForClient(null, null));
        assertEquals("Customer ID must be a positive number", ex.getMessage());
    }

    @Test
    void getAccountsByClient_valid_shouldReturnList() {
        Account a1 = new Account(
                1L,
                "ES12345678901234567890",
                1L,
                100f,
                AccountType.SAVINGS,
                System.currentTimeMillis()
        );
        when(accountDAO.getAccountsByClient(1L)).thenReturn(Arrays.asList(a1));

        List<Account> result = accountService.getAccountsByCustomerId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    // ----------------------- CustomerService Tests -------------------------

    @Test
    void registerCustomer_valid_shouldSucceed() {
        Customer c = new Customer(
                1L,
                "Juan",
                "Perez",
                "12345678A",
                "jperez@example.com",
                "643461329",
                "mypassword",
                System.currentTimeMillis(),
                CustomerStates.ACTIVE
        );

        // Cuando registerCustomer es llamado, devuelve true
        when(customerDAO.registerCustomer(any(Customer.class))).thenReturn(true);
        // Cuando registerAccount es llamado, devuelve true (porque se crea cuenta para el cliente)
        when(accountDAO.registerAccount(any(Account.class))).thenReturn(true);

        // Ejecutamos el método bajo test
        boolean result = customerService.registerCustomer(c);

        // Verificamos que devolvió true
        assertTrue(result);

        // Verificamos que se llamó al DAO para registrar el cliente
        verify(customerDAO).registerCustomer(any(Customer.class));

        // Verificamos que se llamó al DAO para registrar la cuenta (cuenta creada automáticamente)
        verify(accountDAO).registerAccount(any(Account.class));
    }

    @Test
    void registerCustomer_invalid_shouldThrow() {
        Customer c = new Customer(100L, null, null, null, null, null, null, 0L, null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(c));
        assertEquals("Email cannot be null or empty", ex.getMessage());
    }

    @Test
    void getCustomerById_null_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerById(null));
        assertEquals("Customer ID must be a positive number", ex.getMessage());
    }

    @Test
    void deleteCustomer_validId_shouldSucceed() {
        when(customerDAO.deleteCustomer(1L)).thenReturn(true);

        assertTrue(customerService.deleteCustomer(1L));
        verify(customerDAO).deleteCustomer(1L);
    }

    // ---------------------- TransactionService Tests -----------------------

    @Test
    void registerTransaction_valid_shouldSucceed() {
        Transaction t = new Transaction(
                1L,
                1L,
                2L,
                500f,
                TransactionType.TRANSFER,
                System.currentTimeMillis(),
                TransactionStates.COMPLETED
        );

        Account originAccount = new Account(
                1L,
                "ES98765432109876543210",
                1L,
                1000f,
                AccountType.SAVINGS,
                System.currentTimeMillis()
        );

        Account destinationAccount = new Account(
                2L,
                "ES12345678901234567890",
                2L,
                500f,
                AccountType.SAVINGS,
                System.currentTimeMillis()
        );

        when(accountDAO.getAccountById(1L)).thenReturn(originAccount);
        when(accountDAO.getAccountById(2L)).thenReturn(destinationAccount);
        when(transactionDAO.registerTransaction(t)).thenReturn(true);

        assertTrue(transactionService.registerTransaction(t));
        verify(transactionDAO).registerTransaction(t);
    }

    @Test
    void registerTransaction_negativeAmount_shouldThrow() {
        Transaction t = new Transaction(
                1L,
                1L,
                2L,
                -5f,
                TransactionType.TRANSFER,
                System.currentTimeMillis(),
                TransactionStates.PEND
        );

        Exception ex = assertThrows(RuntimeException.class, () -> transactionService.registerTransaction(t));
        assertEquals("Transaction amount must be greater than 0", ex.getMessage());
    }

    @Test
    void getTransactionsBetweenDates_invalidDates_shouldThrow() {
        Date now = new Date();
        Date before = new Date(now.getTime() - 10000);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsBetweenDates(1L, now, before)
        );
        assertEquals("From date cannot be after to date", ex.getMessage());
    }

    @Test
    void getTransactionById_null_shouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(null));
        assertEquals("Transaction ID must be a positive number", ex.getMessage());
    }
}
