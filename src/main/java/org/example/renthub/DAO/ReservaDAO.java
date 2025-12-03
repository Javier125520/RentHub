package org.example.renthub.DAO;

import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Reserva;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO extends Reserva {

    // ────────────────────────────────────────────────
    //  CONSULTAS SQL
    // ────────────────────────────────────────────────

    private static final String INSERT =
            "INSERT INTO reserva (id_usuario, id_inmueble, fecha_inicio, fecha_fin, precio_total, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?);";

    private static final String UPDATE =
            "UPDATE reserva SET id_usuario=?, id_inmueble=?, fecha_inicio=?, fecha_fin=?, precio_total=?, estado=? " +
                    "WHERE id_reserva = ?;";

    private static final String DELETE =
            "DELETE FROM reserva WHERE id_reserva = ?;";

    private static final String SELECT_BY_ID =
            "SELECT * FROM reserva WHERE id_reserva = ?;";

    private static final String SELECT_ALL =
            "SELECT * FROM reserva;";

    private static final String SELECT_BY_USUARIO =
            "SELECT * FROM reserva WHERE id_usuario = ?;";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM reserva WHERE id_inmueble = ?;";

    private static final String CHECK_SOLAPAMIENTO =
            "SELECT COUNT(*) AS total FROM reserva " +
                    "WHERE id_inmueble = ? AND (" +
                    "(fecha_inicio <= ? AND fecha_fin >= ?) " +      // Inicio dentro de otra reserva
                    "OR (fecha_inicio <= ? AND fecha_fin >= ?) " +   // Fin dentro de otra reserva
                    "OR (fecha_inicio >= ? AND fecha_fin <= ?)" +    // Fechas completamente dentro
                    ");";

    private static final String SELECT_ACTIVAS =
            "SELECT * FROM reserva WHERE fecha_fin >= CURDATE();";

    private static final String SELECT_PASADAS =
            "SELECT * FROM reserva WHERE fecha_fin < CURDATE();";

    private static final String SELECT_ENTRE_FECHAS =
            "SELECT * FROM reserva WHERE fecha_inicio >= ? AND fecha_fin <= ?;";


    // ────────────────────────────────────────────────
    //  CONSTRUCTORES
    // ────────────────────────────────────────────────

    public ReservaDAO() {
        super();
    }

    public ReservaDAO(int id) {
        super();
        getById(id);
    }

    public ReservaDAO(Reserva r) {
        super(r.getIdReserva(), r.getFechaEntrada(), r.getFechaSalida(), r.getTotal(),
                r.getEstado(), r.getInmueble(), r.getHuesped(), r.getPago());
    }


    // ────────────────────────────────────────────────
    //  MÉTODO SAVE (INSERT + UPDATE)
    // ────────────────────────────────────────────────

    public boolean save() throws SQLException {
        Connection con = MySQLConnection.getConnection();

        // UPDATE
        if (this.getId_reserva() != 0) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE)) {

                ps.setInt(1, getUsuario().getId_usuario());
                ps.setInt(2, getInmueble().getId_inmueble());
                ps.setDate(3, Date.valueOf(getFecha_inicio()));
                ps.setDate(4, Date.valueOf(getFecha_fin()));
                ps.setDouble(5, getPrecio_total());
                ps.setString(6, getEstado());
                ps.setInt(7, getId_reserva());

                return ps.executeUpdate() > 0;
            }
        }

        // INSERT
        try (PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, getUsuario().getId_usuario());
            ps.setInt(2, getInmueble().getId_inmueble());
            ps.setDate(3, Date.valueOf(getFecha_inicio()));
            ps.setDate(4, Date.valueOf(getFecha_fin()));
            ps.setDouble(5, getPrecio_total());
            ps.setString(6, getEstado());

            boolean inserted = ps.executeUpdate() > 0;

            if (inserted) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) this.setId_reserva(rs.getInt(1));
            }

            return inserted;
        }
    }


    // ────────────────────────────────────────────────
    //  DELETE
    // ────────────────────────────────────────────────

    public boolean remove() throws SQLException {
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(DELETE)) {
            ps.setInt(1, getId_reserva());
            return ps.executeUpdate() > 0;
        }
    }


    // ────────────────────────────────────────────────
    //  GET BY ID
    // ────────────────────────────────────────────────

    public void getById(int id) {
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) loadFromResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ────────────────────────────────────────────────
    //  CONSULTAS ESTÁTICAS
    // ────────────────────────────────────────────────

    public static List<Reserva> getAll() {
        List<Reserva> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_ALL)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReservaDAO r = new ReservaDAO();
                r.loadFromResultSet(rs);
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Reserva> getByUsuario(int idUsuario) {
        List<Reserva> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_USUARIO)) {
            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReservaDAO r = new ReservaDAO();
                r.loadFromResultSet(rs);
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Reserva> getByInmueble(int idInmueble) {
        List<Reserva> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_INMUEBLE)) {
            ps.setInt(1, idInmueble);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReservaDAO r = new ReservaDAO();
                r.loadFromResultSet(rs);
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Reserva> getReservasActivas() {
        List<Reserva> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_ACTIVAS)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReservaDAO r = new ReservaDAO();
                r.loadFromResultSet(rs);
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Reserva> getReservasPasadas() {
        List<Reserva> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_PASADAS)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReservaDAO r = new ReservaDAO();
                r.loadFromResultSet(rs);
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<Reserva> getReservasEntreFechas(LocalDate inicio, LocalDate fin) {
        List<Reserva> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_ENTRE_FECHAS)) {
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReservaDAO r = new ReservaDAO();
                r.loadFromResultSet(rs);
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    // ────────────────────────────────────────────────
    //  COMPROBAR SOLAPAMIENTO DE FECHAS
    // ────────────────────────────────────────────────

    public static boolean haySolapamiento(int idInmueble, LocalDate inicio, LocalDate fin) {
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(CHECK_SOLAPAMIENTO)) {

            ps.setInt(1, idInmueble);

            ps.setDate(2, Date.valueOf(inicio));
            ps.setDate(3, Date.valueOf(inicio));

            ps.setDate(4, Date.valueOf(fin));
            ps.setDate(5, Date.valueOf(fin));

            ps.setDate(6, Date.valueOf(inicio));
            ps.setDate(7, Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("total") > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    // ────────────────────────────────────────────────
    //  MAPEO RESULTSET → RESERVA
    // ────────────────────────────────────────────────

    private void loadFromResultSet(ResultSet rs) throws SQLException {

        this.setId_reserva(rs.getInt("id_reserva"));

        // Usuario
        int idUsuario = rs.getInt("id_usuario");
        this.setUsuario(new UsuarioDAO(idUsuario));

        // Inmueble
        int idInmueble = rs.getInt("id_inmueble");
        this.setInmueble(new InmuebleDAO(idInmueble));

        this.setFecha_inicio(rs.getDate("fecha_inicio").toLocalDate());
        this.setFecha_fin(rs.getDate("fecha_fin").toLocalDate());
        this.setPrecio_total(rs.getDouble("precio_total"));
        this.setEstado(rs.getString("estado"));
    }
}




