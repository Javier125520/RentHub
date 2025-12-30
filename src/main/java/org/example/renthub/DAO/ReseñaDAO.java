package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReseñaDAO {

    // =========================
    // SQL
    // =========================

    private static final String INSERT =
            "INSERT INTO reseña (puntuacion, comentario, fecha, id_inmueble, id_usuario) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE reseña SET puntuacion = ?, comentario = ?, fecha = ?, id_inmueble = ?, id_usuario = ? " +
                    "WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM reseña WHERE id = ?";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM reseña WHERE id_inmueble = ? ORDER BY fecha DESC";

    private static final String SELECT_BY_USUARIO =
            "SELECT * FROM reseña WHERE id_usuario = ? ORDER BY fecha DESC";

    // =========================
    // INSERT
    // =========================

    public boolean insert(Reseña r) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getPuntuacion());
            ps.setString(2, r.getComentario());
            ps.setDate(3, Date.valueOf(r.getFecha()));
            ps.setInt(4, r.getInmueble().getIdInmueble());
            ps.setInt(5, r.getHuesped().getIdUsuario());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    r.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    // =========================
    // UPDATE
    // =========================

    public boolean update(Reseña r) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setInt(1, r.getPuntuacion());
            ps.setString(2, r.getComentario());
            ps.setDate(3, Date.valueOf(r.getFecha()));
            ps.setInt(4, r.getInmueble().getIdInmueble());
            ps.setInt(5, r.getHuesped().getIdUsuario());
            ps.setInt(6, r.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // DELETE
    // =========================

    public boolean delete(int idReseña) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idReseña);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // CONSULTAS
    // =========================

    public List<Reseña> findByInmueble(Inmueble inmueble) throws SQLException {
        List<Reseña> resenas = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resenas.add(mapReseña(rs, inmueble, null));
                }
            }
        }
        return resenas;
    }

    public List<Reseña> findByUsuario(Usuario usuario) throws SQLException {
        List<Reseña> resenas = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_USUARIO)) {
            ps.setInt(1, usuario.getIdUsuario());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resenas.add(mapReseña(rs, null, usuario));
                }
            }
        }
        return resenas;
    }

    // =========================
    // MAPEADOR
    // =========================

    private Reseña mapReseña(ResultSet rs, Inmueble inmueble, Usuario usuario) throws SQLException {

        if (inmueble == null) {
            inmueble = new Inmueble();
            inmueble.setIdInmueble(rs.getInt("id_inmueble"));
        }

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setIdUsuario(rs.getInt("id_usuario"));
        }

        return new Reseña(
                rs.getInt("id"),
                rs.getInt("puntuacion"),
                rs.getString("comentario"),
                rs.getDate("fecha").toLocalDate(),
                inmueble,
                usuario
        );
    }
}

