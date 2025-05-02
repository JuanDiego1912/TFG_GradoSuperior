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
    public Boolean insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes VALUES (?, ?, ?, ?, ?, ?)";
        int clienteInsertado = 0;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cliente.getId());
            statement.setString(2, cliente.getNombre());
            statement.setString(3, cliente.getApellido());
            statement.setString(4, cliente.getDni());
            statement.setString(5, cliente.getEmail());
            statement.setString(6, cliente.getTelefono());
            clienteInsertado = statement.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al insertar el cliente en la base de datos", sqlEx);
        }

        return clienteInsertado > 0;
    }

    @Override
    public Cliente obtenerClientePorId(String id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

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
            rs.close();
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al obtener el cliente con el id " + id + " de la base de datos", sqlEx);
        }
        return null;
    }

    @Override
    public List<Cliente> obtenerClientes() {

        String sql = "SELECT * FROM clientes";
        List<Cliente> clientes = new LinkedList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
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

    @Override
    public Boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, dni = ?, email = ?, telefono = ? WHERE id = ?";
        int clienteActualizado = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getId());

            clienteActualizado = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al actualizar el cliente con el id " + cliente.getId() + " en la base de datos", sqlEx);
        }

        return clienteActualizado > 0;
    }

    @Override
    public Boolean eliminarCliente(String id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        int clienteEliminado = 0;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            clienteEliminado = statement.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al eliminar el cliente con el id " + id + " de la base de datos", sqlEx);
        }

        return clienteEliminado > 0;
    }

    private Cliente setDatosCliente(String id,
                                    String nombre,
                                    String apellido,
                                    String dni,
                                    String email,
                                    String telefono) {
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
