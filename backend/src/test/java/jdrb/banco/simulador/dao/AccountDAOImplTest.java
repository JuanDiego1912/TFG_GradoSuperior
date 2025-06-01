package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementations.AccountDAOImpl;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.enums.AccountType;
import jdrb.banco.simulador.utils.MappingDBTables.AccountTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountDAOImplTest {

    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    private AccountDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        dao = new AccountDAOImpl(dataSource);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
    }

    @Test
    public void registerAccount_successful() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Account account = createValidAccount();
        boolean result = dao.registerAccount(account);

        assertTrue(result);
        verify(ps).setString(1, account.getAccountNumber());
        verify(ps).setLong(2, account.getCustomerId());
        verify(ps).setFloat(3, account.getBalance());
        verify(ps).setString(4, account.getAccountType().name());
        verify(ps).executeUpdate();
    }

    @Test
    public void registerAccount_nullCustomerId_throwsException() {
        Account account = createValidAccount();
        account.setCustomerId(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> dao.registerAccount(account));
        assertTrue(thrown.getMessage().contains("null or empty customer id"));
    }

    @Test
    public void registerAccount_negativeBalance_throwsException() {
        Account account = createValidAccount();
        account.setBalance(-100);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> dao.registerAccount(account));
        assertTrue(thrown.getMessage().contains("negative balance"));
    }

    @Test
    public void getAccountById_exists_returnsAccount() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockResultSet(rs);

        Account account = dao.getAccountById(1L);

        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("ACC123", account.getAccountNumber());
        verify(ps).setLong(1, 1L);
    }

    @Test
    public void getAccountById_notExists_returnsNull() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Account account = dao.getAccountById(999L);

        assertNull(account);
        verify(ps).setLong(1, 999L);
    }

    @Test
    public void getAccountsByClient_returnsList() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSet(rs);

        List<Account> accounts = dao.getAccountsByClient(1L);

        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals(1L, accounts.get(0).getCustomerId());
        verify(ps).setLong(1, 1L);
    }

    @Test
    public void updateAccount_successful() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Account account = createValidAccount();
        boolean updated = dao.updateAccount(account);

        assertTrue(updated);
        verify(ps).setFloat(1, account.getBalance());
        verify(ps).setString(2, account.getAccountType().name());
        verify(ps).setLong(3, account.getId());
        verify(ps).executeUpdate();
    }

    @Test
    public void deleteAccount_successful() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean deleted = dao.deleteAccount(1L);

        assertTrue(deleted);
        verify(ps).setLong(1, 1L);
        verify(ps).executeUpdate();
    }

    @Test
    public void deleteAccountForClient_successful() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean deleted = dao.deleteAccountForClient(1L, 1L);

        assertTrue(deleted);
        verify(ps).setLong(1, 1L);
        verify(ps).setLong(2, 1L);
        verify(ps).executeUpdate();
    }

    private Account createValidAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC123");
        account.setCustomerId(1L);
        account.setBalance(1000f);
        account.setAccountType(AccountType.SAVINGS);
        account.setCreationDate(System.currentTimeMillis());
        return account;
    }

    private void mockResultSet(ResultSet rs) throws SQLException {
        when(rs.getLong(AccountTable.ID)).thenReturn(1L);
        when(rs.getString(AccountTable.ACCOUNT_NUMBER)).thenReturn("ACC123");
        when(rs.getLong(AccountTable.CUSTOMER_ID)).thenReturn(1L);
        when(rs.getFloat(AccountTable.BALANCE)).thenReturn(1000f);
        when(rs.getString(AccountTable.TYPE)).thenReturn("SAVINGS");
        when(rs.getLong(AccountTable.CREATION_DATE)).thenReturn(System.currentTimeMillis());
    }
}
