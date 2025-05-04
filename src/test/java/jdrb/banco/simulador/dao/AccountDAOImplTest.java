package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementation.AccountDAOImpl;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountDAOImplTest {

    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    private AccountDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        dao = new AccountDAOImpl(connection);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
    }

    @Test
    public void testInsertarCuenta_Exitosa() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Account cuenta = crearCuentaValida();
        boolean resultado = dao.insertAccount(cuenta);

        assertTrue(resultado);
    }

    @Test
    public void testInsertarCuenta_IdClienteNull() {
        Account cuenta = crearCuentaValida();
        cuenta.setCustomerId(null);

        assertThrows(RuntimeException.class, () -> dao.insertAccount(cuenta));
    }

    @Test
    public void testInsertarCuenta_SaldoNegativo() {
        Account cuenta = crearCuentaValida();
        cuenta.setAccountBalance(-500f);

        assertThrows(RuntimeException.class, () -> dao.insertAccount(cuenta));
    }

    @Test
    public void testObtenerCuentaPorId_Existe() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockResultSetCuenta(rs);

        Account cuenta = dao.getAccountById("c1");

        assertNotNull(cuenta);
        assertEquals("c1", cuenta.getId());
    }

    @Test
    public void testObtenerCuentaPorId_NoExiste() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Account cuenta = dao.getAccountById("noexiste");

        assertNull(cuenta);
    }

    @Test
    public void testObtenerCuentasPorCliente() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetCuenta(rs);

        List<Account> cuentas = dao.getAccountsByClient("cliente1");

        assertEquals(1, cuentas.size());
    }

    @Test
    public void testActualizarCuenta() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Account cuenta = crearCuentaValida();
        boolean actualizado = dao.updateAccount(cuenta);

        assertTrue(actualizado);
    }

    @Test
    public void testEliminarCuenta() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean eliminado = dao.deleteAccount("c1");

        assertTrue(eliminado);
    }

    @Test
    public void testEliminarCuentaParaCliente() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean eliminado = dao.deleteAccountForClient("cliente1", "c1");

        assertTrue(eliminado);
    }

    private Account crearCuentaValida() {
        Account cuenta = new Account();
        cuenta.setId("c1");
        cuenta.setCustomerId("cliente1");
        cuenta.setAccountBalance(1000f);
        cuenta.setAccountType(AccountType.AHORRO);
        cuenta.setCreationDate(System.currentTimeMillis());
        return cuenta;
    }

    private void mockResultSetCuenta(ResultSet rs) throws SQLException {
        when(rs.getString("id")).thenReturn("c1");
        when(rs.getString("id_cliente")).thenReturn("cliente1");
        when(rs.getFloat("saldo")).thenReturn(1000f);
        when(rs.getString("tipo")).thenReturn("AHORRO");
        when(rs.getLong("fecha_creacion")).thenReturn(System.currentTimeMillis());
    }
}
