package org.example.renthub.DAO;

import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.EstadoReserva;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO extends Reserva {

    // ========================= SQL =========================

    private static final String INSERT =
            "INSERT INTO reserva (fecha_inicio, fecha_fin, total, estado, inmueble_id, huesped_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE reserva SET fecha_inicio=?, fecha_fin=?, total=?, estado=?, inmueble_id=?, huesped_id=? " +
                    "WHERE id=?";

    private static final String DELETE =
            "DELETE FROM reserva WHERE id = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM reserva WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM reserva";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM reserva WHERE inmueble_id = ?";

    private static final String SELECT_BY_HUESPED =
            "SELECT * FROM reserva WHERE huesped_id = ?";

    private static final String SELECT_BY_USUARIO =
            "SELECT r.* FROM reserva r " +
            "JOIN inmueble i ON r.inmueble_id = i.id " +
            "WHERE i.propietario_id = ?";


    // ========================= CONSTRUCTORES =========================

    public ReservaDAO() {
        super();
    }

    public ReservaDAO(int id) {
        super();
        loadById(id);
    }

    public ReservaDAO(Reserva r) {
        super(
                r.getId(),
                r.getFechaEntrada(),
                r.getFechaSalida(),
                r.getTotal(),
                r.getEstado(),
                r.getInmueble(),
                r.getHuesped(),
                r.getPago()
        );
    }

    public ReservaDAO(LocalDate inicio, LocalDate fin, double total,
                      EstadoReserva estado, Inmueble inmueble, Usuario huesped) {

        super(0, inicio, fin, total, estado, inmueble, huesped, null);
    }


    // ========================= SAVE =========================

    public boolean save() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(getFechaEntrada()));
            ps.setDate(2, Date.valueOf(getFechaSalida()));
            ps.setDouble(3, getTotal());
            ps.setString(4, getEstado().name());
            ps.setInt(5, getInmueble().getId());
            ps.setInt(6, getHuesped().getId());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) setId(rs.getInt(1));
            }

            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ========================= UPDATE =========================

    public boolean update() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setDate(1, Date.valueOf(getFechaEntrada()));
            ps.setDate(2, Date.valueOf(getFechaSalida()));
            ps.setDouble(3, getTotal());
            ps.setString(4, getEstado().name());
            ps.setInt(5, getInmueble().getId());
            ps.setInt(6, getHuesped().getId());
            ps.setInt(7, getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ========================= DELETE =========================

    public boolean delete() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ========================= LOAD BY ID =========================

    public void loadById(int id) {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                setId(rs.getInt("id"));
                setFechaEntrada(rs.getDate("fecha_inicio").toLocalDate());
                setFechaSalida(rs.getDate("fecha_fin").toLocalDate());
                setTotal(rs.getDouble("total"));
                setEstado(EstadoReserva.valueOf(rs.getString("estado")));

                setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                setHuesped(new UsuarioDAO(rs.getInt("huesped_id")));

                // cargar pago (si existe)
                setPago(PagoDAO.getByReservaId(getId()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ========================= MÉTODOS ESTÁTICOS =========================

    public static List<Reserva> getAll() {
        List<Reserva> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Reserva r = new Reserva();

                r.setId(rs.getInt("id"));
                r.setFechaEntrada(rs.getDate("fecha_inicio").toLocalDate());
                r.setFechaSalida(rs.getDate("fecha_fin").toLocalDate());
                r.setTotal(rs.getDouble("total"));
                r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));

                r.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                r.setHuesped(new UsuarioDAO(rs.getInt("huesped_id")));
                r.setPago(PagoDAO.getByReservaId(r.getId()));

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<Reserva> getByInmueble(int inmuebleId) {
        List<Reserva> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {

            ps.setInt(1, inmuebleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Reserva r = new Reserva();

                r.setId(rs.getInt("id"));
                r.setFechaEntrada(rs.getDate("fecha_inicio").toLocalDate());
                r.setFechaSalida(rs.getDate("fecha_fin").toLocalDate());
                r.setTotal(rs.getDouble("total"));
                r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));

                r.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                r.setHuesped(new UsuarioDAO(rs.getInt("huesped_id")));
                r.setPago(PagoDAO.getByReservaId(r.getId()));

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<Reserva> getByHuesped(int huespedId) {
        List<Reserva> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_HUESPED)) {

            ps.setInt(1, huespedId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Reserva r = new Reserva();

                r.setId(rs.getInt("id"));
                r.setFechaEntrada(rs.getDate("fecha_inicio").toLocalDate());
                r.setFechaSalida(rs.getDate("fecha_fin").toLocalDate());
                r.setTotal(rs.getDouble("total"));
                r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));

                r.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                r.setHuesped(new UsuarioDAO(rs.getInt("huesped_id")));
                r.setPago(PagoDAO.getByReservaId(r.getId()));

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Reserva> getByUsuario(int id) {
        List<Reserva> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_USUARIO)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Reserva r = new Reserva();

                r.setId(rs.getInt("id"));
                r.setFechaEntrada(rs.getDate("fecha_inicio").toLocalDate());
                r.setFechaSalida(rs.getDate("fecha_fin").toLocalDate());
                r.setTotal(rs.getDouble("total"));
                r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));

                r.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                r.setHuesped(new UsuarioDAO(rs.getInt("huesped_id")));
                r.setPago(PagoDAO.getByReservaId(r.getId()));

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}


