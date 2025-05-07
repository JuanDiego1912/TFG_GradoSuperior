package jdrb.banco.simulador.dao.implementations;

import jdrb.banco.simulador.dao.TransactionDAO;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.model.enums.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    private Connection connection;

    public TransactionDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean registerTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions VALUES (?, ?, ?, ?, ?, ?)";
        int transactionInserted = 0;

        if (transaction.getId() == null) {
            throw new RuntimeException("Error inserting transaction. Transaction must have an id");
        }
        if (transaction.getDestinationAccountId() == null) {
            throw new RuntimeException("Error inserting transaction. Transaction must have a destination account id");
        }
        if (transaction.getTransactionAmount() < 0.0) {
            throw new RuntimeException("Error inserting transaction. Transaction amount must be positive");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, transaction.getId());
            ps.setString(2, transaction.getOriginAccountId());
            ps.setString(3, transaction.getDestinationAccountId());
            ps.setFloat(4, transaction.getTransactionAmount());
            ps.setString(5, transaction.getType().name());
            ps.setLong(6, transaction.getDate());

            transactionInserted = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error inserting transaction with id: " + transaction.getId() + " in database", sqlEx);
        }

        return transactionInserted > 0;
    }

    @Override
    public Transaction getTransactionById(String id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapTransactionFromResultSet(
                        rs.getString("id"),
                        rs.getString("id_origen"),
                        rs.getString("id_destino"),
                        rs.getFloat("monto"),
                        rs.getString("tipo"),
                        rs.getLong("fecha")
                );
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error getting transaction with id " + id + " from database", sqlEx);
        }

        return null;
    }

    @Override
    public List<Transaction> getTransactionsBySourceAccount(String accountId) {
        String sql = "SELECT * FROM transactions WHERE id_origen = ?";

        return executeQueryByAccount(sql, accountId);
    }

    @Override
    public List<Transaction> getTransactionsByDestinationAccount(String accountId) {
        String sql = "SELECT * FROM transactions WHERE id_destino = ?";

        return executeQueryByAccount(sql, accountId);
    }

    @Override
    public List<Transaction> getTransactionsBetweenDates(String accountId, Date from, Date to) {
        String sql = "SELECT * FROM transactions WHERE (id_origen = ? OR id_destino = ?) AND fecha >= ? AND fecha <= ?";
        List<Transaction> transactions = new LinkedList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ps.setString(2, accountId);
            ps.setLong(3, from.getTime());
            ps.setLong(4, to.getTime());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction = mapTransactionFromResultSet(
                        rs.getString("id"),
                        rs.getString("id_origen"),
                        rs.getString("id_destino"),
                        rs.getFloat("monto"),
                        rs.getString("tipo"),
                        rs.getLong("fecha")
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

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction = mapTransactionFromResultSet(
                        rs.getString("id"),
                        rs.getString("id_origen"),
                        rs.getString("id_destino"),
                        rs.getFloat("monto"),
                        rs.getString("tipo"),
                        rs.getLong("fecha")
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
                                                  Long date) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setOriginAccountId(originId);
        transaction.setDestinationAccountId(destinationId);
        transaction.setTransactionAmount(amount);

        try {
            transaction.setType(TransactionType.valueOf(type));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction type in database: " + type, e);
        }

        transaction.setDate(date);
        return transaction;
    }
}