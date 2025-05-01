package jdrb.banco.simulador.dao.implementacion;

import jdrb.banco.simulador.dao.ClienteDAO;
import jdrb.banco.simulador.model.Cliente;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {
    private Connection connection;

    public ClienteDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getId());
            statement.setString(2, cliente.getNombre());
            statement.setString(3, cliente.getApellido());
            statement.setString(4, cliente.getDni());
            statement.setString(5, cliente.getEmail());
            statement.setString(6, cliente.getTelefono());
            statement.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al insertar el cliente en la base de datos", sqlEx);
        }
    }

    @Override
    public Cliente obtenerClientePorId(String id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return setDatosCliente(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("email"),
                        rs.getString("telefono")
                );
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al obtener el cliente con el id " + id + " de la base de datos", sqlEx);
        }
        return null;
    }

    @Override
    public List<Cliente> obtenerClientes() {
        String sql = "SELECT * FROM clientes";
        List<Cliente> clientes = null;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                clientes = new LinkedList<>();

                Cliente cliente = setDatosCliente(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("email"),
                        rs.getString("telefono")
                );

                clientes.add(cliente);
            }

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al obtener los clientes de la base de datos", sqlEx);
        }
        return clientes;
    }

    /* Este metodo recibira un cliente del cual sacaremos su ID y sus datos actualizados.
    * Sobre estos datos haremos la actualizacion de los campos en la base de datos.
    */
    @Override
    public void actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, dni = ?, email = ?, telefono = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {


        } catch (SQLException slEx) {

        }
    }

    @Override
    public void eliminarCliente(String id) {

    }

    private Cliente setDatosCliente(String id, String nombre, String apellido, String dni, String email, String telefono) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDni(dni);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        return cliente;
    }
}
