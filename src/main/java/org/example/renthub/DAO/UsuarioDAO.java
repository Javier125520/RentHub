package org.example.renthub.DAO;

import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Enum.RolUsuario;
import org.example.renthub.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final Connection conn;

    // =========================
    // SQL
    // =========================
    private static final String INSERT =
            "INSERT INTO usuario (nombre, correo, contraseña, rol) VALUES (?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE usuario SET nombre = ?, correo = ?, contraseña = ?, rol = ? WHERE id_usuario = ?";

    private static final String DELETE =
            "DELETE FROM usuario WHERE id_usuario = ?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM usuario WHERE id_usuario = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM usuario";

    private static final String SELECT_BY_CORREO =
            "SELECT * FROM usuario WHERE correo = ?";

    private static final String LOGIN =
            "SELECT * FROM usuario WHERE correo = ? AND contraseña = ?";

    private static final String EXISTS_CORREO =
            "SELECT COUNT(*) FROM usuario WHERE correo = ?";

    // =========================
    // CONSTRUCTORES
    // =========================

    /** Usa la conexión singleton */
    public UsuarioDAO() {
        this.conn = MySQLConnection.getConnection();
    }

    /** Permite inyectar conexión (tests / ampliaciones) */
    public UsuarioDAO(Connection conn) {
        this.conn = conn;
    }

    // =========================
    // CRUD
    // =========================

    public boolean insert(Usuario u) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getRol().name());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setIdUsuario(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(Usuario u) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getRol().name());
            ps.setInt(5, u.getIdUsuario());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int idUsuario) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // CONSULTAS
    // =========================

    public Usuario findById(int idUsuario) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUsuario(rs);
                }
            }
        }
        return null;
    }

    public Usuario findByCorreo(String correo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_CORREO)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUsuario(rs);
                }
            }
        }
        return null;
    }

    public List<Usuario> findAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapUsuario(rs));
            }
        }
        return usuarios;
    }

    // =========================
    // LOGIN
    // =========================

    public Usuario login(String correo, String contrasena) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(LOGIN)) {

            ps.setString(1, correo);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUsuario(rs);
                }
            }
        }
        return null;
    }

    // =========================
    // VALIDACIONES
    // =========================

    public boolean existsByCorreo(String correo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(EXISTS_CORREO)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // =========================
    // MAPEADOR
    // =========================

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("contraseña"),
                RolUsuario.valueOf(rs.getString("rol"))
        );
    }
}



