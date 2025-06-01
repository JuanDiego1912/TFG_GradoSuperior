package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.dao.implementations.CustomerDAOImpl;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.enums.CustomerStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerDAOImplTest {

    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private Statement statement;
    private ResultSet resultSet;
    private CustomerDAOImpl dao;

    @BeforeEach
    public void setUp() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.createStatement()).thenReturn(statement);

        dao = new CustomerDAOImpl(dataSource);
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

        Customer customer = dao.getCustomerById(1L);

        assertNotNull(customer);
        assertEquals(1L, customer.getId());
        assertEquals(CustomerStates.ACTIVE, customer.getState());
    }

    @Test
    public void testGetCustomerById_NotFound() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Customer customer = dao.getCustomerById(999L);

        assertNull(customer);
    }

    @Test
    public void testGetCustomerById_InvalidState_AssignsUnknown() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(2L);
        when(resultSet.getString("name")).thenReturn("Ana");
        when(resultSet.getString("last_name")).thenReturn("Gomez");
        when(resultSet.getString("dni")).thenReturn("87654321");
        when(resultSet.getString("email")).thenReturn("ana@example.com");
        when(resultSet.getString("phone")).thenReturn("555123456");
        when(resultSet.getString("password")).thenReturn("hashed_password");
        when(resultSet.getLong("creation_date")).thenReturn(System.currentTimeMillis());
        when(resultSet.getString("state")).thenReturn("UNKNOWN");

        Customer customer = dao.getCustomerById(2L);

        assertNotNull(customer);
        assertEquals(CustomerStates.UNKNOWN, customer.getState());
    }

    @Test
    public void testGetAllCustomers_SingleResult() throws SQLException {
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        mockCustomerResultSet(resultSet);

        List<Customer> customers = dao.getAllCustomers();

        assertEquals(1, customers.size());
        assertEquals("Juan", customers.get(0).getName());
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

        boolean deleted = dao.deleteCustomer(1L);

        assertTrue(deleted);
    }

    @Test
    public void testRegisterCustomer_SQLException_ThrowsRuntime() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("DB Error"));
        Customer customer = createValidCustomer();

        assertThrows(RuntimeException.class, () -> dao.registerCustomer(customer));
    }

    @Test
    public void testFindByEmail_ReturnsCustomer() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        mockCustomerResultSet(resultSet);

        Customer customer = dao.findByEmail("juan@example.com");

        assertNotNull(customer);
        assertEquals("juan@example.com", customer.getEmail());
    }

    private Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Juan");
        customer.setLastname("Perez");
        customer.setDni("12345678");
        customer.setEmail("juan@example.com");
        customer.setPhone("555000111");
        customer.setCreationDate(System.currentTimeMillis());
        customer.setPassword("hashed_password");
        customer.setState(CustomerStates.ACTIVE);
        return customer;
    }

    private void mockCustomerResultSet(ResultSet rs) throws SQLException {
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Juan");
        when(rs.getString("last_name")).thenReturn("Perez");
        when(rs.getString("dni")).thenReturn("12345678");
        when(rs.getString("email")).thenReturn("juan@example.com");
        when(rs.getString("phone")).thenReturn("555000111");
        when(rs.getString("password")).thenReturn("hashed_password");
        when(rs.getLong("creation_date")).thenReturn(System.currentTimeMillis());
        when(rs.getString("state")).thenReturn("ACTIVE");
    }
}
