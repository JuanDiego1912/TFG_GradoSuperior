package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.enums.AccountType;
import jdrb.banco.simulador.model.enums.CustomerStates;
import jdrb.banco.simulador.service.CustomerService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO customerDAO;
    private final AccountDAO accountDAO;

    @Autowired
    public CustomerServiceImpl(CustomerDAO customerDAO, AccountDAO accountDAO) {
        this.customerDAO = customerDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    public boolean registerCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        validateField(customer.getEmail(), "Email");
        validateField(customer.getPassword(), "Password");

        customer.setPassword(BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt()));
        boolean registered = customerDAO.registerCustomer(customer);

        if (registered) {
            Account account = new Account();
            account.setCustomerId(customer.getId());
            account.setAccountNumber(generateAccountNumber());
            account.setBalance(0.0f);
            account.setAccountType(AccountType.CURRENT);

            accountDAO.registerAccount(account);
        }

        return registered;
    }

    @Override
    public Customer getCustomerById(Long id) {
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

        if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
            customer.setPassword(BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt()));
        }

        return customerDAO.updateCustomer(customer);
    }

    @Override
    public boolean deleteCustomer(Long id) {
        validateId(id, "Customer ID");
        return customerDAO.deleteCustomer(id);
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        validateField(email, "Email");
        return customerDAO.getCustomerByEmail(email);
    }

    @Override
    public Customer login(String email, String password) {
        validateField(email, "Email");
        validateField(password, "Password");

        Optional<Customer> optionalCustomer = Optional.ofNullable(customerDAO.getCustomerByEmail(email));

        if (optionalCustomer.isEmpty()) {
            throw new IllegalArgumentException("The email is not registered or the password is incorrect.");
        }

        Customer customer = optionalCustomer.get();

        if (!BCrypt.checkpw(password, customer.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        if (customer.getState() != CustomerStates.ACTIVE) {
            throw new IllegalArgumentException("Customer is not active");
        }

        return customer;
    }

    private void validateId(Long id, String fieldname) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldname + " must be a positive number");
        }
    }

    private void validateField(String value, String fieldname) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldname + " cannot be null or empty");
        }
    }

    private String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder("ES");
        for (int i = 0; i < 22; i++) {
            accountNumber.append((int) (Math.random() * 10));
        }
        return accountNumber.toString();
    }
}
