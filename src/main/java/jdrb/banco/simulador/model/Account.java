package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.AccountType;

public class Account {

    private String id;
    private String customerId;
    private float accountBalance;
    private AccountType type;
    private Long creationDate;

    public Account() {}

    public Account(String id, String customerId, float accountBalance, AccountType type, Long creationDate) {
        this.id = id;
        this.customerId = customerId;
        this.accountBalance = accountBalance;
        this.type = type;
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public float getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(float accountBalance) {
        this.accountBalance = accountBalance;
    }

    public AccountType getAccountType() {
        return type;
    }

    public void setAccountType(AccountType tipoCuenta) {
        this.type = tipoCuenta;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }
}
