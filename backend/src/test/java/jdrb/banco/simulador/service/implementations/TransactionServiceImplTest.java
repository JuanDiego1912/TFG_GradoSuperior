package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.dao.TransactionDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.model.enums.AccountType;
import jdrb.banco.simulador.model.enums.TransactionStates;
import jdrb.banco.simulador.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionDAO = mock(TransactionDAO.class);
        accountDAO = mock(AccountDAO.class);
        transactionService = new TransactionServiceImpl(transactionDAO, accountDAO);
    }

    @Test
    void registerTransaction_valid_returnsTrue() {
        Transaction t = buildTransaction(1L, 100.0f);

        Account originAccount = new Account(
                1L,
                "ES12345678901234567890",
                1L,
                1000f,
                AccountType.SAVINGS,
                System.currentTimeMillis()
        );

        Account destinationAccount = new Account(
                2L,
                "ES98765432109876543210",
                2L,
                500f,
                AccountType.SAVINGS,
                System.currentTimeMillis()
        );

        when(accountDAO.getAccountById(1L)).thenReturn(originAccount);
        when(accountDAO.getAccountById(2L)).thenReturn(destinationAccount);
        when(transactionDAO.registerTransaction(t)).thenReturn(true);

        assertTrue(transactionService.registerTransaction(t));
    }

    @Test
    void registerTransaction_null_throwsException() {
        assertThrows(RuntimeException.class, () -> transactionService.registerTransaction(null));
    }

    @Test
    void registerTransaction_negativeAmount_throwsException() {
        Transaction t = buildTransaction(2L, -5.0f);
        assertThrows(RuntimeException.class, () -> transactionService.registerTransaction(t));
    }

    @Test
    void getTransactionById_valid_returnsTransaction() {
        Transaction t = buildTransaction(3L, 200.0f);
        when(transactionDAO.getTransactionById(3L)).thenReturn(t);
        assertEquals(t, transactionService.getTransactionById(3L));
    }

    @Test
    void getTransactionById_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(null));
    }

    @Test
    void getTransactionsBySourceAccount_valid_returnsList() {
        List<Transaction> list = Arrays.asList(buildTransaction(4L, 20f));
        when(transactionDAO.getTransactionsBySourceAccount(100L)).thenReturn(list);
        assertEquals(list, transactionService.getTransactionsBySourceAccount(100L));
    }

    @Test
    void getTransactionsByDestinationAccount_valid_returnsList() {
        List<Transaction> list = Arrays.asList(buildTransaction(5L, 15f));
        when(transactionDAO.getTransactionsByDestinationAccount(200L)).thenReturn(list);
        assertEquals(list, transactionService.getTransactionsByDestinationAccount(200L));
    }

    @Test
    void getTransactionsBetweenDates_validRange_returnsList() {
        Date from = new Date(1000);
        Date to = new Date(5000);
        List<Transaction> list = Arrays.asList(buildTransaction(6L, 80f));
        when(transactionDAO.getTransactionsBetweenDates(300L, from, to)).thenReturn(list);
        assertEquals(list, transactionService.getTransactionsBetweenDates(300L, from, to));
    }

    @Test
    void getTransactionsBetweenDates_fromAfterTo_throwsException() {
        Date from = new Date(5000);
        Date to = new Date(1000);
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsBetweenDates(300L, from, to));
    }

    private Transaction buildTransaction(Long id, float amount) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setOriginAccountId(100L);
        t.setDestinationAccountId(200L);
        t.setAmount(amount);
        t.setType(TransactionType.TRANSFER);
        t.setTimestamp(System.currentTimeMillis());
        t.setState(TransactionStates.PEND);
        return t;
    }
}
