package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Enum.EstadoPago;
import org.example.renthub.model.Enum.MetodoPago;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    // =========================
    // SQL
    // =========================

    private static final String INSERT =
            "INSERT INTO pago (metodo, fecha_pago, monto, estado) VALUES (?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE pago SET metodo = ?, fecha_pago = ?, monto = ?, estado = ? WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM pago WHERE id = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM pago WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM pago";

    // =========================
    // INSERT
    // =========================

    public boolean insert(Pago p) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getMetodo().name());
            ps.setDate(2, Date.valueOf(p.getFechaPago()));
            ps.setDouble(3, p.getMonto());
            ps.setString(4, p.getEstado().name());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    // =========================
    // UPDATE
    // =========================

    public boolean update(Pago p) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, p.getMetodo().name());
            ps.setDate(2, Date.valueOf(p.getFechaPago()));
            ps.setDouble(3, p.getMonto());
            ps.setString(4, p.getEstado().name());
            ps.setInt(5, p.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // DELETE
    // =========================

    public boolean delete(int idPago) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // CONSULTAS
    // =========================

    public Pago findById(int idPago) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, idPago);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapPago(rs);
            }
        }
        return null;
    }

    public List<Pago> findAll() throws SQLException {
        List<Pago> pagos = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pagos.add(mapPago(rs));
            }
        }
        return pagos;
    }

    // =========================
    // MAPEADOR
    // =========================

    private Pago mapPago(ResultSet rs) throws SQLException {
        return new Pago(
                rs.getInt("id"),
                MetodoPago.valueOf(rs.getString("metodo")),
                rs.getDate("fecha_pago").toLocalDate(),
                rs.getDouble("monto"),
                EstadoPago.valueOf(rs.getString("estado"))
        );
    }
}

