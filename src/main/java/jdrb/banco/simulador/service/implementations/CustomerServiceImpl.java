package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerServiceImpl(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public boolean registerCustomer(Customer customer) {
        if (customer == null || customer.getId() == null || customer.getId().isEmpty()) {
            throw new IllegalArgumentException("Customer or customer ID cannot be null or empty");
        }
        return customerDAO.registerCustomer(customer);
    }

    @Override
    public Customer getCustomerById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        return customerDAO.getCustomerById(id);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        if (customer == null || customer.getId() == null || customer.getId().isEmpty()) {
            throw new IllegalArgumentException("Customer or customer ID cannot be null or empty");
        }
        return customerDAO.updateCustomer(customer);
    }

    @Override
    public boolean deleteCustomer(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        return customerDAO.deleteCustomer(id);
    }
}
