package org.example.renthub.DAO;


import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InmuebleDAO extends Inmueble {

    // ────────────────────────────────────────────────
    //  CONSULTAS NECESARIAS
    // ────────────────────────────────────────────────

    private static final String INSERT =
            "INSERT INTO inmueble (titulo, descripcion, direccion, ciudad, capacidad, numero_habitaciones, precio_noche, disponible, id_propietario) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE =
            "UPDATE inmueble SET titulo=?, descripcion=?, direccion=?, ciudad=?, capacidad=?, numero_habitaciones=?, precio_noche=?, disponible=?, id_propietario=? " +
                    "WHERE id_inmueble = ?;";

    private static final String DELETE =
            "DELETE FROM inmueble WHERE id_inmueble = ?;";

    private static final String SELECT_BY_ID =
            "SELECT * FROM inmueble WHERE id_inmueble = ?;";

    private static final String SELECT_ALL =
            "SELECT * FROM inmueble;";

    private static final String SELECT_BY_PROPIETARIO =
            "SELECT * FROM inmueble WHERE id_propietario = ?;";

    private static final String SELECT_DISPONIBLES =
            "SELECT * FROM inmueble WHERE disponible = 1;";

    private static final String SELECT_BY_CIUDAD =
            "SELECT * FROM inmueble WHERE ciudad LIKE ?;";

    private static final String SELECT_FILTROS =
            "SELECT * FROM inmueble WHERE ciudad LIKE ? AND capacidad >= ? AND precio_noche <= ?;";


    // ────────────────────────────────────────────────
    //  CONSTRUCTORES
    // ────────────────────────────────────────────────

    public InmuebleDAO() {
        super();
    }

    public InmuebleDAO(Inmueble i) {
        super(i.getIdInmueble(), i.getTitulo(), i.getDescripcion(),
                i.getDireccion(), i.getCiudad(), i.getCapacidad(),
                i.getNumeroHabitaciones(), i.getPrecioNoche(),
                i.isDisponible(), i.getPropietario());
    }

    public InmuebleDAO(int id) {
        super();
        getById(id);
    }


    // ────────────────────────────────────────────────
    //  MÉTODOS CRUD
    // ────────────────────────────────────────────────

    public boolean save() throws SQLException {
        Connection con = MySQLConnection.getConnection();

        // UPDATE
        if (this.getIdInmueble() != 0) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
                ps.setString(1, getTitulo());
                ps.setString(2, getDescripcion());
                ps.setString(3, getDireccion());
                ps.setString(4, getCiudad());
                ps.setInt(5, getCapacidad());
                ps.setInt(6, getNumeroHabitaciones());
                ps.setDouble(7, getPrecioNoche());
                ps.setBoolean(8, isDisponible());
                ps.setInt(9, getPropietario().getIdUsuario());
                ps.setInt(10, getIdInmueble());
                return ps.executeUpdate() > 0;
            }
        }

        // INSERT
        try (PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, getTitulo());
            ps.setString(2, getDescripcion());
            ps.setString(3, getDireccion());
            ps.setString(4, getCiudad());
            ps.setInt(5, getCapacidad());
            ps.setInt(6, getNumeroHabitaciones());
            ps.setDouble(7, getPrecioNoche());
            ps.setBoolean(8, isDisponible());
            ps.setInt(9, getPropietario().getIdUsuario());

            boolean inserted = ps.executeUpdate() > 0;

            if (inserted) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) this.setIdInmueble(rs.getInt(1));
            }

            return inserted;
        }
    }

    public boolean remove() throws SQLException {
        Connection con = MySQLConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(DELETE)) {
            ps.setInt(1, getIdInmueble());
            return ps.executeUpdate() > 0;
        }
    }


    // ────────────────────────────────────────────────
    //  CONSULTAS PARA CARGAR DATOS
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

    public static List<Inmueble> getAll() {
        List<Inmueble> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_ALL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(loadInmueble(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Inmueble> getByPropietario(Usuario propietario) {
        List<Inmueble> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_PROPIETARIO)) {
            ps.setInt(1, propietario.getIdUsuario());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) lista.add(loadInmueble(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Inmueble> getDisponibles() {
        List<Inmueble> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_DISPONIBLES)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(loadInmueble(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Inmueble> buscarPorCiudad(String ciudad) {
        List<Inmueble> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_CIUDAD)) {
            ps.setString(1, "%" + ciudad + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) lista.add(loadInmueble(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<Inmueble> buscarConFiltros(String ciudad, int capacidad, double precioMax) {
        List<Inmueble> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_FILTROS)) {
            ps.setString(1, "%" + ciudad + "%");
            ps.setInt(2, capacidad);
            ps.setDouble(3, precioMax);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(loadInmueble(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    // ────────────────────────────────────────────────
    //  MÉTODOS AUXILIARES DE MAPEO
    // ────────────────────────────────────────────────

    private static Inmueble loadInmueble(ResultSet rs) throws SQLException {
        InmuebleDAO i = new InmuebleDAO();
        i.loadFromResultSet(rs);
        return i;
    }

    private void loadFromResultSet(ResultSet rs) throws SQLException {
        this.setIdInmueble(rs.getInt("id_inmueble"));
        this.setTitulo(rs.getString("titulo"));
        this.setDescripcion(rs.getString("descripcion"));
        this.setDireccion(rs.getString("direccion"));
        this.setCiudad(rs.getString("ciudad"));
        this.setCapacidad(rs.getInt("capacidad"));
        this.setNumeroHabitaciones(rs.getInt("numero_habitaciones"));
        this.setPrecioNoche(rs.getDouble("precio_noche"));
        this.setDisponible(rs.getBoolean("disponible"));

        // Cargar propietario
        int idProp = rs.getInt("id_propietario");
        this.setPropietario(new UsuarioDAO(idProp));

        // Cargar imágenes y servicios cuando sea necesario:
        // this.setImagenes(ImagenInmuebleDAO.getByInmueble(this.getId_inmueble()))
        // this.setServicios(InmuebleServicioDAO.getByInmueble(this.getId_inmueble()))
    }
}

