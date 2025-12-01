package org.example.renthub.DAO;


import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InmuebleDAO extends Inmueble {

    // ========================= SQL =========================

    private static final String INSERT =
            "INSERT INTO inmueble (titulo, descripcion, direccion, ciudad, precioNoche, disponible, propietario_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE inmueble SET titulo=?, descripcion=?, direccion=?, ciudad=?, precioNoche=?, disponible=?, propietario_id=? " +
                    "WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM inmueble WHERE id = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM inmueble WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM inmueble";

    private static final String SELECT_BY_PROPIETARIO =
            "SELECT * FROM inmueble WHERE propietario_id = ?";


    // ========================= CONSTRUCTORES =========================

    public InmuebleDAO() {
        super();
    }

    public InmuebleDAO(int id) {
        super();
        loadById(id);
    }

    public InmuebleDAO(Inmueble i) {
        super(
                i.getId(),
                i.getTitulo(),
                i.getDescripcion(),
                i.getDireccion(),
                i.getCiudad(),
                i.getCapacidad(),
                i.getNumeroHabitaciones(),
                i.getPrecioNoche(),
                i.isDisponible(),
                i.getPropietario()
        );
    }

    public InmuebleDAO(String titulo, String descripcion, String direccion,
                       String ciudad, double precioNoche, boolean disponible, Usuario propietario) {

        super(0, titulo, descripcion, direccion, ciudad, precioNoche, disponible, propietario);
    }


    // ========================= MÉTODOS PRINCIPALES =========================

    public boolean save() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, getTitulo());
            ps.setString(2, getDescripcion());
            ps.setString(3, getDireccion());
            ps.setString(4, getCiudad());
            ps.setDouble(5, getPrecioNoche());
            ps.setBoolean(6, isDisponible());
            ps.setInt(7, getPropietario().getId());

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


    public boolean update() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, getTitulo());
            ps.setString(2, getDescripcion());
            ps.setString(3, getDireccion());
            ps.setString(4, getCiudad());
            ps.setDouble(5, getPrecioNoche());
            ps.setBoolean(6, isDisponible());
            ps.setInt(7, getPropietario().getId());
            ps.setInt(8, getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


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
                setTitulo(rs.getString("titulo"));
                setDescripcion(rs.getString("descripcion"));
                setDireccion(rs.getString("direccion"));
                setCiudad(rs.getString("ciudad"));
                setPrecioNoche(rs.getDouble("precioNoche"));
                setDisponible(rs.getBoolean("disponible"));

                int propietarioId = rs.getInt("propietario_id");
                setPropietario(new UsuarioDAO(propietarioId)); // carga automática
            }

            // cargar relaciones
            loadImagenes();
            loadServicios();
            loadResenas();
            loadReservas();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ========================= LOAD RELACIONES =========================

    private void loadImagenes() {
        this.setImagenes(ImagenInmuebleDAO.getByInmueble(this.getId()));
    }

    private void loadServicios() {
        this.setServicios(InmuebleServicioDAO.getByInmueble(this.getId()));
    }

    private void loadResenas() {
        this.setResenas(ReseñaDAO.getByInmueble(this.getId()));
    }

    private void loadReservas() {
        this.setReservas(ReservaDAO.getByInmueble(this.getId()));
    }


    // ========================= MÉTODOS ESTÁTICOS =========================

    public static List<Inmueble> getAll() {
        List<Inmueble> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Inmueble i = new Inmueble();
                i.setId(rs.getInt("id"));
                i.setTitulo(rs.getString("titulo"));
                i.setDescripcion(rs.getString("descripcion"));
                i.setDireccion(rs.getString("direccion"));
                i.setCiudad(rs.getString("ciudad"));
                i.setPrecioNoche(rs.getDouble("precioNoche"));
                i.setDisponible(rs.getBoolean("disponible"));
                i.setPropietario(new UsuarioDAO(rs.getInt("propietario_id")));

                lista.add(i);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<Inmueble> getByPropietario(int propietarioId) {
        List<Inmueble> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_PROPIETARIO)) {

            ps.setInt(1, propietarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Inmueble i = new Inmueble();
                i.setId(rs.getInt("id"));
                i.setTitulo(rs.getString("titulo"));
                i.setDescripcion(rs.getString("descripcion"));
                i.setDireccion(rs.getString("direccion"));
                i.setCiudad(rs.getString("ciudad"));
                i.setPrecioNoche(rs.getDouble("precioNoche"));
                i.setDisponible(rs.getBoolean("disponible"));
                i.setPropietario(new UsuarioDAO(rs.getInt("propietario_id")));

                lista.add(i);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

}


