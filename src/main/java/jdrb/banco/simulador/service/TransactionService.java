package jdrb.banco.simulador.service;

import jdrb.banco.simulador.model.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    boolean registerTransaction(Transaction transaction);
    Transaction getTransactionById(String id);
    List<Transaction> getTransactionsBySourceAccount(String idCuenta);
    List<Transaction> getTransactionsByDestinationAccount(String idCuenta);
    List<Transaction> getTransactionsBetweenDates(String idCuenta, Date desde, Date hasta);
}
