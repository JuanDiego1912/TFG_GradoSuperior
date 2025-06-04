package jdrb.banco.simulador.dao.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.enums.AccountType;
import jdrb.banco.simulador.utils.MappingDBTables.AccountTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Repository
public class AccountDAOImpl implements AccountDAO {

    private final DataSource dataSource;

    @Autowired
    public AccountDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean registerAccount(Account account) {
        String sql = "INSERT INTO " + AccountTable.TABLE_NAME + " ("
                + AccountTable.ACCOUNT_NUMBER + ", "
                + AccountTable.CUSTOMER_ID + ", "
                + AccountTable.BALANCE + ", "
                + AccountTable.TYPE + ") VALUES (?, ?, ?, ?)";

        int rowsAffected = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (account.getCustomerId() == null) {
                throw new RuntimeException("Error. Cannot insert an account with a null or empty customer id");
            }
            if (account.getBalance() < 0.0) {
                throw new RuntimeException("Error. Cannot insert an account with a negative balance");
            }

            ps.setString(1, account.getAccountNumber());
            ps.setLong(2, account.getCustomerId());
            ps.setFloat(3, account.getBalance());
            ps.setString(4, account.getAccountType().name());
            rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error inserting account in database", sqlEx);
        }

        return rowsAffected > 0;
    }

    @Override
    public Account getAccountById(Long id) {
        String sql = "SELECT * FROM " + AccountTable.TABLE_NAME + " WHERE " + AccountTable.ID + " = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapAccountFromResultSet(
                        rs.getLong(AccountTable.ID),
                        rs.getString(AccountTable.ACCOUNT_NUMBER),
                        rs.getLong(AccountTable.CUSTOMER_ID),
                        rs.getFloat(AccountTable.BALANCE),
                        rs.getString(AccountTable.TYPE),
                        rs.getLong(AccountTable.CREATION_DATE)
                );
            }
            rs.close();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting account with id " + id + " from database", sqlEx);
        }

        return null;
    }

    @Override
    public List<Account> getAccountsByClient(Long customerId) {
        String sql = "SELECT * FROM " + AccountTable.TABLE_NAME + " WHERE " + AccountTable.CUSTOMER_ID + " = ?";
        List<Account> accounts = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ResultSet rs = ps.executeQuery();

            accounts = new LinkedList<>();
            while (rs.next()) {
                Account account = mapAccountFromResultSet(
                        rs.getLong(AccountTable.ID),
                        rs.getString(AccountTable.ACCOUNT_NUMBER),
                        rs.getLong(AccountTable.CUSTOMER_ID),
                        rs.getFloat(AccountTable.BALANCE),
                        rs.getString(AccountTable.TYPE),
                        rs.getLong(AccountTable.CREATION_DATE)
                );
                accounts.add(account);
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting accounts for customer with id " + customerId + " from database", sqlEx);
        }

        return accounts;
    }

    @Override
    public boolean updateAccount(Account account) {
        String sql = "UPDATE " + AccountTable.TABLE_NAME + " SET "
                + AccountTable.BALANCE + " = ?, "
                + AccountTable.TYPE + " = ? WHERE " + AccountTable.ID + " = ?";
        int accountUpdated = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setFloat(1, account.getBalance());
            ps.setString(2, account.getAccountType().name());
            ps.setLong(3, account.getId());

            accountUpdated = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error updating account data with id " + account.getId() + " in database", sqlEx);
        }

        return accountUpdated > 0;
    }

    @Override
    public boolean deleteAccount(Long id) {
        String sql = "DELETE FROM " + AccountTable.TABLE_NAME + " WHERE " + AccountTable.ID + " = ?";
        int accountDeleted = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            accountDeleted = ps.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error deleting account with id " + id + " from database", sqlEx);
        }

        return accountDeleted > 0;
    }

    @Override
    public boolean deleteAccountForClient(Long customerId, Long accountId) {
        String sql = "DELETE FROM " + AccountTable.TABLE_NAME + " WHERE " + AccountTable.CUSTOMER_ID + " = ? AND " + AccountTable.ID + " = ?";
        int accountDeleted = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ps.setLong(2, accountId);
            accountDeleted = ps.executeUpdate();

        }catch (SQLException sqlEx) {
            throw new RuntimeException("Error deleting account with id " + accountId + " for customer with id: " + customerId + " from database", sqlEx);
        }

        return accountDeleted > 0;
    }

    @Override
    public Account getAccountByAccountNumber(String accountNumber) {
        String sql = "SELECT " + AccountTable.ID + " FROM " + AccountTable.TABLE_NAME
                + " WHERE " + AccountTable.ACCOUNT_NUMBER + " = ?";

        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return getAccountById(rs.getLong(AccountTable.ID));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting account with account number " + accountNumber + " from database", e);
        }

        return null;
    }

    private Account mapAccountFromResultSet(Long id,
                                          String accountNumber,
                                          Long customerId,
                                          float balance,
                                          String type,
                                          Long creationDate) {
        Account account = new Account();
        account.setId(id);
        account.setAccountNumber(accountNumber);
        account.setCustomerId(customerId);
        account.setBalance(balance);

        try {
            account.setAccountType(AccountType.valueOf(type));
        } catch (IllegalArgumentException iae) {
            throw new RuntimeException("Invalid account type in database: " + type, iae);
        }

        account.setCreationDate(creationDate);
        return account;
    }
}