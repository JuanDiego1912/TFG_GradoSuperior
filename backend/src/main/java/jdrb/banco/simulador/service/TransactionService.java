package jdrb.banco.simulador.service;

import jdrb.banco.simulador.model.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    boolean registerTransaction(Transaction transaction);
    Transaction getTransactionById(Long id);
    List<Transaction> getTransactionsBySourceAccount(Long idCuenta);
    List<Transaction> getTransactionsByDestinationAccount(Long idCuenta);
    List<Transaction> getTransactionsBetweenDates(Long idCuenta, Date desde, Date hasta);
}
