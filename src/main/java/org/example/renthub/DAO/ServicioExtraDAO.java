package org.example.renthub.DAO;

import org.example.renthub.model.ServicioExtra;
import org.example.renthub.connection.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ServicioExtraDAO extends ServicioExtra {

    // ========================= SQL =========================

    private static final String INSERT =
            "INSERT INTO servicio_extra (nombre, descripcion) VALUES (?, ?)";

    private static final String UPDATE =
            "UPDATE servicio_extra SET nombre = ?, descripcion = ? WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM servicio_extra WHERE id = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM servicio_extra WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM servicio_extra";


    // ========================= CONSTRUCTORES =========================

    public ServicioExtraDAO() {
        super();
    }

    public ServicioExtraDAO(ServicioExtra s) {
        super(s.getId(), s.getNombre(), s.getDescripcion());
    }

    public ServicioExtraDAO(String nombre, String descripcion) {
        super(0, nombre, descripcion);
    }

    public ServicioExtraDAO(int id) {
        super();
        loadById(id);
    }


    // ========================= MÉTODOS PRINCIPALES =========================

    public boolean save() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, getNombre());
            ps.setString(2, getDescripcion());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    setId(rs.getInt(1));
                }
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

            ps.setString(1, getNombre());
            ps.setString(2, getDescripcion());
            ps.setInt(3, getId());

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
                setNombre(rs.getString("nombre"));
                setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ========================= MÉTODOS ESTÁTICOS =========================

    public static List<ServicioExtra> getAll() {
        List<ServicioExtra> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ServicioExtra s = new ServicioExtra();
                s.setId(rs.getInt("id"));
                s.setNombre(rs.getString("nombre"));
                s.setDescripcion(rs.getString("descripcion"));

                lista.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}


