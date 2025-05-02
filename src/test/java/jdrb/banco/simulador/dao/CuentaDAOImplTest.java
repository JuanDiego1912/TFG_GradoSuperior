package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementacion.CuentaDAOImpl;
import jdrb.banco.simulador.model.Cuenta;
import jdrb.banco.simulador.model.enums.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CuentaDAOImplTest {

    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    private CuentaDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        dao = new CuentaDAOImpl(connection);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
    }

    @Test
    public void testInsertarCuenta_Exitosa() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Cuenta cuenta = crearCuentaValida();
        boolean resultado = dao.insertarCuenta(cuenta);

        assertTrue(resultado);
    }

    @Test
    public void testInsertarCuenta_IdClienteNull() {
        Cuenta cuenta = crearCuentaValida();
        cuenta.setIdCliente(null);

        assertThrows(RuntimeException.class, () -> dao.insertarCuenta(cuenta));
    }

    @Test
    public void testInsertarCuenta_SaldoNegativo() {
        Cuenta cuenta = crearCuentaValida();
        cuenta.setSaldo(-500f);

        assertThrows(RuntimeException.class, () -> dao.insertarCuenta(cuenta));
    }

    @Test
    public void testObtenerCuentaPorId_Existe() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockResultSetCuenta(rs);

        Cuenta cuenta = dao.obtenerCuentaPorId("c1");

        assertNotNull(cuenta);
        assertEquals("c1", cuenta.getId());
    }

    @Test
    public void testObtenerCuentaPorId_NoExiste() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Cuenta cuenta = dao.obtenerCuentaPorId("noexiste");

        assertNull(cuenta);
    }

    @Test
    public void testObtenerCuentasPorCliente() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetCuenta(rs);

        List<Cuenta> cuentas = dao.obtenerCuentasPorCliente("cliente1");

        assertEquals(1, cuentas.size());
    }

    @Test
    public void testActualizarCuenta() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Cuenta cuenta = crearCuentaValida();
        boolean actualizado = dao.actualizarCuenta(cuenta);

        assertTrue(actualizado);
    }

    @Test
    public void testEliminarCuenta() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean eliminado = dao.eliminarCuenta("c1");

        assertTrue(eliminado);
    }

    @Test
    public void testEliminarCuentaParaCliente() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean eliminado = dao.eliminarCuentaParaCliente("cliente1", "c1");

        assertTrue(eliminado);
    }

    private Cuenta crearCuentaValida() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId("c1");
        cuenta.setIdCliente("cliente1");
        cuenta.setSaldo(1000f);
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setFechaCreacion(System.currentTimeMillis());
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
