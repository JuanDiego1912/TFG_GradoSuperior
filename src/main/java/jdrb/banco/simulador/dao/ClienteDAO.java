package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Cliente;

import java.util.List;

public interface ClienteDAO {
    Boolean insertarCliente(Cliente cliente);
    Cliente obtenerClientePorId(String id);
    List<Cliente> obtenerClientes();
    Boolean actualizarCliente(Cliente cliente);
    Boolean eliminarCliente(String id);
}
