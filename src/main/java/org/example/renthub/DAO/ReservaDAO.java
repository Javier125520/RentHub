package org.example.renthub.DAO;

import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.enums.EstadoReserva;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Usuario;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la contratación y reserva de alquileres.
 * Valida la disponibilidad de fechas y calcula los resúmenes estadísticos e ingresos de los perfiles.
 */
public class ReservaDAO extends Reserva {

    // =========================================================================
    // SENTENCIAS SQL
    // =========================================================================
    private static final String INSERT =
            "INSERT INTO reserva (fecha_inicio, fecha_fin, precio, estado, id_huesped, id_inmueble) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE reserva SET fecha_inicio = ?, fecha_fin = ?, precio = ? WHERE id_reserva = ?";

    private static final String DELETE =
            "DELETE FROM reserva WHERE id_reserva = ?";

    private static final String CANCELAR =
            "UPDATE reserva SET estado = 'CANCELADA' WHERE id_reserva = ?";

    private static final String SELECT_BY_HUESPED =
            "SELECT r.*, i.id_inmueble, i.titulo, i.ciudad, i.direccion, i.precio_noche " +
                    "FROM reserva r " +
                    "JOIN inmueble i ON r.id_inmueble = i.id_inmueble " +
                    "WHERE r.id_huesped = ?";

    private static final String SELECT_BY_PROPIETARIO =
            "SELECT r.*, i.id_inmueble, i.titulo, i.ciudad, i.direccion, i.precio_noche, u.nombre, u.correo " +
                    "FROM reserva r " +
                    "JOIN inmueble i ON r.id_inmueble = i.id_inmueble " +
                    "JOIN usuario u ON r.id_huesped = u.id_usuario " +
                    "WHERE i.id_propietario = ?";

    private static final String CHECK_DISPONIBILIDAD =
            "SELECT COUNT(*) FROM reserva WHERE id_inmueble = ? AND estado <> 'CANCELADA' AND fecha_inicio < ? AND fecha_fin > ?";

    private static final String CONFIRMAR =
            "UPDATE reserva SET estado = ? WHERE id_reserva = ?";

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================
    public ReservaDAO() {
        super();
    }

    /** Constructor por copia para envolturas transaccionales de reservas en la UI */
    public ReservaDAO(Reserva r) {
        super();
        this.setIdReserva(r.getIdReserva());
        this.setFechaEntrada(r.getFechaEntrada());
        this.setFechaSalida(r.getFechaSalida());
        this.setPrecioTotal(r.getPrecioTotal());
        this.setEstado(r.getEstado());
        this.setFechaRegistro(r.getFechaRegistro());
        this.setHuesped(r.getHuesped());
        this.setInmueble(r.getInmueble());
    }

    // =========================================================================
    // MÉTODOS CRUD DE INSTANCIA (Active Record)
    // =========================================================================

