package jdrb.banco.simulador.service;

import jdrb.banco.simulador.model.Customer;

import java.util.List;

public interface CustomerService {
    boolean registerCustomer(Customer customer);
    Customer getCustomerById(Long id);
    List<Customer> getAllCustomers();
    boolean updateCustomer(Customer customer);
    boolean deleteCustomer(Long id);
    Customer getCustomerByEmail(String email);
    Customer login(String email, String passsword);
}
