package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementations.TransactionDAOImpl;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.model.enums.TransactionStates;
import jdrb.banco.simulador.model.enums.TransactionType;
import jdrb.banco.simulador.utils.MappingDBTables.TransactionsTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionDAOImplTest {

    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    private TransactionDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        dao = new TransactionDAOImpl(dataSource);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
    }

    @Test
    public void testInsertTransaction_Success() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Transaction tx = createValidTransaction();
        boolean result = dao.registerTransaction(tx);

        assertTrue(result);
        verify(ps).executeUpdate();
    }

    @Test
    public void testInsertTransaction_NullDestinationAccount() {
        Transaction tx = createValidTransaction();
        tx.setDestinationAccountId(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.registerTransaction(tx));
        assertTrue(ex.getMessage().contains("destination account"));
    }

    @Test
    public void testInsertTransaction_NegativeAmount() {
        Transaction tx = createValidTransaction();
        tx.setAmount(-500f);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.registerTransaction(tx));
        assertTrue(ex.getMessage().contains("must be positive"));
    }

    @Test
    public void testGetTransactionById_Found() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockTransactionResultSet(rs);

        Transaction tx = dao.getTransactionById(123L);

        assertNotNull(tx);
        assertEquals(123L, tx.getId());
        verify(ps).executeQuery();
    }

    @Test
    public void testGetTransactionById_NotFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Transaction tx = dao.getTransactionById(999L);
        assertNull(tx);
    }

    @Test
    public void testGetTransactionsBySourceAccount() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockTransactionResultSet(rs);

        List<Transaction> list = dao.getTransactionsBySourceAccount(1L);

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getOriginAccountId());
    }

    @Test
    public void testGetTransactionsByDestinationAccount() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockTransactionResultSet(rs);

        List<Transaction> list = dao.getTransactionsByDestinationAccount(2L);

        assertEquals(1, list.size());
        assertEquals(2L, list.get(0).getDestinationAccountId());
    }

    @Test
    public void testGetTransactionsBetweenDates() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockTransactionResultSet(rs);

        Date from = new Date(System.currentTimeMillis() - 100000);
        Date to = new Date();

        List<Transaction> list = dao.getTransactionsBetweenDates(1L, from, to);

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getOriginAccountId());
    }

    // ---------- Helper methods ----------

    private Transaction createValidTransaction() {
        Transaction tx = new Transaction();
        tx.setOriginAccountId(1L);
        tx.setDestinationAccountId(2L);
        tx.setAmount(1000f);
        tx.setType(TransactionType.TRANSFER);
        tx.setTimestamp(System.currentTimeMillis());
        tx.setState(TransactionStates.COMPLETED);
        return tx;
    }

    private void mockTransactionResultSet(ResultSet rs) throws SQLException {
        when(rs.getLong(TransactionsTable.ID)).thenReturn(123L);
        when(rs.getLong(TransactionsTable.ORIGIN_ACCOUNT_ID)).thenReturn(1L);
        when(rs.getLong(TransactionsTable.DESTINATION_ACCOUNT_ID)).thenReturn(2L);
        when(rs.getFloat(TransactionsTable.AMOUNT)).thenReturn(500f);
        when(rs.getString(TransactionsTable.TYPE)).thenReturn("TRANSFER");
        when(rs.getLong(TransactionsTable.TIMESTAMP)).thenReturn(System.currentTimeMillis());
        when(rs.getString(TransactionsTable.STATE)).thenReturn("COMPLETED");
    }
}