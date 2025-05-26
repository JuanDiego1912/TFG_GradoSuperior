package jdrb.banco.simulador.dao.implementations;

import jdrb.banco.simulador.dao.TransactionDAO;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.model.enums.TransactionStates;
import jdrb.banco.simulador.model.enums.TransactionType;
import jdrb.banco.simulador.utils.MappingDBTables.TransactionsTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Repository
public class TransactionDAOImpl implements TransactionDAO {

    private final DataSource dataSource;

    @Autowired
    public TransactionDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean registerTransaction(Transaction transaction) {
        String sql = "INSERT INTO " + TransactionsTable.TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        int transactionInserted = 0;

        if (transaction.getId() == null) {
            throw new RuntimeException("Error inserting transaction. Transaction must have an id");
        }
        if (transaction.getDestinationAccountId() == null) {
            throw new RuntimeException("Error inserting transaction. Transaction must have a destination account id");
        }
        if (transaction.getAmount() < 0.0) {
            throw new RuntimeException("Error inserting transaction. Transaction amount must be positive");
        }
        if (transaction.getState() == null) {
            throw new RuntimeException("Error inserting transaction. Transaction must have a state");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, transaction.getId());
            ps.setString(2, transaction.getOriginAccountId());
            ps.setString(3, transaction.getDestinationAccountId());
            ps.setFloat(4, transaction.getAmount());
            ps.setString(5, transaction.getType().name());
            ps.setLong(6, transaction.getTimestamp());
            ps.setString(7, transaction.getState().name());

            transactionInserted = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error inserting transaction with id: " + transaction.getId() + " in database", sqlEx);
        }

        return transactionInserted > 0;
    }

    @Override
    public Transaction getTransactionById(String id) {
        String sql = "SELECT * FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.ID + " = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapTransactionFromResultSet(
                        rs.getString(TransactionsTable.ID),
                        rs.getString(TransactionsTable.ORIGIN_ACCOUNT_ID),
                        rs.getString(TransactionsTable.DESTINATION_ACCOUNT_ID),
                        rs.getFloat(TransactionsTable.AMOUNT),
                        rs.getString(TransactionsTable.TYPE),
                        rs.getLong(TransactionsTable.TIMESTAMP),
                        rs.getString(TransactionsTable.STATE)
                );
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting transaction with id " + id + " from database", sqlEx);
        }

        return null;
    }

    @Override
    public List<Transaction> getTransactionsBySourceAccount(String accountId) {
        String sql = "SELECT * FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.ORIGIN_ACCOUNT_ID + " = ?";

        return executeQueryByAccount(sql, accountId);
    }

    @Override
    public List<Transaction> getTransactionsByDestinationAccount(String accountId) {
        String sql = "SELECT * FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.DESTINATION_ACCOUNT_ID + " = ?";

        return executeQueryByAccount(sql, accountId);
    }

    @Override
    public List<Transaction> getTransactionsBetweenDates(String accountId, Date from, Date to) {
        String sql = "SELECT * FROM " + TransactionsTable.TABLE_NAME + " WHERE (" + TransactionsTable.ORIGIN_ACCOUNT_ID + " = ? OR "
                + TransactionsTable.DESTINATION_ACCOUNT_ID + " = ?) AND "
                + TransactionsTable.TIMESTAMP + " >= ? AND " + TransactionsTable.TIMESTAMP + " <= ?";
        List<Transaction> transactions = new LinkedList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ps.setString(2, accountId);
            ps.setLong(3, from.getTime());
            ps.setLong(4, to.getTime());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction = mapTransactionFromResultSet(
                        rs.getString(TransactionsTable.ID),
                        rs.getString(TransactionsTable.ORIGIN_ACCOUNT_ID),
                        rs.getString(TransactionsTable.DESTINATION_ACCOUNT_ID),
                        rs.getFloat(TransactionsTable.AMOUNT),
                        rs.getString(TransactionsTable.TYPE),
                        rs.getLong(TransactionsTable.TIMESTAMP),
                        rs.getString(TransactionsTable.STATE)
                );
                transactions.add(transaction);
            }

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting transactions for account with id " + accountId + 
                    " between date: " + from.getTime() + " and date: " + to.getTime() + " from database", sqlEx);
        }

        return transactions;
    }

    private List<Transaction> executeQueryByAccount(String sql, String accountId) {
        List<Transaction> transactions = new LinkedList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction =mapTransactionFromResultSet(
                        rs.getString(TransactionsTable.ID),
                        rs.getString(TransactionsTable.ORIGIN_ACCOUNT_ID),
                        rs.getString(TransactionsTable.DESTINATION_ACCOUNT_ID),
                        rs.getFloat(TransactionsTable.AMOUNT),
                        rs.getString(TransactionsTable.TYPE),
                        rs.getLong(TransactionsTable.TIMESTAMP),
                        rs.getString(TransactionsTable.STATE)
                );
                transactions.add(transaction);
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting transactions for account with id " + accountId + " from database", sqlEx);
        }

        return transactions;
    }

    private Transaction mapTransactionFromResultSet(String id,
                                                  String originId,
                                                  String destinationId,
                                                  float amount,
                                                  String type,
                                                  Long date,
                                                  String state) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setOriginAccountId(originId);
        transaction.setDestinationAccountId(destinationId);
        transaction.setAmount(amount);

        try {
            transaction.setState(TransactionStates.valueOf(state.toUpperCase(java.util.Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            transaction.setState(TransactionStates.UNKNOWN);
        }

        try {
            transaction.setType(TransactionType.valueOf(type));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction type in database: " + type, e);
        }

        transaction.setTimestamp(date);
        return transaction;
    }
}