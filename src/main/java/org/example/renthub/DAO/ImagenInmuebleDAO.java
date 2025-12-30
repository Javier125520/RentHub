package org.example.renthub.DAO;

import org.example.renthub.model.ImagenInmueble;
import org.example.renthub.model.Inmueble;
import org.example.renthub.connection.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImagenInmuebleDAO {

    // =========================
    // SQL
    // =========================

    private static final String INSERT =
            "INSERT INTO imagen_inmueble (id_inmueble, url) VALUES (?, ?)";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM imagen_inmueble WHERE id_inmueble = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM imagen_inmueble WHERE id = ?";

    private static final String DELETE_BY_INMUEBLE =
            "DELETE FROM imagen_inmueble WHERE id_inmueble = ?";

    // =========================
    // INSERT
    // =========================

    public boolean insert(ImagenInmueble img) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, img.getInmueble().getIdInmueble());
            ps.setString(2, img.getUrl());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    img.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    // =========================
    // SELECT
    // =========================

    public List<ImagenInmueble> findByInmueble(Inmueble inmueble) throws SQLException {
        List<ImagenInmueble> imagenes = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ImagenInmueble img = new ImagenInmueble(
                        rs.getInt("id"),
                        inmueble,
                        rs.getString("url")
                );
                imagenes.add(img);
            }
        }
        return imagenes;
    }

    // =========================
    // DELETE
    // =========================

    public boolean deleteById(int idImagen) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setInt(1, idImagen);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteByInmueble(Inmueble inmueble) throws SQLException {
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(DELETE_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());
            return ps.executeUpdate() > 0;
        }
    }
}


