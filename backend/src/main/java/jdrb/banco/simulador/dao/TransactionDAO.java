package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionDAO {
    boolean registerTransaction(Transaction transaccion);
    Transaction getTransactionById(Long id);
    List<Transaction> getTransactionsBySourceAccount(Long idCuenta);
    List<Transaction> getTransactionsByDestinationAccount(Long idCuenta);
    List<Transaction> getTransactionsBetweenDates(Long idCuenta, Date desde, Date hasta);
}
