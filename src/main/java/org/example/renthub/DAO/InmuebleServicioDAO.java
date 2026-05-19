package org.example.renthub.DAO;


import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.*;
import org.example.renthub.model.enums.EstadoServicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la relación muchos a muchos entre Inmuebles y Servicios Extras.
 * Almacena información personalizada por propiedad, como precios específicos o estados de activación.
 */
public class InmuebleServicioDAO extends InmuebleServicio {

    // =========================================================================
    // SENTENCIAS SQL
    // =========================================================================
    private static final String INSERT =
            "INSERT INTO inmueble_servicio (id_inmueble, id_servicio, precio_adicional, estado_servicio) VALUES (?, ?, ?, ?);";

    private static final String DELETE =
            "DELETE FROM inmueble_servicio WHERE id_inmueble = ? AND id_servicio = ?;";

    private static final String SELECT_BY_INMUEBLE =
            """
            SELECT isv.*, se.nombre, se.descripcion
            FROM inmueble_servicio isv
            JOIN servicio_extra se 
                ON isv.id_servicio = se.id_servicio
            WHERE isv.id_inmueble = ?;
            """;

    private static final String DELETE_BY_INMUEBLE =
            "DELETE FROM inmueble_servicio WHERE id_inmueble = ?;";

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================
    public InmuebleServicioDAO() {
        super();
    }

    /** Constructor por copia para enlazar la relación bajo objetos activos */
    public InmuebleServicioDAO(InmuebleServicio is) {
        super();
        this.setInmueble(is.getInmueble());
        this.setServicio(is.getServicio());
        this.setPrecioAdicional(is.getPrecioAdicional());
        this.setEstado(is.getEstado());
    }

    // =========================================================================
    // MÉTODOS CRUD DE INSTANCIA (Active Record)
    // =========================================================================

    /** Asocia un servicio extra específico a un inmueble determinando su coste extra por noche */
    public boolean insert() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setInt(1, this.getInmueble().getIdInmueble());
            ps.setInt(2, this.getServicio().getIdServicio());
            ps.setDouble(3, this.getPrecioAdicional());
            ps.setString(4, this.getEstado().name());

            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina el vínculo de servicio particular sobre un alojamiento */
    public boolean remove() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, this.getInmueble().getIdInmueble());
            ps.setInt(2, this.getServicio().getIdServicio());

            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA ESTÁTICOS
    // =========================================================================

    /**
     * Recupera la totalidad de los servicios adicionales activos en una vivienda.
     * @param inmueble El inmueble a inspeccionar.
     * @return Listado hidratado con el objeto del servicio embebido de forma limpia.
     */
    public static List<InmuebleServicio> findByInmueble(Inmueble inmueble) throws SQLException {
        List<InmuebleServicio> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InmuebleServicioDAO is = new InmuebleServicioDAO();
                    is.setPrecioAdicional(rs.getDouble("precio_adicional"));
                    is.setEstado(EstadoServicio.valueOf(rs.getString("estado_servicio")));
                    is.setInmueble(inmueble);

                    // Mapeo manual del servicio extra obtenido mediante el JOIN SQL
                    ServicioExtra s = new ServicioExtra();
                    s.setIdServicio(rs.getInt("id_servicio"));
                    s.setNombre(rs.getString("nombre"));
                    s.setDescripcion(rs.getString("descripcion"));
                    is.setServicio(s);

                    lista.add(is);
                }
            }
        }
        return lista;
    }

    /** Elimina de golpe todos los servicios vinculados a una vivienda*/
    public static void deleteByInmueble(Inmueble inmueble) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());
            ps.executeUpdate();
        }
    }
}