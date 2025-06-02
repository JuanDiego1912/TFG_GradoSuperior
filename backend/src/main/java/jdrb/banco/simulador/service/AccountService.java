package jdrb.banco.simulador.service;

import jdrb.banco.simulador.model.Account;

import java.util.List;

public interface AccountService {
    boolean registerAccount(Account account);
    Account getAccountById(Long id);
    List<Account> getAccountsByCustomerId(Long customerId);
    boolean updateAccount(Account account);
    boolean deleteAccount(Long id);
    boolean deleteAccountForClient(Long customerId, Long accountId);
}
