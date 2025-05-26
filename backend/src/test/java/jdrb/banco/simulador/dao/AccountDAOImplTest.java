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

        assertTrue(result, "Account should be registered successfully");
    }

    @Test
    public void registerAccount_nullCustomerId_throwsException() {
        Account account = createValidAccount();
        account.setCustomerId(null);

        assertThrows(RuntimeException.class, () -> dao.registerAccount(account), "Should throw for null customer ID");
    }

    @Test
    public void registerAccount_negativeBalance_throwsException() {
        Account account = createValidAccount();
        account.setBalance(-500f);

        assertThrows(RuntimeException.class, () -> dao.registerAccount(account), "Should throw for negative balance");
    }

    @Test
    public void getAccountById_exists_returnsAccount() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockResultSet(rs);

        Account account = dao.getAccountById("c1");

        assertNotNull(account);
        assertEquals("c1", account.getId());
    }

    @Test
    public void getAccountById_notExists_returnsNull() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Account account = dao.getAccountById("nonexistent");

        assertNull(account);
    }

    @Test
    public void getAccountsByClient_returnsList() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSet(rs);

        List<Account> accounts = dao.getAccountsByClient("client1");

        assertEquals(1, accounts.size());
    }

    @Test
    public void updateAccount_successful() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Account account = createValidAccount();
        boolean updated = dao.updateAccount(account);

        assertTrue(updated, "Account should be updated");
    }

    @Test
    public void deleteAccount_successful() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean deleted = dao.deleteAccount("c1");

        assertTrue(deleted, "Account should be deleted");
    }

    @Test
    public void deleteAccountForClient_successful() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean deleted = dao.deleteAccountForClient("client1", "c1");

        assertTrue(deleted, "Client's account should be deleted");
    }

    private Account createValidAccount() {
        Account account = new Account();
        account.setId("c1");
        account.setCustomerId("client1");
        account.setBalance(1000f);
        account.setAccountType(AccountType.SAVINGS);
        account.setCreationDate(System.currentTimeMillis());
        return account;
    }

    private void mockResultSet(ResultSet rs) throws SQLException {
        when(rs.getString(AccountTable.ID)).thenReturn("c1");
        when(rs.getString(AccountTable.CUSTOMER_ID)).thenReturn("client1");
        when(rs.getFloat(AccountTable.BALANCE)).thenReturn(1000f);
        when(rs.getString(AccountTable.TYPE)).thenReturn("SAVINGS");
        when(rs.getLong(AccountTable.CREATION_DATE)).thenReturn(System.currentTimeMillis());
    }
}
