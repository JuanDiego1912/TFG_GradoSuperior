package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.dao.TransactionDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDAO transactionDAO;
    private final AccountDAO accountDAO;

    @Autowired
    public TransactionServiceImpl(TransactionDAO transactionDAO, AccountDAO accountDAO) {
        this.transactionDAO = transactionDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    public boolean registerTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new RuntimeException("Transaction cannot be null");
        }

        validateId(transaction.getId(), "Transaction ID");
        validateId(transaction.getOriginAccountId(), "Transaction origin account ID");
        validateId(transaction.getDestinationAccountId(), "Transaction destination account ID");

        if (transaction.getOriginAccountId().equals(transaction.getDestinationAccountId())) {
            throw new RuntimeException("Origin and destination accounts cannot be the same");
        }

        if (transaction.getAmount() < 0.0) {
            throw new RuntimeException("Transaction amount must be greater than 0");
        }

        Account originAccount = accountDAO.getAccountById(transaction.getOriginAccountId());
        if (originAccount == null) {
            throw new RuntimeException("Origin account does not exist");
        }

        Account destinationAccount = accountDAO.getAccountById(transaction.getDestinationAccountId());
        if (destinationAccount == null) {
            throw new RuntimeException("Destination account does not exist");
        }

        return transactionDAO.registerTransaction(transaction);
    }

    @Override
    public Transaction getTransactionById(Long id) {
        validateId(id, "Transaction ID");
        return transactionDAO.getTransactionById(id);
    }

    @Override
    public List<Transaction> getTransactionsBySourceAccount(Long accountId) {
        validateId(accountId, "Account ID");
        return transactionDAO.getTransactionsBySourceAccount(accountId);
    }

    @Override
    public List<Transaction> getTransactionsByDestinationAccount(Long accountId) {
        validateId(accountId, "Account ID");
        return transactionDAO.getTransactionsByDestinationAccount(accountId);
    }

    @Override
    public List<Transaction> getTransactionsBetweenDates(Long accountId, Date from, Date to) {
        validateId(accountId, "Account ID");
        if (from == null) {
            throw new IllegalArgumentException("From date cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("To date cannot be null");
        }
        if (from.after(to)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }
        return transactionDAO.getTransactionsBetweenDates(accountId, from, to);
    }

    private void validateId(Long id, String fieldname) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldname + " must be a positive number");
        }
    }
}
