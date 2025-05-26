package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.AccountType;

public class Account {

    private String id;
    private String customerId;
    private float balance;
    private AccountType type;
    private long creationDate;

    public Account() {}

    public Account(String id,
                   String customerId,
                   float accountBalance,
                   AccountType type,
                   long creationDate) {
        this.id = id;
        this.customerId = customerId;
        this.balance = accountBalance;
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

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return type;
    }

    public void setAccountType(AccountType tipoCuenta) {
        this.type = tipoCuenta;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
