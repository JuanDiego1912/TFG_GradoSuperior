package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementation.CustomerDAOImpl;
import jdrb.banco.simulador.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerDAOImplTest {

    private Connection connection;
    private PreparedStatement ps;
    private Statement statement;
    private ResultSet rs;
    private CustomerDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        statement = mock(Statement.class);
        rs = mock(ResultSet.class);
        dao = new CustomerDAOImpl(connection);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(connection.createStatement()).thenReturn(statement);
    }

    @Test
    public void testInsertarCliente() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Customer cliente = crearClienteValido();
        boolean resultado = dao.insertCustomer(cliente);

        assertTrue(resultado);
    }

    @Test
    public void testObtenerClientePorId_Existe() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockResultSetCliente(rs);

        Customer cliente = dao.getCustomerById("cli1");

        assertNotNull(cliente);
        assertEquals("cli1", cliente.getId());
    }

    @Test
    public void testObtenerClientePorId_NoExiste() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Customer cliente = dao.getCustomerById("noexiste");

        assertNull(cliente);
    }

    @Test
    public void testObtenerTodosLosClientes() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockResultSetCliente(rs);

        List<Customer> clientes = dao.getAllCustomers();

        assertEquals(1, clientes.size());
    }


    @Test
    public void testActualizarCliente() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        Customer cliente = crearClienteValido();
        boolean resultado = dao.updateCustomer(cliente);

        assertTrue(resultado);
    }

    @Test
    public void testEliminarCliente() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);

        boolean eliminado = dao.deleteCustomer("cli1");

        assertTrue(eliminado);
    }

    private Customer crearClienteValido() {
        Customer cliente = new Customer();
        cliente.setId("cli1");
        cliente.setName("Juan");
        cliente.setLastname("Perez");
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
