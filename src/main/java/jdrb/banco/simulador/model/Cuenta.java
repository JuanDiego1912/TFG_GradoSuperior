package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.TipoCuenta;

import java.util.Date;

public class Cuenta {

    private String id;
    private String idCliente;
    private float saldo;
    private TipoCuenta tipo;
    private Date fechaCreacion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public TipoCuenta getTipoCuenta() {
        return tipo;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipo = tipoCuenta;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
