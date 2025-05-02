package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Transaccion;

import java.util.Date;
import java.util.List;

public interface TransaccionDAO {
    Boolean insertarTransaccion(Transaccion transaccion);
    Transaccion obtenerTransaccionPorId(String id);
    List<Transaccion> obtenerTransaccionesPorCuentaOrigen(String idCuenta);
    List<Transaccion> obtenerTransaccionesPorCuentaDestino(String idCuenta);
    List<Transaccion> obtenerTransaccionesEntreFechas(String idCuenta, Date desde, Date hasta);
}
