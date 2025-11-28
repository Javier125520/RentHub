package org.example.renthub.DAO;

import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.RolUsuario;
import org.example.renthub.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends Usuario {

    // ============= SQL =============
    private static final String INSERT =
            "INSERT INTO usuario (nombre, correo, contrasena, rol) VALUES (?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE usuario SET nombre = ?, correo = ?, contrasena = ?, rol = ? WHERE id = ?";

    private static final String DELETE =
            "DELETE FROM usuario WHERE id = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM usuario WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM usuario";

    private static final String SELECT_BY_CORREO =
            "SELECT * FROM usuario WHERE correo = ?";




    // ============= CONSTRUCTORES =============

    public UsuarioDAO() {
        super();
    }

    public UsuarioDAO(int id) {
        super();
        loadById(id);
    }

    public UsuarioDAO(Usuario u) {
        super(u.getId(), u.getNombre(), u.getCorreo(), u.getContrasena(), u.getRol());
    }

    public UsuarioDAO(String nombre, String correo, String contrasena, RolUsuario rol) {
        super(0, nombre, correo, contrasena, rol);
    }


    // ============= MÉTODOS PRINCIPALES =============

    public boolean save() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try {
            PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, getNombre());
            ps.setString(2, getCorreo());
            ps.setString(3, getContrasena());
            ps.setString(4, getRol().name());

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

        try {
            PreparedStatement ps = conn.prepareStatement(UPDATE);
            ps.setString(1, getNombre());
            ps.setString(2, getCorreo());
            ps.setString(3, getContrasena());
            ps.setString(4, getRol().name());
            ps.setInt(5, getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean delete() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try {
            PreparedStatement ps = conn.prepareStatement(DELETE);
            ps.setInt(1, getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ============= CARGA POR ID =============

    public void loadById(int id) {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return;

        try {
            PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                setId(rs.getInt("id"));
                setNombre(rs.getString("nombre"));
                setCorreo(rs.getString("correo"));
                setContrasena(rs.getString("contrasena"));
                setRol(RolUsuario.valueOf(rs.getString("rol")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ============= GET ALL =============

    public static List<Usuario> getAll() {
        List<Usuario> list = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return list;

        try {
            PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(RolUsuario.valueOf(rs.getString("rol")));
                list.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static UsuarioDAO buscarPorCorreo(String correo) {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return null;

        try {
            PreparedStatement ps = conn.prepareStatement(SELECT_BY_CORREO);
            ps.setString(1, correo);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UsuarioDAO u = new UsuarioDAO();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(RolUsuario.valueOf(rs.getString("rol")));
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

