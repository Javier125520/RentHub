package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.enums.TipoInmueble;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la gestión de Viviendas/Inmuebles.
 * Centraliza la lógica de negocio, filtros avanzados y búsquedas transaccionales.
 */
public class InmuebleDAO extends Inmueble {

    // =========================================================================
    // SENTENCIAS SQL BASE
    // =========================================================================
    private static final String INSERT =
            "INSERT INTO inmueble (tipo_inmueble, titulo, descripcion, direccion, ciudad, capacidad, numero_habitaciones, precio_noche, disponible, id_propietario) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE inmueble SET tipo_inmueble=?, titulo=?, descripcion=?, direccion=?, ciudad=?, capacidad=?, numero_habitaciones=?, precio_noche=?, disponible=?, id_propietario=? " +
                    "WHERE id_inmueble=?";

    private static final String DELETE =
            "DELETE FROM inmueble WHERE id_inmueble=?";

    private static final String SELECT_INMUEBLES_BY_PROPIETARIO =
            "SELECT * FROM inmueble WHERE id_propietario=?";

    private static final String SELECT_INMUEBLES_DISPONIBLES =
            "SELECT * FROM inmueble WHERE disponible=TRUE";

    private static final String COUNT_BY_PROPIETARIO =
            "SELECT COUNT(*) FROM inmueble WHERE id_propietario = ?";

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================
    public InmuebleDAO() {
        super();
    }

    /** Constructor por copia: Extrae y arrastra los estados de memoria de un Inmueble ordinario */
    public InmuebleDAO(Inmueble i) {
        super();
        this.setIdInmueble(i.getIdInmueble());
        this.setTipoInmueble(i.getTipoInmueble());
        this.setTitulo(i.getTitulo());
        this.setDescripcion(i.getDescripcion());
        this.setDireccion(i.getDireccion());
        this.setCiudad(i.getCiudad());
        this.setCapacidad(i.getCapacidad());
        this.setNumeroHabitaciones(i.getNumeroHabitaciones());
        this.setPrecioNoche(i.getPrecioNoche());
        this.setDisponible(i.isDisponible());
        this.setPropietario(i.getPropietario());
        this.setImagenes(i.getImagenes());
        this.setServicios(i.getServicios());
    }

    // =========================================================================
    // MÉTODOS CRUD DE INSTANCIA (Active Record)
    // =========================================================================

