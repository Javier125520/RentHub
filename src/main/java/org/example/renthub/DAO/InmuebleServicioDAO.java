package org.example.renthub.DAO;


import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InmuebleServicioDAO extends InmuebleServicio {

    // ========================= SQL =========================

    private static final String INSERT =
            "INSERT INTO inmueble_servicio (inmueble_id, servicio_id, estado, precioAdicional, incluidoEnPrecio) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE inmueble_servicio SET inmueble_id=?, servicio_id=?, estado=?, precioAdicional=?, incluidoEnPrecio=? " +
                    "WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM inmueble_servicio WHERE id = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM inmueble_servicio WHERE id = ?";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM inmueble_servicio WHERE inmueble_id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM inmueble_servicio";


    // ========================= CONSTRUCTORES =========================

    public InmuebleServicioDAO() {
        super();
    }

    public InmuebleServicioDAO(int id) {
        super();
        loadById(id);
    }

    public InmuebleServicioDAO(InmuebleServicio is) {
        super(is.getId(), is.getInmueble(), is.getServicio(), is.getPrecioAdicional(), is.isIncluidoEnPrecio(), is.getEstado());
    }

    public InmuebleServicioDAO(Inmueble inmueble, ServicioExtra servicio,
                               EstadoServicio estado, double precioAdicional, boolean incluido) {

        super(0, inmueble, servicio, precioAdicional, incluido, estado);
    }


    // ========================= SAVE =========================

    public boolean save() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, getInmueble().getIdInmueble());
            ps.setInt(2, getServicio().getIdServicio());
            ps.setString(3, getEstado().name());
            ps.setDouble(4, getPrecioAdicional());
            ps.setBoolean(5, isIncluidoEnPrecio());

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

            ps.setInt(1, getInmueble().getIdInmueble());
            ps.setInt(2, getServicio().getIdServicio());
            ps.setString(3, getEstado().name());
            ps.setDouble(4, getPrecioAdicional());
            ps.setBoolean(5, isIncluidoEnPrecio());
            ps.setInt(6, getId());

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

                // cargar objetos relacionados
                setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                setServicio(new ServicioExtraDAO(rs.getInt("servicio_id")));

                setEstado(EstadoServicio.valueOf(rs.getString("estado")));
                setPrecioAdicional(rs.getDouble("precioAdicional"));
                setIncluidoEnPrecio(rs.getBoolean("incluidoEnPrecio"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ========================= MÉTODOS ESTÁTICOS =========================

    public static List<InmuebleServicio> getByInmueble(int inmuebleId) {
        List<InmuebleServicio> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {

            ps.setInt(1, inmuebleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                InmuebleServicio is = new InmuebleServicio();

                is.setId(rs.getInt("id"));
                is.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                is.setServicio(new ServicioExtraDAO(rs.getInt("servicio_id")));
                is.setEstado(EstadoServicio.valueOf(rs.getString("estado")));
                is.setPrecioAdicional(rs.getDouble("precioAdicional"));
                is.setIncluidoEnPrecio(rs.getBoolean("incluidoEnPrecio"));

                lista.add(is);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<InmuebleServicio> getAll() {
        List<InmuebleServicio> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InmuebleServicio is = new InmuebleServicio();

                is.setId(rs.getInt("id"));
                is.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                is.setServicio(new ServicioExtraDAO(rs.getInt("servicio_id")));
                is.setEstado(EstadoServicio.valueOf(rs.getString("estado")));
                is.setPrecioAdicional(rs.getDouble("precioAdicional"));
                is.setIncluidoEnPrecio(rs.getBoolean("incluidoEnPrecio"));

                lista.add(is);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}

