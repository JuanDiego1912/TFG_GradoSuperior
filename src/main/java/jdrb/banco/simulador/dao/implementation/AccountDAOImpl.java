package jdrb.banco.simulador.dao.implementation;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.enums.AccountType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    private Connection connection;

    public AccountDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Boolean insertAccount(Account account) {
        String sql = "INSERT INTO accounts VALUES (?, ?, ?, ?, ?)";
        int accountInserted = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            if (account.getCustomerId() == null) {
                throw new RuntimeException("Error. Cannot insert an account with a null or empty customer id");
            }
            if (account.getAccountBalance() < 0.0) {
                throw new RuntimeException("Error. Cannot insert an account with a negative balance");
            }

            ps.setString(1, account.getId());
            ps.setString(2, account.getCustomerId());
            ps.setFloat(3, account.getAccountBalance());
            ps.setString(4, account.getAccountType().name());
            ps.setLong(5, account.getCreationDate());
            accountInserted = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error inserting account in database", sqlEx);
        }

        return accountInserted > 0;
    }

    @Override
    public Account getAccountById(String id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapAccountFromResultSet(
                        rs.getString("id"),
                        rs.getString("id_cliente"),
                        rs.getFloat("saldo"),
                        rs.getString("tipo"),
                        rs.getLong("fecha_creacion")
                );
            }
            rs.close();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting account with id " + id + " from database", sqlEx);
        }

        return null;
    }

    @Override
    public List<Account> getAccountsByClient(String customerId) {
        String sql = "SELECT * FROM accounts WHERE id_cliente = ?";
        List<Account> accounts = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();

            accounts = new LinkedList<>();
            while (rs.next()) {
                Account account = mapAccountFromResultSet(
                        rs.getString("id"),
                        rs.getString("id_cliente"),
                        rs.getFloat("saldo"),
                        rs.getString("tipo"),
                        rs.getLong("fecha_creacion")
                );
                accounts.add(account);
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting accounts for customer with id " + customerId + " from database", sqlEx);
        }

        return accounts;
    }

    @Override
    public Boolean updateAccount(Account account) {
        String sql = "UPDATE accounts SET saldo = ?, tipo = ? WHERE id = ?";
        int accountUpdated = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setFloat(1, account.getAccountBalance());
            ps.setString(2, account.getAccountType().name());
            ps.setString(3, account.getId());

            accountUpdated = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error updating account data with id " + account.getId() + " in database", sqlEx);
        }

        return accountUpdated > 0;
    }

    @Override
    public Boolean deleteAccount(String id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        int accountDeleted = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            accountDeleted = ps.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error deleting account with id " + id + " from database", sqlEx);
        }

        return accountDeleted > 0;
    }

    @Override
    public Boolean deleteAccountForClient(String customerId, String accountId) {
        String sql = "DELETE FROM accounts WHERE id_cliente = ? AND id = ?";
        int accountDeleted = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, customerId);
            ps.setString(2, accountId);
            accountDeleted = ps.executeUpdate();

        }catch (SQLException sqlEx) {
            throw new RuntimeException("Error deleting account with id " + accountId + " for customer with id: " + customerId + " from database", sqlEx);
        }

        return accountDeleted > 0;
    }

    private Account mapAccountFromResultSet(String id,
                                          String customerId,
                                          float balance,
                                          String type,
                                          Long creationDate) {
        Account account = new Account();
        account.setId(id);
        account.setCustomerId(customerId);
        account.setAccountBalance(balance);

        try {
            account.setAccountType(AccountType.valueOf(type));
        } catch (IllegalArgumentException iae) {
            throw new RuntimeException("Invalid account type in database: " + type, iae);
        }

        account.setCreationDate(creationDate);
        return account;
    }
}