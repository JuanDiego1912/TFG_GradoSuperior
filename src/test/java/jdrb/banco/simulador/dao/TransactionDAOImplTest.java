package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementations.TransactionDAOImpl;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionDAOImplTest {

    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    private TransactionDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        dao = new TransactionDAOImpl(connection);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
    }

    @Test
    public void testInsertarTransaccion_Exito() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Transaction tx = crearTransaccionValida();
        boolean resultado = dao.registerTransaction(tx);

        assertTrue(resultado);
        verify(ps).executeUpdate();
    }

    @Test
    public void testInsertarTransaccion_IdNulo() {
        Transaction tx = crearTransaccionValida();
        tx.setId(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.registerTransaction(tx));
        assertTrue(ex.getMessage().contains("must have an id"));
    }

    @Test
    public void testInsertarTransaccion_CuentaDestinoNula() {
        Transaction tx = crearTransaccionValida();
        tx.setDestinationAccountId(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.registerTransaction(tx));
        assertTrue(ex.getMessage().contains("destination account"));
    }

    @Test
    public void testObtenerTransaccionPorId_Encontrada() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockResultSetTransaccion(rs);

        Transaction tx = dao.getTransactionById("tx123");

        assertNotNull(tx);
        assertEquals("tx123", tx.getId());
        verify(ps).executeQuery();
    }

    @Test
    public void testObtenerTransaccionPorId_NoEncontrada() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Transaction tx = dao.getTransactionById("noexiste");
        assertNull(tx);
    }

    @Test
    public void testObtenerTransaccionesPorCuentaOrigen() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetTransaccion(rs);

        List<Transaction> lista = dao.getTransactionsBySourceAccount("cuenta1");

        assertEquals(1, lista.size());
        assertEquals("cuenta1", lista.get(0).getOriginAccountId());
    }

    @Test
    public void testObtenerTransaccionesPorCuentaDestino() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetTransaccion(rs);

        List<Transaction> lista = dao.getTransactionsByDestinationAccount("cuenta2");

        assertEquals(1, lista.size());
        assertEquals("cuenta2", lista.get(0).getDestinationAccountId());
    }

    @Test
    public void testObtenerTransaccionesEntreFechas() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetTransaccion(rs);

        Date desde = new Date(System.currentTimeMillis() - 100000);
        Date hasta = new Date();

        List<Transaction> lista = dao.getTransactionsBetweenDates("cuenta1", desde, hasta);

        assertEquals(1, lista.size());
        assertEquals("cuenta1", lista.get(0).getOriginAccountId());
    }

    // Utilidades de test
    private Transaction crearTransaccionValida() {
        Transaction tx = new Transaction();
        tx.setId("tx123");
        tx.setOriginAccountId("cuenta1");
        tx.setDestinationAccountId("cuenta2");
        tx.setTransactionAmount(1000f);
        tx.setType(TransactionType.TRANSFER);
        tx.setDate(System.currentTimeMillis());
        return tx;
    }

    private void mockResultSetTransaccion(ResultSet rs) throws SQLException {
        when(rs.getString("id")).thenReturn("tx123");
        when(rs.getString("id_origen")).thenReturn("cuenta1");
        when(rs.getString("id_destino")).thenReturn("cuenta2");
        when(rs.getFloat("monto")).thenReturn(500f);
        when(rs.getString("tipo")).thenReturn("TRANSFER");
        when(rs.getLong("fecha")).thenReturn(System.currentTimeMillis());
    }
}
