package org.example.renthub.DAO;

import org.example.renthub.model.ServicioExtra;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioExtraDAO {

    private final Connection conn;

    // =========================
    // SQL
    // =========================
    private static final String INSERT =
            "INSERT INTO servicio_extra (nombre, descripcion) VALUES (?, ?)";

    private static final String UPDATE =
            "UPDATE servicio_extra SET nombre = ?, descripcion = ? WHERE id_servicio = ?";

    private static final String DELETE =
            "DELETE FROM servicio_extra WHERE id_servicio = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM servicio_extra WHERE id_servicio = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM servicio_extra";

    // =========================
    // Constructor
    // =========================
    public ServicioExtraDAO() {
        this.conn = MySQLConnection.getConnection();
    }

    // =========================
    // CRUD
    // =========================

    public boolean insert(ServicioExtra s) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    s.setIdServicio(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(ServicioExtra s) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setInt(3, s.getIdServicio());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int idServicio) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idServicio);
            return ps.executeUpdate() > 0;
        }
    }

    public List<ServicioExtra> findAll() throws SQLException {
        List<ServicioExtra> servicios = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                servicios.add(mapServicio(rs));
            }
        }
        return servicios;
    }

    // =========================
    // MAPPER
    // =========================

    private ServicioExtra mapServicio(ResultSet rs) throws SQLException {
        return new ServicioExtra(
                rs.getInt("id_servicio"),
                rs.getString("nombre"),
                rs.getString("descripcion")
        );
    }
}


