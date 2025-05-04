package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionDAO {
    Boolean insertTransaction(Transaction transaccion);
    Transaction getTransactionById(String id);
    List<Transaction> getTransactionsBySourceAccount(String idCuenta);
    List<Transaction> getTransactionsByDestinationAccount(String idCuenta);
    List<Transaction> getTransactionsBetweenDates(String idCuenta, Date desde, Date hasta);
}
