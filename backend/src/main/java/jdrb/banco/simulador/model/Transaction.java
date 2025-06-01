package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.TransactionStates;
import jdrb.banco.simulador.model.enums.TransactionType;
import org.jetbrains.annotations.*;

import java.util.Date;

public class Transaction {

    private Long id;

    private Long originAccountId;
    private Long destinationAccountId;
    private float amount;
    private TransactionType type;
    private Long timestamp;
    private TransactionStates state;

    public Transaction() {}

    public Transaction(Long id,
                       Long originAccountId,
                       Long destinationAccountId,
                       float transactionAmount,
                       TransactionType type,
                       Long timestamp,
                       TransactionStates state) {
        this.id = id;
        this.originAccountId = originAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = transactionAmount;
        this.type = type;
        this.timestamp = timestamp;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginAccountId() {
        return originAccountId;
    }

    public void setOriginAccountId(Long originAccountId) {
        this.originAccountId = originAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Date getFechaDate() {
        return new Date(timestamp);
    }

    public TransactionStates getState() {
        return state;
    }

    public void setState(TransactionStates state) {
        this.state = state;
    }
}
