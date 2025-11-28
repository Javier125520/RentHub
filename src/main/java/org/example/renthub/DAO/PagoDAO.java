package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO extends Pago {

    // ========================= SQL =========================

    private static final String INSERT =
            "INSERT INTO pago (metodo, fecha_pago, monto, estado, reserva_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE pago SET metodo=?, fecha_pago=?, monto=?, estado=?, reserva_id=? " +
                    "WHERE id=?";

    private static final String DELETE =
            "DELETE FROM pago WHERE id = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM pago WHERE id = ?";

    private static final String SELECT_BY_RESERVA =
            "SELECT * FROM pago WHERE reserva_id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM pago";


    // ========================= CONSTRUCTORES =========================

    public PagoDAO() {
        super();
    }

    public PagoDAO(Pago p) {
        super(
                p.getId(),
                p.getMetodo(),
                p.getFechaPago(),
                p.getMonto(),
                p.getEstado()
        );
    }

    public PagoDAO(int id) {
        super();
        loadById(id);
    }

    public PagoDAO(MetodoPago metodo, LocalDate fecha, double monto,
                   EstadoPago estado) {

        super(0, metodo, fecha, monto, estado);
    }


    // ========================= SAVE =========================

    public boolean save() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, getMetodo().name());
            ps.setDate(2, Date.valueOf(getFechaPago()));
            ps.setDouble(3, getMonto());
            ps.setString(4, getEstado().name());

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

            ps.setString(1, getMetodo().name());
            ps.setDate(2, Date.valueOf(getFechaPago()));
            ps.setDouble(3, getMonto());
            ps.setString(4, getEstado().name());
            ps.setInt(5, getId());

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
                setMetodo(MetodoPago.valueOf(rs.getString("metodo")));
                setFechaPago(rs.getDate("fecha_pago").toLocalDate());
                setMonto(rs.getDouble("monto"));
                setEstado(EstadoPago.valueOf(rs.getString("estado")));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ========================= MÉTODOS ESTÁTICOS =========================

    public static Pago getByReservaId(int reservaId) {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_RESERVA)) {

            ps.setInt(1, reservaId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Pago p = new Pago();

                p.setId(rs.getInt("id"));
                p.setMetodo(MetodoPago.valueOf(rs.getString("metodo")));
                p.setFechaPago(rs.getDate("fecha_pago").toLocalDate());
                p.setMonto(rs.getDouble("monto"));
                p.setEstado(EstadoPago.valueOf(rs.getString("estado")));
                return p;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // puede no existir pago aún
    }


    public static List<Pago> getAll() {
        List<Pago> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Pago p = new Pago();

                p.setId(rs.getInt("id"));
                p.setMetodo(MetodoPago.valueOf(rs.getString("metodo")));
                p.setFechaPago(rs.getDate("fecha_pago").toLocalDate());
                p.setMonto(rs.getDouble("monto"));
                p.setEstado(EstadoPago.valueOf(rs.getString("estado")));

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
