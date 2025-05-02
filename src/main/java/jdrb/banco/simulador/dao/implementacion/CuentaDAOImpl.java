package jdrb.banco.simulador.dao.implementacion;

import jdrb.banco.simulador.dao.CuentaDAO;
import jdrb.banco.simulador.model.Cuenta;
import jdrb.banco.simulador.model.enums.TipoCuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CuentaDAOImpl implements CuentaDAO {

    private Connection connection;

    public CuentaDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Boolean insertarCuenta(Cuenta cuenta) {
        String sql = "INSERT INTO cuentas VALUES (?, ?, ?, ?, ?)";
        int cuentaInsertada = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            if (cuenta.getIdCliente() == null) {
                throw new RuntimeException("\"Error. No puedes insertar una cuenta con un id de cliente nulo o vacío");
            }
            if (cuenta.getSaldo() < 0.0) {
                throw new RuntimeException("Error. No puedes insertar una cuenta con un saldo negativo");
            }

            ps.setString(1, cuenta.getId());
            ps.setString(2, cuenta.getIdCliente());
            ps.setFloat(3, cuenta.getSaldo());
            ps.setString(4, cuenta.getTipoCuenta().name());
            ps.setLong(5, cuenta.getFechaCreacion());
            cuentaInsertada = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al insertar la cuenta en la base de datos", sqlEx);
        }

        return cuentaInsertada > 0;
    }

    @Override
    public Cuenta obtenerCuentaPorId(String id) {
        String sql = "SELECT * FROM cuentas WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearCuentaDesdeResultSet(
                        rs.getString("id"),
                        rs.getString("id_cliente"),
                        rs.getFloat("saldo"),
                        rs.getString("tipo"),
                        rs.getLong("fecha_creacion")
                );
            }
            rs.close();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al obtener la cuenta con el id " + id + " de la base de datos", sqlEx);
        }

        return null;
    }

    @Override
    public List<Cuenta> obtenerCuentasPorCliente(String idCliente) {
        String sql = "SELECT * FROM cuentas WHERE id_cliente = ?";
        List<Cuenta> cuentas = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, idCliente);
            ResultSet rs = ps.executeQuery();

            cuentas = new LinkedList<>();
            while (rs.next()) {
                Cuenta cuenta = mapearCuentaDesdeResultSet(
                        rs.getString("id"),
                        rs.getString("id_cliente"),
                        rs.getFloat("saldo"),
                        rs.getString("tipo"),
                        rs.getLong("fecha_creacion")
                );
                cuentas.add(cuenta);
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al obtener las cuentas del cliente con id " + idCliente + " de la base de datos", sqlEx);
        }

        return cuentas;
    }

    @Override
    public Boolean actualizarCuenta(Cuenta cuenta) {
        String sql = "UPDATE cuentas SET saldo = ?, tipo = ? WHERE id = ?";
        int cuentaActualizada = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setFloat(1, cuenta.getSaldo());
            ps.setString(2, cuenta.getTipoCuenta().name());
            ps.setString(3, cuenta.getId());

            cuentaActualizada = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al actualizar los datos de la cuenta con id " + cuenta.getId() + " en la base de datos", sqlEx);
        }

        return cuentaActualizada > 0;
    }

    @Override
    public Boolean eliminarCuenta(String id) {
        String sql = "DELETE FROM cuentas WHERE id = ?";
        int cuentaEliminada = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            cuentaEliminada = ps.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al eliminar la cuenta con el id " + id + " de la base de datos", sqlEx);
        }

        return cuentaEliminada > 0;
    }

    @Override
    public Boolean eliminarCuentaParaCliente(String idCliente, String idCuenta) {
        String sql = "DELETE FROM cuentas WHERE id_cliente = ? AND id = ?";
        int cuentaEliminada = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, idCliente);
            ps.setString(2, idCuenta);
            cuentaEliminada = ps.executeUpdate();

        }catch (SQLException sqlEx) {
            throw new RuntimeException("Error al eliminar la cuenta con el id " + idCuenta + " para el cliente con id: " + idCliente + " de la base de datos", sqlEx);
        }

        return cuentaEliminada > 0 ;
    }

    private Cuenta mapearCuentaDesdeResultSet(String id,
                                  String idCliente,
                                  float saldo,
                                  String tipo,
                                  Long fechaCreacion) {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(id);
        cuenta.setIdCliente(idCliente);
        cuenta.setSaldo(saldo);

        try {
            cuenta.setTipoCuenta(TipoCuenta.valueOf(tipo));
        } catch (IllegalArgumentException iae) {
            throw new RuntimeException("Tipo de cuenta inválido en la base de datos: " + tipo, iae);
        }

        cuenta.setFechaCreacion(fechaCreacion);
        return cuenta;
    }


}
