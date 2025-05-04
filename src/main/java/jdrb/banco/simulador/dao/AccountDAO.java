package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Account;

import java.util.List;

public interface AccountDAO {
    Boolean insertAccount(Account account);
    Account getAccountById(String id);
    List<Account> getAccountsByClient(String idCliente);
    Boolean updateAccount(Account cuenta);
    Boolean deleteAccount(String id);
    Boolean deleteAccountForClient(String idCliente, String idCuenta);
}