    /** Inserta la reserva comprobando previamente si las fechas han quedado libres */
    public boolean insert() throws SQLException {
        if (!estaDisponible(this.getInmueble().getIdInmueble(), this.getFechaEntrada(), this.getFechaSalida())) {
            return false; // Bloquea la inserción automática si se solapan las fechas
        }

        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(this.getFechaEntrada()));
            ps.setDate(2, Date.valueOf(this.getFechaSalida()));
            ps.setDouble(3, this.getPrecioTotal());
            ps.setString(4, this.getEstado().name());
            ps.setInt(5, this.getHuesped().getIdUsuario());
            ps.setInt(6, this.getInmueble().getIdInmueble());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.setIdReserva(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /** Modifica el intervalo temporal o precio de una contratación */
    public boolean update() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setDate(1, Date.valueOf(this.getFechaEntrada()));
            ps.setDate(2, Date.valueOf(this.getFechaSalida()));
            ps.setDouble(3, this.getPrecioTotal());
            ps.setInt(4, this.getIdReserva());

            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina la reserva de manera absoluta física */
    public boolean remove() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, this.getIdReserva());
            return ps.executeUpdate() > 0;
        }
    }

    /** Realiza una baja lógica cambiando el estado del Enum a CANCELADA en lugar de eliminar la fila */
    public void cancelarReserva() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(CANCELAR)) {
            ps.setInt(1, this.getIdReserva());
            if (ps.executeUpdate() > 0) {
                this.setEstado(EstadoReserva.CANCELADA);
            }
        }
    }

    /** Modifica el estado de cobro de la reserva a CONFIRMADA tras el pago de la pasarela */
    public void confirmarReserva() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(CONFIRMAR)) {
            ps.setString(1, "CONFIRMADA");
            ps.setInt(2, this.getIdReserva());
            if (ps.executeUpdate() > 0) {
                this.setEstado(EstadoReserva.CONFIRMADA);
            }
        }
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA ESTÁTICOS Y ESTADÍSTICAS
    // =========================================================================

    /** Obtiene el historial de reservas solicitadas por un Huésped */
    public static List<ReservaDAO> findByHuesped(int idUsuario) throws SQLException {
        List<ReservaDAO> reservas = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_HUESPED)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapReserva(rs));
                }
            }
        }
        return reservas;
    }

    /** Obtiene la bandeja de entrada de solicitudes de reserva recibidas por un Propietario */
    public static List<ReservaDAO> findByPropietario(int idUsuario) throws SQLException {
        List<ReservaDAO> reservas = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_PROPIETARIO)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapReserva(rs));
                }
            }
        }
        return reservas;
    }

    /** Comprueba mediante intersección booleana si existe alguna reserva solapada en esas fechas */
    public static boolean estaDisponible(int idInmueble, LocalDate entrada, LocalDate salida) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(CHECK_DISPONIBILIDAD)) {
            ps.setInt(1, idInmueble);
            ps.setDate(2, Date.valueOf(salida));
            ps.setDate(3, Date.valueOf(entrada));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }

    /** Cuenta el histórico de reservas hechas por un huésped */
    public static int countByHuesped(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE id_huesped = ?";
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Cuenta las reservas abonadas y confirmadas del huésped */
    public static int countPagadasByHuesped(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE id_huesped = ? AND estado = 'CONFIRMADA'";
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Cuenta los cobros pendientes del huésped */
    public static int countPendientesByHuesped(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE id_huesped = ? AND estado = 'PENDIENTE'";
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Recupera el último movimiento/reserva para el feed de Actividad Reciente del Huésped */
    public static Reserva findUltimaByHuesped(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM reserva WHERE id_huesped = ? ORDER BY fecha_registro DESC LIMIT 1";
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapReserva(rs);
            }
        }
        return null;
    }

    /** Cuenta las solicitudes totales gestionadas sobre las viviendas de un propietario */
    public static int countByPropietario(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva r JOIN inmueble i ON r.id_inmueble = i.id_inmueble WHERE i.id_propietario = ?";
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Cuenta las reservas pendientes de aprobación/pago del propietario */
    public static int countPendientesByPropietario(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva r JOIN inmueble i ON r.id_inmueble = i.id_inmueble WHERE i.id_propietario = ? AND r.estado = 'PENDIENTE'";
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Obtiene el último movimiento para el feed de actividad del propietario */
    public static Reserva findUltimaByPropietario(int idUsuario) throws SQLException {
        String sql = """
                SELECT r.*, i.id_inmueble, i.titulo, i.ciudad, i.direccion, i.precio_noche, u.nombre, u.correo
                FROM reserva r
                JOIN inmueble i ON r.id_inmueble = i.id_inmueble
                JOIN usuario u ON r.id_huesped = u.id_usuario
                WHERE i.id_propietario = ?
                ORDER BY r.fecha_registro DESC
                LIMIT 1
                """;
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapReserva(rs);
            }
        }
        return null;
    }

    // =========================================================================
    // MAPEADOR INTERNO (Construye ReservaDAO acoplando objetos mediante JOINs)
    // =========================================================================
    private static ReservaDAO mapReserva(ResultSet rs) throws SQLException {
        ReservaDAO r = new ReservaDAO();

        r.setIdReserva(rs.getInt("id_reserva"));
        r.setFechaEntrada(rs.getDate("fecha_inicio").toLocalDate());
        r.setFechaSalida(rs.getDate("fecha_fin").toLocalDate());
        r.setPrecioTotal(rs.getDouble("precio"));
        r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
        r.setFechaRegistro(Timestamp.valueOf(rs.getTimestamp("fecha_registro").toLocalDateTime()));

        // Saneamiento e hidratación segura del Huesped
        int idHuesped = rs.getInt("id_huesped");
        Usuario huesped = UsuarioDAO.getById(idHuesped);
        if (huesped == null) {
            huesped = new Usuario();
            huesped.setIdUsuario(idHuesped);
        }
        try {
            huesped.setNombre(rs.getString("nombre"));
            huesped.setCorreo(rs.getString("correo"));
        } catch (SQLException e) { /* Ignorado si la consulta base no incluye el JOIN extendido */ }
        r.setHuesped(huesped);

        // Saneamiento e hidratación segura del Inmueble
        int idInmueble = rs.getInt("id_inmueble");
        Inmueble i = new Inmueble();
        i.setIdInmueble(idInmueble);
        try {
            i.setTitulo(rs.getString("titulo"));
            i.setCiudad(rs.getString("ciudad"));
            i.setDireccion(rs.getString("direccion"));
            i.setPrecioNoche(rs.getDouble("precio_noche"));
        } catch (SQLException e) { /* Ignorado si la información del inmueble no venía anexada */ }
        r.setInmueble(i);

        return r;
    }
}


