package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Cuenta;

import java.util.List;

public interface CuentaDAO {
    Boolean insertarCuenta(Cuenta cuenta);
    Cuenta obtenerCuentaPorId(String id);
    List<Cuenta> obtenerCuentasPorCliente(String idCliente);
    Boolean actualizarCuenta(Cuenta cuenta);
    Boolean eliminarCuenta(String id);
    Boolean eliminarCuentaParaCliente(String idCliente, String idCuenta);
}
