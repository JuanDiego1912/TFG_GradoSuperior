package jdrb.banco.simulador.service;

import jdrb.banco.simulador.model.Account;

import java.util.List;

public interface AccountService {
    boolean registerAccount(Account account);
    Account getAccountById(String id);
    List<Account> getAccountsByClient(String customerId);
    boolean updateAccount(Account account);
    boolean deleteAccount(String id);
    boolean deleteAccountForClient(String customerId, String accountId);
}
