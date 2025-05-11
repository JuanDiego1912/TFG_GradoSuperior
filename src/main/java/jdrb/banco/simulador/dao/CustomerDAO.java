package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Customer;

import java.util.List;

public interface CustomerDAO {
    boolean registerCustomer(Customer cliente);
    Customer getCustomerById(String id);
    List<Customer> getAllCustomers();
    boolean updateCustomer(Customer cliente);
    boolean deleteCustomer(String id);
    Customer findByEmail(String email);
}
