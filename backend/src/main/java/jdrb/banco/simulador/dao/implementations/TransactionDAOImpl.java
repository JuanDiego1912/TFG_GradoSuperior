package jdrb.banco.simulador.dao.implementations;

import jdrb.banco.simulador.dao.TransactionDAO;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.model.enums.TransactionStates;
import jdrb.banco.simulador.model.enums.TransactionType;
import jdrb.banco.simulador.utils.MappingDBTables.TransactionsTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
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
        String sql = "INSERT INTO " + TransactionsTable.TABLE_NAME + " ("
                + TransactionsTable.ORIGIN_ACCOUNT_ID + ", "
                + TransactionsTable.DESTINATION_ACCOUNT_ID + ", "
                + TransactionsTable.AMOUNT + ", "
                + TransactionsTable.TYPE + ","
                + TransactionsTable.STATE + ") VALUES (?, ?, ?, ?, ?);";
        int transactionInserted = 0;

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
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, transaction.getOriginAccountId());
            ps.setLong(2, transaction.getDestinationAccountId());
            ps.setFloat(3, transaction.getAmount());
            ps.setString(4, transaction.getType().name());
            ps.setString(5, transaction.getState().name());

            transactionInserted = ps.executeUpdate();

            if (transactionInserted > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error inserting transaction with id: " + transaction.getId() + " in database", sqlEx);
        }

        return transactionInserted > 0;
    }

    @Override
    public Transaction getTransactionById(Long id) {
        String sql = "SELECT * FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.ID + " = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapTransactionFromResultSet(
                        rs.getLong(TransactionsTable.ID),
                        rs.getLong(TransactionsTable.ORIGIN_ACCOUNT_ID),
                        rs.getLong(TransactionsTable.DESTINATION_ACCOUNT_ID),
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
    public List<Transaction> getTransactionsBySourceAccount(Long accountId) {
        String sql = "SELECT * FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.ORIGIN_ACCOUNT_ID + " = ?";

        return executeQueryByAccount(sql, accountId);
    }

    @Override
    public List<Transaction> getTransactionsByDestinationAccount(Long accountId) {
        String sql = "SELECT * FROM " + TransactionsTable.TABLE_NAME + " WHERE " + TransactionsTable.DESTINATION_ACCOUNT_ID + " = ?";

        return executeQueryByAccount(sql, accountId);
    }

    @Override
    public List<Transaction> getTransactionsBetweenDates(Long accountId, Date from, Date to) {
        String sql = "SELECT * FROM " + TransactionsTable.TABLE_NAME + " WHERE (" + TransactionsTable.ORIGIN_ACCOUNT_ID + " = ? OR "
                + TransactionsTable.DESTINATION_ACCOUNT_ID + " = ?) AND "
                + TransactionsTable.TIMESTAMP + " >= ? AND " + TransactionsTable.TIMESTAMP + " <= ?";
        List<Transaction> transactions = new LinkedList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, accountId);
            ps.setLong(2, accountId);
            ps.setLong(3, from.getTime());
            ps.setLong(4, to.getTime());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction = mapTransactionFromResultSet(
                        rs.getLong(TransactionsTable.ID),
                        rs.getLong(TransactionsTable.ORIGIN_ACCOUNT_ID),
                        rs.getLong(TransactionsTable.DESTINATION_ACCOUNT_ID),
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

    private List<Transaction> executeQueryByAccount(String sql, Long accountId) {
        List<Transaction> transactions = new LinkedList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, accountId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction =mapTransactionFromResultSet(
                        rs.getLong(TransactionsTable.ID),
                        rs.getLong(TransactionsTable.ORIGIN_ACCOUNT_ID),
                        rs.getLong(TransactionsTable.DESTINATION_ACCOUNT_ID),
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

    private Transaction mapTransactionFromResultSet(Long id,
                                                    Long originId,
                                                    Long destinationId,
                                                  float amount,
                                                  String type,
                                                  Long timestamp,
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

        transaction.setTimestamp(timestamp);
        return transaction;
    }
}