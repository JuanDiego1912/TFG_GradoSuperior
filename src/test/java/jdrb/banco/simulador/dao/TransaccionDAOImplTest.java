package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementacion.TransaccionDAOImpl;
import jdrb.banco.simulador.model.Transaccion;
import jdrb.banco.simulador.model.enums.TipoTransaccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransaccionDAOImplTest {

    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    private TransaccionDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        dao = new TransaccionDAOImpl(connection);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
    }

    @Test
    public void testInsertarTransaccion_Exito() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Transaccion tx = crearTransaccionValida();
        boolean resultado = dao.insertarTransaccion(tx);

        assertTrue(resultado);
        verify(ps).executeUpdate();
    }

    @Test
    public void testInsertarTransaccion_IdNulo() {
        Transaccion tx = crearTransaccionValida();
        tx.setId(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertarTransaccion(tx));
        assertTrue(ex.getMessage().contains("debe de tener un id"));
    }

    @Test
    public void testInsertarTransaccion_CuentaDestinoNula() {
        Transaccion tx = crearTransaccionValida();
        tx.setIdCuentaDestino(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertarTransaccion(tx));
        assertTrue(ex.getMessage().contains("cuenta destino"));
    }

    @Test
    public void testObtenerTransaccionPorId_Encontrada() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockResultSetTransaccion(rs);

        Transaccion tx = dao.obtenerTransaccionPorId("tx123");

        assertNotNull(tx);
        assertEquals("tx123", tx.getId());
        verify(ps).executeQuery();
    }

    @Test
    public void testObtenerTransaccionPorId_NoEncontrada() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Transaccion tx = dao.obtenerTransaccionPorId("noexiste");
        assertNull(tx);
    }

    @Test
    public void testObtenerTransaccionesPorCuentaOrigen() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetTransaccion(rs);

        List<Transaccion> lista = dao.obtenerTransaccionesPorCuentaOrigen("cuenta1");

        assertEquals(1, lista.size());
        assertEquals("cuenta1", lista.get(0).getIdCuentaOrigen());
    }

    @Test
    public void testObtenerTransaccionesPorCuentaDestino() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetTransaccion(rs);

        List<Transaccion> lista = dao.obtenerTransaccionesPorCuentaDestino("cuenta2");

        assertEquals(1, lista.size());
        assertEquals("cuenta2", lista.get(0).getIdCuentaDestino());
    }

    @Test
    public void testObtenerTransaccionesEntreFechas() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetTransaccion(rs);

        Date desde = new Date(System.currentTimeMillis() - 100000);
        Date hasta = new Date();

        List<Transaccion> lista = dao.obtenerTransaccionesEntreFechas("cuenta1", desde, hasta);

        assertEquals(1, lista.size());
        assertEquals("cuenta1", lista.get(0).getIdCuentaOrigen());
    }

    // Utilidades de test
    private Transaccion crearTransaccionValida() {
        Transaccion tx = new Transaccion();
        tx.setId("tx123");
        tx.setIdCuentaOrigen("cuenta1");
        tx.setIdCuentaDestino("cuenta2");
        tx.setMonto(1000f);
        tx.setTipo(TipoTransaccion.TRANSFERENCIA);
        tx.setFecha(System.currentTimeMillis());
        return tx;
    }

    private void mockResultSetTransaccion(ResultSet rs) throws SQLException {
        when(rs.getString("id")).thenReturn("tx123");
        when(rs.getString("id_origen")).thenReturn("cuenta1");
        when(rs.getString("id_destino")).thenReturn("cuenta2");
        when(rs.getFloat("monto")).thenReturn(500f);
        when(rs.getString("tipo")).thenReturn("TRANSFERENCIA");
        when(rs.getLong("fecha")).thenReturn(System.currentTimeMillis());
    }
}