    /** Inserta el inmueble actual asignándolo al id de su propietario logueado */
    public boolean insert() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, this.getTipoInmueble().name()); // Almacenamos el String representativo del Enum
            ps.setString(2, this.getTitulo());
            ps.setString(3, this.getDescripcion());
            ps.setString(4, this.getDireccion());
            ps.setString(5, this.getCiudad());
            ps.setInt(6, this.getCapacidad());
            ps.setInt(7, this.getNumeroHabitaciones());
            ps.setDouble(8, this.getPrecioNoche());
            ps.setBoolean(9, this.isDisponible());
            ps.setInt(10, this.getPropietario().getIdUsuario());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.setIdInmueble(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /** Actualiza la totalidad de los datos del inmueble en base a su ID único */
    public boolean update() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, this.getTipoInmueble().name());
            ps.setString(2, this.getTitulo());
            ps.setString(3, this.getDescripcion());
            ps.setString(4, this.getDireccion());
            ps.setString(5, this.getCiudad());
            ps.setInt(6, this.getCapacidad());
            ps.setInt(7, this.getNumeroHabitaciones());
            ps.setDouble(8, this.getPrecioNoche());
            ps.setBoolean(9, this.isDisponible());
            ps.setInt(10, this.getPropietario().getIdUsuario());
            ps.setInt(11, this.getIdInmueble());

            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina el inmueble que invoca el método */
    public boolean remove() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, this.getIdInmueble());
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA ESTÁTICOS
    // =========================================================================

    /** Eliminar vivienda de forma estática mediante su identificador primario */
    public static boolean delete(int idInmueble) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idInmueble);
            return ps.executeUpdate() > 0;
        }
    }

    /** Obtiene el catálogo de inmuebles publicados por un propietario específico */
    public static List<Inmueble> findByPropietario(int idPropietario) throws SQLException {
        List<Inmueble> inmuebles = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_INMUEBLES_BY_PROPIETARIO)) {
            ps.setInt(1, idPropietario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inmuebles.add(mapInmueble(rs));
                }
            }
        }
        return inmuebles;
    }

    /** Obtiene todos los inmuebles marcados con disponibilidad activa en el sistema */
    public static List<Inmueble> findDisponibles() throws SQLException {
        List<Inmueble> inmuebles = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_INMUEBLES_DISPONIBLES)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inmuebles.add(mapInmueble(rs));
                }
            }
        }
        return inmuebles;
    }

    /** Cuenta las viviendas totales de un propietario (Utilizado para el contador del Perfil) */
    public static int countByPropietario(int idPropietario) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(COUNT_BY_PROPIETARIO)) {
            ps.setInt(1, idPropietario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * MOTOR DE BÚSQUEDA AVANZADA CON PARÁMETROS DINÁMICOS.
     * Concatena condiciones e intercepta solapamiento de fechas con reservas existentes y filtros de servicios combinados.
     */
    public static List<Inmueble> buscar(
            String titulo, String ciudad, Double precioMin, Double precioMax,
            Integer capacidad, Integer numeroHabitaciones, LocalDate fechaEntrada,
            LocalDate fechaSalida, List<Integer> serviciosIncluidos
    ) throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT DISTINCT i.* FROM inmueble i ");
        List<Object> params = new ArrayList<>();

        // Si se buscan servicios específicos, realizamos el cruce con la tabla relacional intermedia
        if (serviciosIncluidos != null && !serviciosIncluidos.isEmpty()) {
            sql.append("JOIN inmueble_servicio isv ON i.id_inmueble = isv.id_inmueble ");
        }

        sql.append("WHERE i.disponible = TRUE ");

        // Inyección dinámica de filtros según los campos completados por el huésped
        if (titulo != null && !titulo.isBlank()) {
            sql.append("AND i.titulo LIKE ? ");
            params.add("%" + titulo + "%");
        }
        if (ciudad != null && !ciudad.isBlank()) {
            sql.append("AND i.ciudad LIKE ? ");
            params.add("%" + ciudad + "%");
        }
        if (precioMin != null) {
            sql.append("AND i.precio_noche >= ? ");
            params.add(precioMin);
        }
        if (precioMax != null) {
            sql.append("AND i.precio_noche <= ? ");
            params.add(precioMax);
        }
        if (capacidad != null) {
            sql.append("AND i.capacidad >= ? ");
            params.add(capacidad);
        }
        if (numeroHabitaciones != null) {
            sql.append("AND i.numero_habitaciones >= ? ");
            params.add(numeroHabitaciones);
        }

        // Exclusión transaccional de inmuebles ocupados en el rango de fechas seleccionado
        if (fechaEntrada != null && fechaSalida != null) {
            sql.append("""
                AND i.id_inmueble NOT IN (
                    SELECT r.id_inmueble
                    FROM reserva r
                    WHERE r.fecha_inicio < ?
                      AND r.fecha_fin > ?
                )
                """);
            params.add(Date.valueOf(fechaSalida));
            params.add(Date.valueOf(fechaEntrada));
        }

        // Construcción dinámica del IN(...) para el listado correlativo de servicios extras
        if (serviciosIncluidos != null && !serviciosIncluidos.isEmpty()) {
            sql.append("AND isv.id_servicio IN (");
            for (int i = 0; i < serviciosIncluidos.size(); i++) {
                sql.append("?");
                if (i < serviciosIncluidos.size() - 1) sql.append(",");
            }
            sql.append(") ");
            params.addAll(serviciosIncluidos);
        }

        List<Inmueble> resultado = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapInmueble(rs));
                }
            }
        }
        return resultado;
    }

    // =========================================================================
    // MAPEADOR INTERNO (Construye e hidrata el modelo Inmueble)
    // =========================================================================
    private static Inmueble mapInmueble(ResultSet rs) throws SQLException {
        Inmueble i = new Inmueble();

        i.setIdInmueble(rs.getInt("id_inmueble"));
        i.setTipoInmueble(TipoInmueble.valueOf(rs.getString("tipo_inmueble")));
        i.setTitulo(rs.getString("titulo"));
        i.setDescripcion(rs.getString("descripcion"));
        i.setDireccion(rs.getString("direccion"));
        i.setCiudad(rs.getString("ciudad"));
        i.setCapacidad(rs.getInt("capacidad"));
        i.setNumeroHabitaciones(rs.getInt("numero_habitaciones"));
        i.setPrecioNoche(rs.getDouble("precio_noche"));
        i.setDisponible(rs.getBoolean("disponible"));

        // Recuperamos el propietario vinculándolo mediante su correspondiente DAO activo
        int idPropietario = rs.getInt("id_propietario");
        i.setPropietario(UsuarioDAO.getById(idPropietario));

        // Carga ansiosa de colecciones dependientes (Imágenes y servicios configurados)
        i.setImagenes(ImagenInmuebleDAO.findByInmueble(i));
        i.setServicios(InmuebleServicioDAO.findByInmueble(i));

        return i;
    }
}
