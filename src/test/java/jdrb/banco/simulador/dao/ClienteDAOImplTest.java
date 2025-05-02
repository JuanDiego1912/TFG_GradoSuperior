package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementacion.ClienteDAOImpl;
import jdrb.banco.simulador.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClienteDAOImplTest {

    private Connection connection;
    private PreparedStatement ps;
    private Statement statement;
    private ResultSet rs;
    private ClienteDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        statement = mock(Statement.class);
        rs = mock(ResultSet.class);
        dao = new ClienteDAOImpl(connection);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(connection.createStatement()).thenReturn(statement);
    }

    @Test
    public void testInsertarCliente() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Cliente cliente = crearClienteValido();
        boolean resultado = dao.insertarCliente(cliente);

        assertTrue(resultado);
    }

    @Test
    public void testObtenerClientePorId_Existe() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockResultSetCliente(rs);

        Cliente cliente = dao.obtenerClientePorId("cli1");

        assertNotNull(cliente);
        assertEquals("cli1", cliente.getId());
    }

    @Test
    public void testObtenerClientePorId_NoExiste() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Cliente cliente = dao.obtenerClientePorId("noexiste");

        assertNull(cliente);
    }

    @Test
    public void testObtenerTodosLosClientes() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetCliente(rs);

        List<Cliente> clientes = dao.obtenerClientes();

        assertEquals(1, clientes.size());
    }


    @Test
    public void testActualizarCliente() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Cliente cliente = crearClienteValido();
        boolean resultado = dao.actualizarCliente(cliente);

        assertTrue(resultado);
    }

    @Test
    public void testEliminarCliente() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean eliminado = dao.eliminarCliente("cli1");

        assertTrue(eliminado);
    }

    private Cliente crearClienteValido() {
        Cliente cliente = new Cliente();
        cliente.setId("cli1");
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setDni("12345678");
        return cliente;
    }

    private void mockResultSetCliente(ResultSet rs) throws SQLException {
        when(rs.getString("id")).thenReturn("cli1");
        when(rs.getString("nombre")).thenReturn("Juan");
        when(rs.getString("apellido")).thenReturn("Perez");
        when(rs.getString("dni")).thenReturn("12345678");
    }
}
