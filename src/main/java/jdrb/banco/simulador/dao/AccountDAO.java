package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Account;

import java.util.List;

public interface AccountDAO {
    boolean registerAccount(Account account);
    Account getAccountById(String id);
    List<Account> getAccountsByClient(String idCliente);
    boolean updateAccount(Account cuenta);
    boolean deleteAccount(String id);
    boolean deleteAccountForClient(String idCliente, String idCuenta);
}
