package jdrb.banco.simulador.dao.implementacion;

import jdrb.banco.simulador.dao.TransaccionDAO;
import jdrb.banco.simulador.model.Transaccion;
import jdrb.banco.simulador.model.enums.TipoTransaccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TransaccionDAOImpl implements TransaccionDAO {

    private Connection connection;

    public TransaccionDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Boolean insertarTransaccion(Transaccion transaccion) {
        String sql = "INSERT INTO transacciones VALUES (?, ?, ?, ?, ?, ?)";
        int transaccionInsertada = 0;

        if (transaccion.getId() == null) {
            throw new RuntimeException("Error al introducir la transaccion. La transaccion debe de tener un id");
        }
        if (transaccion.getIdCuentaDestino() == null) {
            throw new RuntimeException("Error al introducir la transaccion. La transaccion debe de tener un id de cuenta destino");
        }
        if (transaccion.getMonto() < 0.0) {
            throw new RuntimeException("Error al introducir la transaccion. El monto de la transaccion debe ser positivo");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, transaccion.getId());
            ps.setString(2, transaccion.getIdCuentaOrigen());
            ps.setString(3, transaccion.getIdCuentaDestino());
            ps.setFloat(4, transaccion.getMonto());
            ps.setString(5, transaccion.getTipo().name());
            ps.setLong(6, transaccion.getFecha());

            transaccionInsertada = ps.executeUpdate();

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al insertar la transacción con id: " + transaccion.getId() + " en la base de datos", sqlEx);
        }

        return transaccionInsertada > 0;
    }

    @Override
    public Transaccion obtenerTransaccionPorId(String id) {
        String sql = "SELECT * FROM transacciones WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearTransaccionDesdeResultSet(
                        rs.getString("id"),
                        rs.getString("id_origen"),
                        rs.getString("id_destino"),
                        rs.getFloat("monto"),
                        rs.getString("tipo"),
                        rs.getLong("fecha")
                );
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al obtener la transaccion con el id " + id + " de la base de datos", sqlEx);
        }

        return null;
    }

    @Override
    public List<Transaccion> obtenerTransaccionesPorCuentaOrigen(String idCuenta) {
        String sql = "SELECT * FROM transacciones WHERE id_origen = ?";

        return ejecutarConsultaPorCuenta(sql, idCuenta);
    }

    @Override
    public List<Transaccion> obtenerTransaccionesPorCuentaDestino(String idCuenta) {
        String sql = "SELECT * FROM transacciones WHERE id_destino = ?";

        return ejecutarConsultaPorCuenta(sql, idCuenta);
    }

    @Override
    public List<Transaccion> obtenerTransaccionesEntreFechas(String idCuenta, Date desde, Date hasta) {
        String sql = "SELECT * FROM transacciones WHERE (id_origen = ? OR id_destino = ?)  AND fecha >= ? AND fecha <= ?";
        List<Transaccion> transacciones = new LinkedList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, idCuenta);
            ps.setString(2, idCuenta);
            ps.setLong(3, desde.getTime());
            ps.setLong(4, hasta.getTime());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaccion transaccion = mapearTransaccionDesdeResultSet(
                        rs.getString("id"),
                        rs.getString("id_origen"),
                        rs.getString("id_destino"),
                        rs.getFloat("monto"),
                        rs.getString("tipo"),
                        rs.getLong("fecha")
                );
                transacciones.add(transaccion);
            }

        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al obtener las transacciones de la cuenta con id " + idCuenta + " " +
                    " entra la fecha: " + desde.getTime() + " hasta la fecha: " + hasta.getTime() + " de la base de datos", sqlEx);
        }

        return transacciones;
    }

    private List<Transaccion> ejecutarConsultaPorCuenta(String sql, String idCuenta) {
        List<Transaccion> transacciones = new LinkedList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, idCuenta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaccion transaccion = mapearTransaccionDesdeResultSet(
                        rs.getString("id"),
                        rs.getString("id_origen"),
                        rs.getString("id_destino"),
                        rs.getFloat("monto"),
                        rs.getString("tipo"),
                        rs.getLong("fecha")
                );
                transacciones.add(transaccion);
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Error al obtener las transacciones de la cuenta con id " + idCuenta + " de la base de datos", sqlEx);
        }

        return transacciones;
    }

    private Transaccion mapearTransaccionDesdeResultSet(String id,
                                                        String idOrigen,
                                                        String idDestino,
                                                        float monto,
                                                        String tipo,
                                                        Long fecha) {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(id);
        transaccion.setIdCuentaOrigen(idOrigen);
        transaccion.setIdCuentaDestino(idDestino);
        transaccion.setMonto(monto);

        try {
            transaccion.setTipo(TipoTransaccion.valueOf(tipo));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de transacción invalido en la base de datos: " + tipo, e);
        }

        transaccion.setFecha(fecha);
        return transaccion;
    }
}
