package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.enums.CustomerStates;
import jdrb.banco.simulador.service.CustomerService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO customerDAO;

    @Autowired
    public CustomerServiceImpl(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public boolean registerCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null or empty");
        }
        validateId(customer.getId(), "Customer ID");
        customer.setPassword(BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt()));
        return customerDAO.registerCustomer(customer);
    }

    @Override
    public Customer getCustomerById(String id) {
        validateId(id, "Customer ID");
        return customerDAO.getCustomerById(id);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        validateId(customer.getId(), "Customer ID");
        customer.setPassword(BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt()));
        return customerDAO.updateCustomer(customer);
    }

    @Override
    public boolean deleteCustomer(String id) {
        validateId(id, "Customer ID");
        return customerDAO.deleteCustomer(id);
    }

    @Override
    public Customer login(String email, String passsword) {
        validateId(email, "Email");
        validateId(passsword, "Password");

        Customer customer = customerDAO.findByEmail(email);
        if (customer == null) {
            throw new IllegalArgumentException("No customer found with email: " + email);
        }

        if (!BCrypt.checkpw(passsword, customer.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        if (customer.getState() != CustomerStates.ACTIVE) {
            throw new IllegalArgumentException("Customer is not active");
        }

        return customer;
    }

    private void validateId(String id, String fieldname) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException(fieldname + " cannot be null or empty");
        }
    }
}
