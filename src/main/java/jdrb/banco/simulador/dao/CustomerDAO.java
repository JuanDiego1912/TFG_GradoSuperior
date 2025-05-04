package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Customer;

import java.util.List;

public interface CustomerDAO {
    Boolean insertCustomer(Customer cliente);
    Customer getCustomerById(String id);
    List<Customer> getAllCustomers();
    Boolean updateCustomer(Customer cliente);
    Boolean deleteCustomer(String id);
}
