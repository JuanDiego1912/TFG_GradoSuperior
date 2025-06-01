package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.enums.CustomerStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerDAO = mock(CustomerDAO.class);
        accountDAO = mock(AccountDAO.class);
        customerService = new CustomerServiceImpl(customerDAO, accountDAO);
    }

    @Test
    void registerCustomer_validCustomer_returnsTrue() {
        Customer customer = buildCustomer(123L, "plain123");
        when(customerDAO.registerCustomer(any(Customer.class))).thenReturn(true);

        boolean result = customerService.registerCustomer(customer);

        assertTrue(result);
        verify(customerDAO).registerCustomer(any(Customer.class));
    }

    @Test
    void registerCustomer_nullCustomer_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(null));
    }

    @Test
    void registerCustomer_missingEmail_throwsException() {
        Customer customer = buildCustomer(1L, "1234");
        customer.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(customer));
    }

    @Test
    void registerCustomer_missingPassword_throwsException() {
        Customer customer = buildCustomer(1L, null);

        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(customer));
    }

    @Test
    void getCustomerById_validId_returnsCustomer() {
        Customer customer = buildCustomer(456L, null);
        when(customerDAO.getCustomerById(456L)).thenReturn(customer);

        Customer result = customerService.getCustomerById(456L);

        assertEquals(customer, result);
        verify(customerDAO).getCustomerById(456L);
    }

    @Test
    void getCustomerById_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerById(null));
    }

    @Test
    void getCustomerById_negativeId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerById(-10L));
    }

    @Test
    void getAllCustomers_returnsList() {
        List<Customer> customers = Arrays.asList(
                buildCustomer(1L, null),
                buildCustomer(2L, null)
        );

        when(customerDAO.getAllCustomers()).thenReturn(customers);

        assertEquals(customers, customerService.getAllCustomers());
    }

    @Test
    void updateCustomer_validCustomer_returnsTrue() {
        Customer customer = buildCustomer(999L, "newpass");
        when(customerDAO.updateCustomer(any(Customer.class))).thenReturn(true);

        boolean result = customerService.updateCustomer(customer);

        assertTrue(result);
        verify(customerDAO).updateCustomer(any(Customer.class));
    }

    @Test
    void updateCustomer_nullCustomer_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomer(null));
    }

    @Test
    void updateCustomer_nullId_throwsException() {
        Customer customer = buildCustomer(null, "pass");
        assertThrows(NullPointerException.class, () -> customerService.updateCustomer(customer));
    }

    @Test
    void deleteCustomer_validId_returnsTrue() {
        when(customerDAO.deleteCustomer(321L)).thenReturn(true);

        boolean result = customerService.deleteCustomer(321L);

        assertTrue(result);
        verify(customerDAO).deleteCustomer(321L);
    }

    @Test
    void deleteCustomer_invalidId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.deleteCustomer(null));
        assertThrows(IllegalArgumentException.class, () -> customerService.deleteCustomer(-5L));
    }

    @Test
    void registerCustomer_passwordShouldBeEncrypted() {
        Customer rawCustomer = buildCustomer(10L, "plaintext123");

        when(customerDAO.registerCustomer(any(Customer.class))).thenAnswer(invocation -> {
            Customer passed = invocation.getArgument(0);
            assertNotEquals("plaintext123", passed.getPassword());
            assertTrue(passed.getPassword().startsWith("$2a$"));
            return true;
        });

        assertTrue(customerService.registerCustomer(rawCustomer));
        verify(customerDAO).registerCustomer(any(Customer.class));
    }

    @Test
    void updateCustomer_passwordShouldBeEncrypted() {
        Customer customer = buildCustomer(99L, "secret");

        when(customerDAO.updateCustomer(any(Customer.class))).thenAnswer(invocation -> {
            Customer updated = invocation.getArgument(0);
            assertNotEquals("secret", updated.getPassword());
            assertTrue(updated.getPassword().startsWith("$2a$"));
            return true;
        });

        assertTrue(customerService.updateCustomer(customer));
        verify(customerDAO).updateCustomer(any(Customer.class));
    }

    @Test
    void login_validCredentials_returnsCustomer() {
        String email = "login@test.com";
        String plainPassword = "mypassword";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        Customer customer = buildCustomer(42L, hashedPassword);
        customer.setEmail(email);
        customer.setState(CustomerStates.ACTIVE);

        when(customerDAO.findByEmail(email)).thenReturn(customer);

        Customer result = customerService.login(email, plainPassword);

        assertNotNull(result);
        assertEquals(customer, result);
    }

    @Test
    void login_wrongPassword_throwsException() {
        Customer customer = buildCustomer(33L, BCrypt.hashpw("correct", BCrypt.gensalt()));
        customer.setEmail("login@test.com");
        customer.setState(CustomerStates.ACTIVE);

        when(customerDAO.findByEmail("login@test.com")).thenReturn(customer);

        assertThrows(IllegalArgumentException.class, () -> customerService.login("login@test.com", "wrongpass"));
    }

    @Test
    void login_customerNotFound_throwsException() {
        when(customerDAO.findByEmail("missing@test.com")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> customerService.login("missing@test.com", "any"));
    }

    @Test
    void login_customerInactive_throwsException() {
        Customer customer = buildCustomer(88L, BCrypt.hashpw("pass", BCrypt.gensalt()));
        customer.setEmail("inactive@test.com");
        customer.setState(CustomerStates.BLOCKED);

        when(customerDAO.findByEmail("inactive@test.com")).thenReturn(customer);

        assertThrows(IllegalArgumentException.class, () -> customerService.login("inactive@test.com", "pass"));
    }

    private Customer buildCustomer(Long id, String password) {
        Customer c = new Customer();
        c.setId(id);
        c.setName("John");
        c.setLastname("Doe");
        c.setDni("12345678");
        c.setEmail("john.doe@test.com");
        c.setPhone("555-1234");
        c.setState(CustomerStates.ACTIVE);
        c.setPassword(password);
        return c;
    }
}
