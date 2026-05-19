package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para las valoraciones y reseñas (Opiniones).
 */
public class ReseñaDAO extends Reseña {

    // =========================================================================
    // SENTENCIAS SQL
    // =========================================================================
    private static final String INSERT =
            "INSERT INTO reseña (puntuacion, comentario, fecha, id_inmueble, id_huesped) VALUES (?, ?, ?, ?, ?);";

    private static final String UPDATE =
            "UPDATE reseña SET puntuacion = ?, comentario = ? WHERE id_reseña = ?;";

    private static final String DELETE =
            "DELETE FROM reseña WHERE id_reseña = ?;";

    private static final String SELECT_BY_USUARIO =
            """
            SELECT r.*, i.id_inmueble, i.titulo, i.ciudad, i.direccion
            FROM reseña r
            JOIN inmueble i ON r.id_inmueble = i.id_inmueble
            WHERE r.id_huesped = ?
            ORDER BY r.fecha DESC;
            """;

    private static final String SELECT_BY_PROPIETARIO =
            """
            SELECT r.*, i.titulo, i.id_inmueble, i.ciudad, i.direccion, u.nombre, u.correo 
            FROM reseña r 
            JOIN inmueble i ON r.id_inmueble = i.id_inmueble 
            JOIN usuario u ON r.id_huesped = u.id_usuario 
            WHERE i.id_propietario = ?;
            """;

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================
    public ReseñaDAO() {
        super();
    }

    /** Constructor por copia utilizado en los paneles de edición de comentarios */
    public ReseñaDAO(Reseña r) {
        super();
        this.setId(r.getId());
        this.setPuntuacion(r.getPuntuacion());
        this.setComentario(r.getComentario());
        this.setFecha(r.getFecha());
        this.setInmueble(r.getInmueble());
        this.setHuesped(r.getHuesped());
    }

    // =========================================================================
    // MÉTODOS CRUD DE INSTANCIA (Active Record)
    // =========================================================================

    /** Publica una reseña asociándole la fecha del sistema actual */
    public boolean insert() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, this.getPuntuacion());
            ps.setString(2, this.getComentario());
            ps.setDate(3, Date.valueOf(this.getFecha()));
            ps.setInt(4, this.getInmueble().getIdInmueble());
            ps.setInt(5, this.getHuesped().getIdUsuario());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /** Modifica la puntuación de estrellas y descripción de una reseña */
    public boolean update() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setInt(1, this.getPuntuacion());
            ps.setString(2, this.getComentario());
            ps.setInt(3, this.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina la reseña */
    public boolean remove() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, this.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA ESTÁTICOS
    // =========================================================================

    /** Borrado estático por ID */
    public static boolean delete(int idReseña) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idReseña);
            return ps.executeUpdate() > 0;
        }
    }

    /** Devuelve el listado de opiniones emitidas por un Huésped */
    public static List<Reseña> findByUsuario(Usuario usuario) throws SQLException {
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

    /** Devuelve las opiniones que han recibido los alojamientos de un propietario */
    public static List<Reseña> findByPropietario(int idPropietario) throws SQLException {
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

    /** Cuenta las valoraciones totales hechas por un huésped */
    public static int countByHuesped(int idHuesped) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reseña WHERE id_huesped = ?;";
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHuesped);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Cuenta las valoraciones totales que ha recibido un arrendador en sus inmuebles */
    public static int countByPropietario(int idPropietario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reseña r JOIN inmueble i ON r.id_inmueble = i.id_inmueble WHERE i.id_propietario = ?;";
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropietario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Obtiene la última reseña entrante para refrescar los datos del feed del Dashboard */
    public static Reseña findUltimaByPropietario(int idPropietario) throws SQLException {
        String sql = """
                SELECT r.*, i.titulo, i.id_inmueble, i.ciudad, i.direccion, u.nombre, u.correo
                FROM reseña r
                JOIN inmueble i ON r.id_inmueble = i.id_inmueble
                JOIN usuario u ON r.id_huesped = u.id_usuario
                WHERE i.id_propietario = ?
                ORDER BY r.fecha DESC
                LIMIT 1;
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

    // =========================================================================
    // MAPEADOR INTERNO (Construye e hidrata la reseña)
    // =========================================================================
    private static Reseña mapReseña(ResultSet rs, Inmueble inmueble, Usuario usuario) throws SQLException {
        Reseña r = new Reseña();

        r.setId(rs.getInt("id_reseña"));
        r.setPuntuacion(rs.getInt("puntuacion"));
        r.setComentario(rs.getString("comentario"));
        r.setFecha(rs.getDate("fecha").toLocalDate());

        if (inmueble == null) {
            inmueble = new Inmueble();
            inmueble.setIdInmueble(rs.getInt("id_inmueble"));
            try {
                inmueble.setTitulo(rs.getString("titulo"));
                inmueble.setCiudad(rs.getString("ciudad"));
                inmueble.setDireccion(rs.getString("direccion"));
            } catch (SQLException e) { /* Atrapado si no se incluye el JOIN de viviendas */ }
        }
        r.setInmueble(inmueble);

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setIdUsuario(rs.getInt("id_huesped"));
            try {
                usuario.setNombre(rs.getString("nombre"));
            } catch (SQLException e) { /* Atrapado si no se incluye el JOIN de usuarios */ }
        }
        r.setHuesped(usuario);

        return r;
    }
}