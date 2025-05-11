package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private CustomerDAO customerDAO;
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerDAO = mock(CustomerDAO.class);
        customerService = new CustomerServiceImpl(customerDAO);
    }

    @Test
    void registerCustomer_validCustomer_returnsTrue() {
        Customer customer = buildCustomer("123");
        when(customerDAO.registerCustomer(customer)).thenReturn(true);
        assertTrue(customerService.registerCustomer(customer));
        verify(customerDAO).registerCustomer(customer);
    }

    @Test
    void registerCustomer_nullCustomer_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(null));
    }

    @Test
    void registerCustomer_emptyId_throwsException() {
        Customer customer = buildCustomer("");
        assertThrows(IllegalArgumentException.class, () -> customerService.registerCustomer(customer));
    }

    @Test
    void getCustomerById_validId_returnsCustomer() {
        Customer customer = buildCustomer("456");
        when(customerDAO.getCustomerById("456")).thenReturn(customer);
        assertEquals(customer, customerService.getCustomerById("456"));
        verify(customerDAO).getCustomerById("456");
    }

    @Test
    void getCustomerById_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerById(null));
    }

    @Test
    void getAllCustomers_returnsList() {
        List<Customer> customers = Arrays.asList(buildCustomer("1"), buildCustomer("2"));
        when(customerDAO.getAllCustomers()).thenReturn(customers);
        assertEquals(customers, customerService.getAllCustomers());
    }

    @Test
    void updateCustomer_validCustomer_returnsTrue() {
        Customer customer = buildCustomer("999");
        when(customerDAO.updateCustomer(customer)).thenReturn(true);
        assertTrue(customerService.updateCustomer(customer));
        verify(customerDAO).updateCustomer(customer);
    }

    @Test
    void updateCustomer_invalidCustomer_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomer(null));
    }

    @Test
    void deleteCustomer_validId_returnsTrue() {
        when(customerDAO.deleteCustomer("321")).thenReturn(true);
        assertTrue(customerService.deleteCustomer("321"));
        verify(customerDAO).deleteCustomer("321");
    }

    @Test
    void deleteCustomer_emptyId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> customerService.deleteCustomer(""));
    }

    @Test
    void registerCustomer_passwordShouldBeEncrypted() {
        Customer rawCustomer = buildCustomer("enc123");
        rawCustomer.setPassword("plaintext123");

        when(customerDAO.registerCustomer(any(Customer.class))).thenAnswer(invocation -> {
            Customer passedCustomer = invocation.getArgument(0);

            assertNotNull(passedCustomer.getPassword());
            assertNotEquals("plaintext123", passedCustomer.getPassword(), "Password should be encrypted");
            assertTrue(passedCustomer.getPassword().startsWith("$2a$"), "Encrypted password should start with $2a$");

            return true;
        });

        assertTrue(customerService.registerCustomer(rawCustomer));
        verify(customerDAO).registerCustomer(any(Customer.class));
    }


    @Test
    void updateCustomer_passwordShouldBeEncrypted() {
        Customer customer = buildCustomer("upEnc");
        customer.setPassword("myNewPassword");

        when(customerDAO.updateCustomer(any(Customer.class))).thenAnswer(invocation -> {
            Customer updatedCustomer = invocation.getArgument(0);

            assertNotEquals("myNewPassword", updatedCustomer.getPassword(), "Password should be encrypted");
            assertTrue(updatedCustomer.getPassword().startsWith("$2a$"), "Encrypted password should start with $2a$");

            return true;
        });

        assertTrue(customerService.updateCustomer(customer));
        verify(customerDAO).updateCustomer(any(Customer.class));
    }

    private Customer buildCustomer(String id) {
        Customer c = new Customer();
        c.setId(id);
        c.setName("John");
        c.setLastname("Doe");
        c.setDni("12345678");
        c.setEmail("john.doe@test.com");
        c.setPhone("555-1234");
        return c;
    }
}
