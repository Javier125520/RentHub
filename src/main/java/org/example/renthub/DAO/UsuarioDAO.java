package org.example.renthub.DAO;

import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.RolUsuario;
import org.example.renthub.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends Usuario {

    // ───────────────────────────────────────────
    //  CONSULTAS SQL NECESARIAS
    // ───────────────────────────────────────────

    private static final String INSERT_USUARIO =
            "INSERT INTO usuario (nombre, correo, contraseña, rol) VALUES (?, ?, ?, ?);";

    private static final String UPDATE_USUARIO =
            "UPDATE usuario SET nombre = ?, correo = ?, contraseña = ?, rol = ? WHERE id_usuario = ?;";

    private static final String DELETE_USUARIO =
            "DELETE FROM usuario WHERE id_usuario = ?;";

    private static final String SELECT_BY_ID =
            "SELECT * FROM usuario WHERE id_usuario = ?;";

    private static final String SELECT_BY_CORREO =
            "SELECT * FROM usuario WHERE correo = ?;";

    private static final String SELECT_ALL =
            "SELECT * FROM usuario;";

    private static final String SELECT_BY_ROL =
            "SELECT * FROM usuario WHERE rol = ?;";

    private static final String LOGIN =
            "SELECT * FROM usuario WHERE correo = ? AND contraseña = ?;";


    // ───────────────────────────────────────────
    //  CONSTRUCTORES NECESARIOS
    // ───────────────────────────────────────────

    public UsuarioDAO() {
        super();
    }

    public UsuarioDAO(int id, String nombre, String correo, String contraseña, RolUsuario rol) {
        super(id, nombre, correo, contraseña, rol);
    }

    public UsuarioDAO(Usuario u) {
        super(u.getIdUsuario(), u.getNombre(), u.getCorreo(), u.getContrasena(), u.getRol());
    }

    public UsuarioDAO(int id) {
        super();
        getById(id);
    }


    // ───────────────────────────────────────────
    //  MÉTODOS CRUD
    // ───────────────────────────────────────────

    public boolean save() throws SQLException {
        Connection con = MySQLConnection.getConnection();

        // Si el usuario existe → update
        if (this.getIdUsuario() != 0) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE_USUARIO)) {
                ps.setString(1, getNombre());
                ps.setString(2, getCorreo());
                ps.setString(3, getContrasena());
                ps.setString(4, getRol().name());
                ps.setInt(5, getIdUsuario());
                return ps.executeUpdate() > 0;
            }
        }

        // Si no existe → insert
        try (PreparedStatement ps = con.prepareStatement(INSERT_USUARIO, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, getNombre());
            ps.setString(2, getCorreo());
            ps.setString(3, getContrasena());
            ps.setString(4, getRol().name());

            boolean inserted = ps.executeUpdate() > 0;

            if (inserted) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) this.setIdUsuario(rs.getInt(1));
            }

            return inserted;
        }
    }

    public boolean remove() throws SQLException {
        Connection con = MySQLConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(DELETE_USUARIO)) {
            ps.setInt(1, getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }


    // ───────────────────────────────────────────
    //  MÉTODOS DE CONSULTA
    // ───────────────────────────────────────────

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

    public static Usuario getByCorreo(String correo) {
        Connection con = MySQLConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_CORREO)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return loadUser(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Usuario login(String correo, String pass) {
        Connection con = MySQLConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(LOGIN)) {
            ps.setString(1, correo);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return loadUser(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Usuario> getAll() {
        List<Usuario> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(SELECT_ALL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(loadUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static List<Usuario> getByRol(RolUsuario rol) {
        List<Usuario> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_ROL)) {
            ps.setString(1, rol.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(loadUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    // ───────────────────────────────────────────
    //  MÉTODO AUXILIAR PARA MAPEAR RESULTSET → OBJETO
    // ───────────────────────────────────────────

    private static Usuario loadUser(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("contraseña"),
                RolUsuario.valueOf(rs.getString("rol"))
        );
    }

    private void loadFromResultSet(ResultSet rs) throws SQLException {
        this.setIdUsuario(rs.getInt("id_usuario"));
        this.setNombre(rs.getString("nombre"));
        this.setCorreo(rs.getString("correo"));
        this.setContrasena(rs.getString("contraseña"));
        this.setRol(RolUsuario.valueOf(rs.getString("rol")));
    }
}

