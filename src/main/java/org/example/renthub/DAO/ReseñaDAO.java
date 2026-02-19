package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReseñaDAO {

    private final Connection conn;

    // =========================
    // SQL
    // =========================

    private static final String INSERT =
            "INSERT INTO reseña (puntuacion, comentario, fecha, id_inmueble, id_huesped) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE reseña SET puntuacion = ?, comentario = ?" +
                    "WHERE id_reseña = ?";

    private static final String DELETE =
            "DELETE FROM reseña WHERE id_reseña = ?";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM reseña WHERE id_inmueble = ? ORDER BY fecha DESC";

    private static final String SELECT_BY_USUARIO =
            """
            SELECT r.*,
                   i.id_inmueble,
                   i.titulo,
                   i.ciudad,
                   i.direccion
            FROM reseña r
            JOIN inmueble i ON r.id_inmueble = i.id_inmueble
            WHERE r.id_huesped = ?
            ORDER BY r.fecha DESC
            """;

    private static final String SELECT_BY_PROPIETARIO =
            "SELECT r.*, i.titulo, i.id_inmueble, i.ciudad, i.direccion," +
                    "u.nombre, u.correo " +
                    "FROM reseña r " +
                    "JOIN inmueble i ON r.id_inmueble = i.id_inmueble " +
                    "JOIN usuario u ON r.id_huesped = u.id_usuario " +
                    "WHERE i.id_propietario = ?";

    public ReseñaDAO() {
        this.conn = MySQLConnection.getConnection();
    }

    public ReseñaDAO(Connection conn) {
        this.conn = conn;
    }

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
            ps.setInt(3, r.getId());

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

    public List<Reseña> findByPropietario(int idPropietario) throws SQLException {
        List<Reseña> resenas = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_PROPIETARIO)) {
            ps.setInt(1, idPropietario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resenas.add(mapReseña(rs, null, null));
                }
            }
        }
        return resenas;
    }

    public int countByHuesped(int idHuesped) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reseña WHERE id_huesped = ?";
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHuesped);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countByPropietario(int idPropietario) throws SQLException {
        String sql = """
                SELECT COUNT(*) 
                FROM reseña r
                JOIN inmueble i ON r.id_inmueble = i.id_inmueble
                WHERE i.id_propietario = ?
                """;
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropietario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public Reseña findUltimaByPropietario(int idPropietario) throws SQLException {
        String sql = """
                SELECT r.*,
                       i.titulo, i.id_inmueble, i.ciudad, i.direccion,
                       u.nombre, u.correo
                FROM reseña r
                JOIN inmueble i ON r.id_inmueble = i.id_inmueble
                JOIN usuario u ON r.id_huesped = u.id_usuario
                WHERE i.id_propietario = ?
                ORDER BY r.fecha DESC
                LIMIT 1
                """;
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropietario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReseña(rs, null, null);
                }
            }
        }
        return null;
    }

    // =========================
    // MAPEADOR
    // =========================

    private Reseña mapReseña(ResultSet rs, Inmueble inmueble, Usuario usuario) throws SQLException {

        if (inmueble == null) {
            inmueble = new Inmueble();
            inmueble.setIdInmueble(rs.getInt("id_inmueble"));
            inmueble.setTitulo(rs.getString("titulo"));
            inmueble.setCiudad(rs.getString("ciudad"));
            inmueble.setDireccion(rs.getString("direccion"));
        }

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setIdUsuario(rs.getInt("id_huesped"));
            usuario.setNombre(rs.getString("nombre")); // si lo traes en el JOIN
        }

        return new Reseña(
                rs.getInt("id_reseña"),  // usa el nombre real de tu columna
                rs.getInt("puntuacion"),
                rs.getString("comentario"),
                rs.getDate("fecha").toLocalDate(),
                inmueble,
                usuario
        );
    }
}

