package jdrb.banco.simulador.dao.implementations;

import jdrb.banco.simulador.dao.CustomerDAO;
import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.model.enums.CustomerStates;
import jdrb.banco.simulador.utils.MappingDBTables.CustomersTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Repository
public class CustomerDAOImpl implements CustomerDAO {

    private final DataSource dataSource;

    @Autowired
    public CustomerDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean registerCustomer(Customer customer) {
        String sql = "INSERT INTO " + CustomersTable.TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int customerInserted = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customer.getId());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getLastname());
            statement.setString(4, customer.getDni());
            statement.setString(5, customer.getEmail());
            statement.setString(6, customer.getPhone());
            statement.setString(7, customer.getPassword());
            statement.setLong(8, customer.getCreationDate());
            statement.setString(9, customer.getState().name());

            customerInserted = statement.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error inserting customer in database", sqlEx);
        }

        return customerInserted > 0;
    }

    @Override
    public Customer getCustomerById(String id) {
        String sql = "SELECT * FROM " + CustomersTable.TABLE_NAME + " WHERE " + CustomersTable.ID + " = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            String[] customerStates = Arrays.stream(CustomerStates.values()).map(CustomerStates::name).toArray(String[]::new);

            if (rs.next()) {

                String state = rs.getString(CustomersTable.STATE);

                if (!Arrays.asList(customerStates).contains(state.toUpperCase(Locale.ROOT))) {
                    throw new IllegalArgumentException("Invalid state: " + state);
                }

                return setCustomerData(
                        rs.getString(CustomersTable.ID),
                        rs.getString(CustomersTable.NAME),
                        rs.getString(CustomersTable.LAST_NAME),
                        rs.getString(CustomersTable.DNI),
                        rs.getString(CustomersTable.EMAIL),
                        rs.getString(CustomersTable.PHONE),
                        rs.getString(CustomersTable.PASSWORD),
                        rs.getLong(CustomersTable.CREATION_DATE),
                        rs.getString(CustomersTable.STATE)
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

        String sql = "SELECT * FROM " + CustomersTable.TABLE_NAME;
        List<Customer> customers = new LinkedList<>();

        try (Connection connection = dataSource.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = setCustomerData(
                        rs.getString(CustomersTable.ID),
                        rs.getString(CustomersTable.NAME),
                        rs.getString(CustomersTable.LAST_NAME),
                        rs.getString(CustomersTable.DNI),
                        rs.getString(CustomersTable.EMAIL),
                        rs.getString(CustomersTable.PHONE),
                        rs.getString(CustomersTable.PASSWORD),
                        rs.getLong(CustomersTable.CREATION_DATE),
                        rs.getString(CustomersTable.STATE)
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
        String sql = "UPDATE " + CustomersTable.TABLE_NAME + " SET "
                + CustomersTable.NAME + " = ?, "
                + CustomersTable.LAST_NAME + " = ?, "
                + CustomersTable.DNI + " = ?, "
                + CustomersTable.EMAIL + " = ?, "
                + CustomersTable.PHONE + " = ?, "
                + CustomersTable.PASSWORD + " = ?, "
                + CustomersTable.STATE + " = ? WHERE " + CustomersTable.ID + " = ?";
        int customerUpdated = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getLastname());
            ps.setString(3, customer.getDni());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getPhone());
            ps.setString(6, customer.getPassword());
            ps.setString(7, customer.getState().name());
            ps.setString(8, customer.getId());

            customerUpdated = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error updating customer with id " + customer.getId() + " in database", sqlEx);
        }

        return customerUpdated > 0;
    }

    @Override
    public boolean deleteCustomer(String id) {
        String sql = "DELETE FROM " + CustomersTable.TABLE_NAME + " WHERE " + CustomersTable.ID + " = ?";
        int customerDeleted = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            customerDeleted = statement.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error deleting customer with id " + id + " from database", sqlEx);
        }

        return customerDeleted > 0;
    }

    @Override
    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM " + CustomersTable.TABLE_NAME + " WHERE " + CustomersTable.EMAIL + " = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return setCustomerData(
                        rs.getString(CustomersTable.ID),
                        rs.getString(CustomersTable.NAME),
                        rs.getString(CustomersTable.LAST_NAME),
                        rs.getString(CustomersTable.DNI),
                        rs.getString(CustomersTable.EMAIL),
                        rs.getString(CustomersTable.PHONE),
                        rs.getString(CustomersTable.PASSWORD),
                        rs.getLong(CustomersTable.CREATION_DATE),
                        rs.getString(CustomersTable.STATE)
                );
            }

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting customer with email " + email + " from database", sqlEx);
        }

        return null;
    }

    private Customer setCustomerData(String id,
                                   String name,
                                   String lastname,
                                   String dni,
                                   String email,
                                   String phone,
                                   String password,
                                   long registerDate,
                                   String state) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setLastname(lastname);
        customer.setDni(dni);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setCreationDate(registerDate);
        customer.setPassword(password);

        try {
            customer.setState(CustomerStates.valueOf(state.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException iae) {
            customer.setState(CustomerStates.UNKNOWN);
        }

        return customer;
    }
}