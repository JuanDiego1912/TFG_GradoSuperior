package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Account;

import java.util.List;

public interface AccountDAO {
    boolean registerAccount(Account account);
    Account getAccountById(Long id);
    List<Account> getAccountsByClient(Long idCliente);
    boolean updateAccount(Account cuenta);
    boolean deleteAccount(Long id);
    boolean deleteAccountForClient(Long idCliente, Long idCuenta);
}
