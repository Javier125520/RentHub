package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.enums.EstadoPago;
import org.example.renthub.model.enums.MetodoPago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    // =========================
    // SQL
    // =========================

    private static final String INSERT =
            "INSERT INTO pago (metodo, fecha_pago, monto, estado, id_reserva) VALUES (?, ?, ?, ?, ?)";

    private static final String DELETE =
            "DELETE FROM pago WHERE id = ?";

    // =========================
    // INSERT
    // =========================

    public boolean insert(Pago p) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getMetodo().name());
            ps.setTimestamp(2, Timestamp.valueOf(p.getFechaPago()));
            ps.setDouble(3, p.getMonto());
            ps.setString(4, p.getEstado().name());
            ps.setInt(5, p.getReserva().getIdReserva());

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
    // DELETE
    // =========================

    public boolean delete(int idPago) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;
        }
    }

}

