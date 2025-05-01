package jdrb.banco.simulador.dao;

import jdrb.banco.simulador.model.Cliente;

import java.util.List;

public interface ClienteDAO {
    void insertarCliente(Cliente cliente);
    Cliente obtenerClientePorId(String id);
    List<Cliente> obtenerClientes();
    void actualizarCliente(Cliente cliente);
    void eliminarCliente(String id);
}
