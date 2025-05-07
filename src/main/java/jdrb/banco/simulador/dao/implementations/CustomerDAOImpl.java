package jdrb.banco.simulador.dao.implementations;

import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.enums.CustomerStates;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CustomerDAOImpl implements CustomerDAO {

    private Connection connection;

    public CustomerDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean registerCustomer(Customer customer) {
        String sql = "INSERT INTO customers VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int customerInserted = 0;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customer.getId());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getLastname());
            statement.setString(4, customer.getDni());
            statement.setString(5, customer.getEmail());
            statement.setString(6, customer.getCustomerPhone());
            statement.setLong(7, customer.getRegistrationDate());
            statement.setString(8, customer.getState().name());
            customerInserted = statement.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error inserting customer in database", sqlEx);
        }

        return customerInserted > 0;
    }

    @Override
    public Customer getCustomerById(String id) {
        String sql = "SELECT * FROM customers WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            String[] customerStates = Arrays.stream(CustomerStates.values()).map(CustomerStates::name).toArray(String[]::new);

            if (rs.next()) {

                String state = rs.getString("estado");

                if (!Arrays.asList(customerStates).contains(state.toUpperCase(Locale.ROOT))) {
                    throw new IllegalArgumentException("Invalid state: " + state);
                }

                return setCustomerData(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("email"),
                        rs.getString("telefono"),
                        rs.getLong("fecha_registro"),
                        rs.getString("estado")
                );
            }
            rs.close();
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting customer with id " + id + " from database", sqlEx);
        }
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {

        String sql = "SELECT * FROM customers";
        List<Customer> customers = new LinkedList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = setCustomerData(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("email"),
                        rs.getString("telefono"),
                        rs.getLong("fecha_registro"),
                        rs.getString("estado")
                );
                customers.add(customer);
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting customers from database", sqlEx);
        }

        return customers;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET nombre = ?, apellido = ?, dni = ?, email = ?, telefono = ?, estado = ? WHERE id = ?";
        int customerUpdated = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getLastname());
            ps.setString(3, customer.getDni());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getCustomerPhone());
            ps.setString(6, customer.getState().name());
            ps.setString(7, customer.getId());

            customerUpdated = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error updating customer with id " + customer.getId() + " in database", sqlEx);
        }

        return customerUpdated > 0;
    }

    @Override
    public boolean deleteCustomer(String id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        int customerDeleted = 0;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            customerDeleted = statement.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error deleting customer with id " + id + " from database", sqlEx);
        }

        return customerDeleted > 0;
    }

    private Customer setCustomerData(String id,
                                   String name,
                                   String lastname,
                                   String dni,
                                   String email,
                                   String phone,
                                   Long registerDate,
                                   String state) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setLastname(lastname);
        customer.setDni(dni);
        customer.setEmail(email);
        customer.setCustomerPhone(phone);
        customer.setRegistrationDate(registerDate);

        try {
            customer.setState(CustomerStates.valueOf(state.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException iae) {
            customer.setState(CustomerStates.UNKNOWN);
        }

        return customer;
    }
}