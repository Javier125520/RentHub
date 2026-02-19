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

public class ReservaDAO {

    private final Connection conn;

    // =========================
    // SQL
    // =========================

    private static final String INSERT =
            "INSERT INTO reserva (fecha_inicio, fecha_fin, precio, estado, id_huesped, id_inmueble) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE reserva SET fecha_inicio = ?, fecha_fin = ?, precio = ?" +
            "WHERE id_reserva = ?";

    private static final String DELETE =
            "DELETE FROM reserva WHERE id_reserva = ?";

    private static final String CANCELAR =
            "UPDATE reserva SET estado = 'CANCELADA' WHERE id_reserva = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM reserva WHERE id_reserva = ?";

    private static final String SELECT_BY_HUESPED =
            "SELECT r.*, i.id_inmueble, i.titulo, i.ciudad, i.direccion, i.precio_noche " +
                    "FROM reserva r " +
                    "JOIN inmueble i ON r.id_inmueble = i.id_inmueble " +
                    "WHERE r.id_huesped = ?";

    private static final String SELECT_BY_PROPIETARIO =
            "SELECT r.*, " +
                    "i.id_inmueble, i.titulo, i.ciudad, i.direccion, i.precio_noche, " +
                    "u.nombre, u.correo " +
                    "FROM reserva r " +
                    "JOIN inmueble i ON r.id_inmueble = i.id_inmueble " +
                    "JOIN usuario u ON r.id_huesped = u.id_usuario " +
                    "WHERE i.id_propietario = ?";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM reserva WHERE id_inmueble = ?";

    private static final String UPDATE_ESTADO =
            "UPDATE reserva SET estado = ? WHERE id_reserva = ?";

    private static final String CHECK_DISPONIBILIDAD =
            """
            SELECT COUNT(*) 
            FROM reserva
            WHERE id_inmueble = ?
              AND estado <> 'CANCELADA'
              AND fecha_inicio < ?
              AND fecha_fin > ?
            """;

    private static final String CONFIRMAR =
            "UPDATE reserva SET estado = ? WHERE id_reserva = ?";

    // =========================
    // CONSTRUCTORES
    // =========================

    public ReservaDAO() {
        this.conn = MySQLConnection.getConnection();
    }

    public ReservaDAO(Connection conn) {
        this.conn = conn;
    }


    public boolean insert(Reserva r) throws SQLException {

        if (!estaDisponible(
                r.getInmueble().getIdInmueble(),
                r.getFechaEntrada(),
                r.getFechaSalida())) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(r.getFechaEntrada()));
            ps.setDate(2, Date.valueOf(r.getFechaSalida()));
            ps.setDouble(3, r.getPrecioTotal());
            ps.setString(4, r.getEstado().name());
            ps.setInt(5, r.getHuesped().getIdUsuario());
            ps.setInt(6, r.getInmueble().getIdInmueble());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    r.setIdReserva(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(Reserva r) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setDate(1, Date.valueOf(r.getFechaEntrada()));
            ps.setDate(2, Date.valueOf(r.getFechaSalida()));
            ps.setDouble(3, r.getPrecioTotal());
            ps.setInt(4, r.getIdReserva());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int idReserva) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        }
    }

    public void cancelarReserva(int idReserva) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(CANCELAR)) {
            ps.setInt(1, idReserva);
            ps.executeUpdate();
        }
    }


    public List<Reserva> findByHuesped(int idUsuario) throws SQLException {

        List<Reserva> reservas = new ArrayList<>();

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

    public List<Reserva> findByPropietario(int idUsuario) {

        List<Reserva> reservas = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_PROPIETARIO)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapReserva(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reservas;
    }

    public boolean estaDisponible(int idInmueble, LocalDate entrada, LocalDate salida)
            throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(CHECK_DISPONIBILIDAD)) {

            ps.setInt(1, idInmueble);
            ps.setDate(2, Date.valueOf(salida));
            ps.setDate(3, Date.valueOf(entrada));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return false;
    }

    public void confirmarReserva(int idReserva) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(CONFIRMAR)) {

            ps.setString(1, "CONFIRMADA");
            ps.setInt(2, idReserva);

            ps.executeUpdate();
        }
    }

    public int countByHuesped(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE id_huesped = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countPagadasByHuesped(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE id_huesped = ? AND estado = 'CONFIRMADA'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countPendientesByHuesped(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE id_huesped = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Reserva findUltimaByHuesped(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM reserva WHERE id_huesped = ? ORDER BY fecha_registro DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapReserva(rs);
            }
        }
        return null;
    }

    public int countByPropietario(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva r JOIN inmueble i ON r.id_inmueble = i.id_inmueble WHERE i.id_propietario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countPendientesByPropietario(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva r JOIN inmueble i ON r.id_inmueble = i.id_inmueble WHERE i.id_propietario = ? AND r.estado = 'PENDIENTE'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Reserva findUltimaByPropietario(int idUsuario) throws SQLException {
        String sql = """
                SELECT r.*, i.id_inmueble, i.titulo, i.ciudad, i.direccion, i.precio_noche, u.nombre, u.correo
                FROM reserva r
                JOIN inmueble i ON r.id_inmueble = i.id_inmueble
                JOIN usuario u ON r.id_huesped = u.id_usuario
                WHERE i.id_propietario = ?
                ORDER BY r.fecha_registro DESC
                LIMIT 1
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapReserva(rs);
            }
        }
        return null;
    }

    private Reserva mapReserva(ResultSet rs) throws SQLException {

        Reserva r = new Reserva();

        r.setIdReserva(rs.getInt("id_reserva"));
        r.setFechaEntrada(rs.getDate("fecha_inicio").toLocalDate());
        r.setFechaSalida(rs.getDate("fecha_fin").toLocalDate());
        r.setPrecioTotal(rs.getDouble("precio"));
        r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
        r.setFechaRegistro(Timestamp.valueOf(rs.getTimestamp("fecha_registro").toLocalDateTime()));

        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_huesped"));
        try {
            u.setNombre(rs.getString("nombre"));
            u.setCorreo(rs.getString("correo"));
        } catch (SQLException e) {
            // Si no se encuentran las columnas, se ignora y se deja el usuario con solo el ID
        }
        r.setHuesped(u);

        Inmueble i = new Inmueble();
        i.setIdInmueble(rs.getInt("id_inmueble"));
        i.setTitulo(rs.getString("titulo"));
        i.setCiudad(rs.getString("ciudad"));
        i.setDireccion(rs.getString("direccion"));
        i.setPrecioNoche(rs.getDouble("precio_noche"));
        r.setInmueble(i);

        return r;
    }
}





