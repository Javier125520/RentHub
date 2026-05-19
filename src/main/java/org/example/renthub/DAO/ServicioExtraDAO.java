package org.example.renthub.DAO;

import org.example.renthub.model.ServicioExtra;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para el catálogo de Servicios Extras del sistema.
 */
public class ServicioExtraDAO extends ServicioExtra {

    // =========================================================================
    // SENTENCIAS SQL
    // =========================================================================
    private static final String INSERT =
            "INSERT INTO servicio_extra (nombre, descripcion) VALUES (?, ?);";

    private static final String UPDATE =
            "UPDATE servicio_extra SET nombre = ?, descripcion = ? WHERE id_servicio = ?;";

    private static final String DELETE =
            "DELETE FROM servicio_extra WHERE id_servicio = ?;";

    private static final String SELECT_ALL =
            "SELECT * FROM servicio_extra;";

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================
    public ServicioExtraDAO() {
        super();
    }

    public ServicioExtraDAO(int idServicio, String nombre, String descripcion) {
        super(idServicio, nombre, descripcion);
    }

    /** Constructor por copia para transformar el catálogo plano en activos operativos */
    public ServicioExtraDAO(ServicioExtra s) {
        super();
        this.setIdServicio(s.getIdServicio());
        this.setNombre(s.getNombre());
        this.setDescripcion(s.getDescripcion());
    }

    // =========================================================================
    // MÉTODOS CRUD DE INSTANCIA (Active Record)
    // =========================================================================

    /** Registra un nuevo tipo de servicio global en el sistema */
    public boolean insert() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.getNombre());
            ps.setString(2, this.getDescripcion());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.setIdServicio(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /** Modifica las propiedades globales de un servicio extra */
    public boolean update() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, this.getNombre());
            ps.setString(2, this.getDescripcion());
            ps.setInt(3, this.getIdServicio());
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina permanentemente el servicio extra del catálogo global */
    public boolean remove() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, this.getIdServicio());
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA ESTÁTICOS
    // =========================================================================

    /** Borrado estático mediante paso directo de ID */
    public static boolean delete(int idServicio) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idServicio);
            return ps.executeUpdate() > 0;
        }
    }

    /** Recupera la lista completa de opciones del catálogo global de servicios extras */
    public static List<ServicioExtra> findAll() throws SQLException {
        List<ServicioExtra> servicios = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                servicios.add(mapServicio(rs));
            }
        }
        return servicios;
    }

    // =========================================================================
    // MAPEADOR INTERNO
    // =========================================================================
    private static ServicioExtra mapServicio(ResultSet rs) throws SQLException {
        return new ServicioExtra(
                rs.getInt("id_servicio"),
                rs.getString("nombre"),
                rs.getString("descripcion")
        );
    }
}


