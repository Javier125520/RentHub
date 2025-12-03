package org.example.renthub.DAO;

import org.example.renthub.model.ImagenInmueble;
import org.example.renthub.model.Inmueble;
import org.example.renthub.connection.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImagenInmuebleDAO extends ImagenInmueble {

    // ========================= SQL =========================

    private static final String INSERT =
            "INSERT INTO imagen_inmueble (inmueble_id, url) VALUES (?, ?)";

    private static final String DELETE =
            "DELETE FROM imagen_inmueble WHERE id = ?";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM imagen_inmueble WHERE inmueble_id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM imagen_inmueble";

    private static final String SELECT_BY_INMUEBLE_ID =
            "SELECT * FROM imagen_inmueble WHERE inmueble_id = ?";

    // ========================= CONSTRUCTORES =========================

    public ImagenInmuebleDAO() {
        super();
    }

    public ImagenInmuebleDAO(int id, Inmueble inmueble, String url) {
        super(id, inmueble, url);
    }

    public ImagenInmuebleDAO(ImagenInmueble img) {
        super(img.getId(), img.getInmuebleId(), img.getUrl());
    }

    public ImagenInmuebleDAO(int id) {
        super();
        loadById(id);
    }

    // ========================= MÉTODOS PRINCIPALES =========================

    public boolean save() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, getInmuebleId().getIdInmueble());
            ps.setString(2, getUrl());

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


    // ========================= LOAD (USO INTERNO) =========================

    private void loadById(int id) {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return;

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM imagen_inmueble WHERE id = ?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                setId(rs.getInt("id"));
                setUrl(rs.getString("url"));
                setInmuebleId(new InmuebleDAO(rs.getInt("inmueble_id")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ========================= MÉTODOS ESTÁTICOS =========================

    public static List<ImagenInmueble> getByInmueble(int inmuebleId) {
        List<ImagenInmueble> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {

            ps.setInt(1, inmuebleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ImagenInmueble img = new ImagenInmueble();
                img.setId(rs.getInt("id"));
                img.setUrl(rs.getString("url"));
                img.setInmuebleId(new InmuebleDAO(rs.getInt("inmueble_id")));

                lista.add(img);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<ImagenInmueble> getAll() {
        List<ImagenInmueble> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ImagenInmueble img = new ImagenInmueble();
                img.setId(rs.getInt("id"));
                img.setUrl(rs.getString("url"));
                img.setInmuebleId(new InmuebleDAO(rs.getInt("inmueble_id")));

                lista.add(img);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ImagenInmueble> getByInmuebleId(int id) {
        List<ImagenInmueble> imagenes = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return imagenes;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ImagenInmueble img = new ImagenInmueble();
                img.setId(rs.getInt("id"));
                img.setUrl(rs.getString("url"));
                img.setInmuebleId(new InmuebleDAO(rs.getInt("inmueble_id")));

                imagenes.add(img);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imagenes;
    }
}

