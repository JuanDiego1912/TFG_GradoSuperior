package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.service.AccountService;

import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;

    public AccountServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public boolean registerAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null or empty");
        }
        validateId(account.getId(), "Account ID");
        validateId(account.getCustomerId(), "Customer ID");
        return accountDAO.registerAccount(account);
    }

    @Override
    public Account getAccountById(String id) {
        validateId(id, "Account ID");
        return accountDAO.getAccountById(id);
    }

    @Override
    public List<Account> getAccountsByClient(String customerId) {
        validateId(customerId, "Customer ID");
        return accountDAO.getAccountsByClient(customerId);
    }

    @Override
    public boolean updateAccount(Account account) {
        if (account == null || account.getId() == null || account.getId().isEmpty()) {
            throw new IllegalArgumentException("Account or account ID cannot be null or empty");
        }
        return accountDAO.updateAccount(account);
    }

    @Override
    public boolean deleteAccount(String id) {
        validateId(id, "Account ID");
        return accountDAO.deleteAccount(id);
    }

    @Override
    public boolean deleteAccountForClient(String customerId, String accountId) {
        validateId(customerId, "Customer ID");
        validateId(accountId, "Account ID");
        return accountDAO.deleteAccountForClient(customerId, accountId);
    }

    private void validateId(String id, String fielname) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException(fielname + " cannot be null or empty");
        }
    }
}
