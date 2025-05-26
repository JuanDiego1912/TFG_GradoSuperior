package jdrb.banco.simulador.service;

import jdrb.banco.simulador.model.Customer;

import java.util.List;

public interface CustomerService {
    boolean registerCustomer(Customer customer);
    Customer getCustomerById(String id);
    List<Customer> getAllCustomers();
    boolean updateCustomer(Customer customer);
    boolean deleteCustomer(String id);
    Customer login(String email, String passsword);
}
