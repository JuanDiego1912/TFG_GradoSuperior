package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementations.CustomerDAOImpl;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.enums.CustomerStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerDAOImplTest {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private Statement statement;
    private ResultSet resultSet;
    private CustomerDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);
        dao = new CustomerDAOImpl(connection);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.createStatement()).thenReturn(statement);
    }

    @Test
    public void testRegisterCustomer_Successful() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        Customer customer = createValidCustomer();

        boolean result = dao.registerCustomer(customer);

        assertTrue(result);
    }

    @Test
    public void testGetCustomerById_Exists() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        mockCustomerResultSet(resultSet);

        Customer customer = dao.getCustomerById("cli1");

        assertNotNull(customer);
        assertEquals("cli1", customer.getId());
        assertEquals(CustomerStates.ACTIVE, customer.getState());
    }

    @Test
    public void testGetCustomerById_NotFound() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Customer customer = dao.getCustomerById("nonexistent");

        assertNull(customer);
    }

    @Test
    public void testGetCustomerById_InvalidState_ThrowsException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("id")).thenReturn("cli2");
        when(resultSet.getString("nombre")).thenReturn("Ana");
        when(resultSet.getString("apellido")).thenReturn("Gomez");
        when(resultSet.getString("dni")).thenReturn("87654321");
        when(resultSet.getString("email")).thenReturn("ana@example.com");
        when(resultSet.getString("telefono")).thenReturn("555123456");
        when(resultSet.getLong("fecha_registro")).thenReturn(System.currentTimeMillis());
        when(resultSet.getString("estado")).thenReturn("INVALID_STATE");

        assertThrows(IllegalArgumentException.class, () -> dao.getCustomerById("cli2"));
    }

    @Test
    public void testGetAllCustomers_SingleResult() throws SQLException {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        mockCustomerResultSet(resultSet);

        List<Customer> customers = dao.getAllCustomers();

        assertEquals(1, customers.size());
    }

    @Test
    public void testUpdateCustomer_Successful() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        Customer customer = createValidCustomer();

        boolean result = dao.updateCustomer(customer);

        assertTrue(result);
    }

    @Test
    public void testDeleteCustomer_Successful() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean deleted = dao.deleteCustomer("cli1");

        assertTrue(deleted);
    }

    @Test
    public void testRegisterCustomer_SQLException_ThrowsRuntime() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("DB Error"));
        Customer customer = createValidCustomer();

        assertThrows(RuntimeException.class, () -> dao.registerCustomer(customer));
    }

    private Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setId("cli1");
        customer.setName("Juan");
        customer.setLastname("Perez");
        customer.setDni("12345678");
        customer.setEmail("juan@example.com");
        customer.setCustomerPhone("555000111");
        customer.setRegistrationDate(System.currentTimeMillis());
        customer.setState(CustomerStates.ACTIVE);
        return customer;
    }

    private void mockCustomerResultSet(ResultSet rs) throws SQLException {
        when(rs.getString("id")).thenReturn("cli1");
        when(rs.getString("nombre")).thenReturn("Juan");
        when(rs.getString("apellido")).thenReturn("Perez");
        when(rs.getString("dni")).thenReturn("12345678");
        when(rs.getString("email")).thenReturn("juan@example.com");
        when(rs.getString("telefono")).thenReturn("555000111");
        when(rs.getLong("fecha_registro")).thenReturn(System.currentTimeMillis());
        when(rs.getString("estado")).thenReturn("ACTIVE");
    }
}
