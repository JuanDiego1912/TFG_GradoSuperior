package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.dao.TransactionDAO;
import jdrb.banco.simulador.model.Account;
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

class TransactionServiceImplTest {

    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionDAO = mock(TransactionDAO.class);
        accountDAO = mock(AccountDAO.class);
        transactionService = new TransactionServiceImpl(transactionDAO,accountDAO);
    }

    @Test
    void registerTransaction_valid_returnsTrue() {
        Transaction t = buildTransaction("T1", 100.0f);

        when(accountDAO.getAccountById("ACC1")).thenReturn(
                new Account("ACC1", "C1", 1000f, AccountType.SAVINGS, System.currentTimeMillis()));

        when(transactionDAO.registerTransaction(t)).thenReturn(true);

        assertTrue(transactionService.registerTransaction(t));
    }


    @Test
    void registerTransaction_null_throwsException() {
        assertThrows(RuntimeException.class, () -> transactionService.registerTransaction(null));
    }

    @Test
    void registerTransaction_negativeAmount_throwsException() {
        Transaction t = buildTransaction("T2", -5.0f);
        assertThrows(RuntimeException.class, () -> transactionService.registerTransaction(t));
    }

    @Test
    void getTransactionById_valid_returnsTransaction() {
        Transaction t = buildTransaction("T3", 200.0f);
        when(transactionDAO.getTransactionById("T3")).thenReturn(t);
        assertEquals(t, transactionService.getTransactionById("T3"));
    }

    @Test
    void getTransactionById_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(null));
    }

    @Test
    void getTransactionsBySourceAccount_valid_returnsList() {
        List<Transaction> list = Arrays.asList(buildTransaction("T4", 20f));
        when(transactionDAO.getTransactionsBySourceAccount("ACC1")).thenReturn(list);
        assertEquals(list, transactionService.getTransactionsBySourceAccount("ACC1"));
    }

    @Test
    void getTransactionsByDestinationAccount_valid_returnsList() {
        List<Transaction> list = Arrays.asList(buildTransaction("T5", 15f));
        when(transactionDAO.getTransactionsByDestinationAccount("ACC2")).thenReturn(list);
        assertEquals(list, transactionService.getTransactionsByDestinationAccount("ACC2"));
    }

    @Test
    void getTransactionsBetweenDates_validRange_returnsList() {
        Date from = new Date(1000);
        Date to = new Date(5000);
        List<Transaction> list = Arrays.asList(buildTransaction("T6", 80f));
        when(transactionDAO.getTransactionsBetweenDates("ACC3", from, to)).thenReturn(list);
        assertEquals(list, transactionService.getTransactionsBetweenDates("ACC3", from, to));
    }

    @Test
    void getTransactionsBetweenDates_fromAfterTo_throwsException() {
        Date from = new Date(5000);
        Date to = new Date(1000);
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionsBetweenDates("ACC3", from, to));
    }

    private Transaction buildTransaction(String id, float amount) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setOriginAccountId("ACC1");
        t.setDestinationAccountId("ACC2");
        t.setAmount(amount);
        t.setType(TransactionType.TRANSFER);
        t.setTimestamp(System.currentTimeMillis());
        return t;
    }
}
