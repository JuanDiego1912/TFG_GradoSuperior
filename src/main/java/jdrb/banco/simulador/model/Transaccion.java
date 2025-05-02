package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.TipoTransaccion;

import java.util.Date;

public class Transaccion {

    private String id;
    private String idCuentaOrigen;
    private String idCuentaDestino;
    private float monto;
    private TipoTransaccion tipo;
    private Long fecha;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCuentaOrigen() {
        return idCuentaOrigen;
    }

    public void setIdCuentaOrigen(String idCuentaOrigen) {
        this.idCuentaOrigen = idCuentaOrigen;
    }

    public String getIdCuentaDestino() {
        return idCuentaDestino;
    }

    public void setIdCuentaDestino(String idCuentaDestino) {
        this.idCuentaDestino = idCuentaDestino;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public Date getFechaDate() {
        return new Date(fecha);
    }
}
