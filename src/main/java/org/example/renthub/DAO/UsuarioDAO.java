package org.example.renthub.DAO;

import org.example.renthub.model.Usuario;
import org.example.renthub.model.enums.RolUsuario;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;

/**
 * Clase de Acceso a Datos (DAO) para los Usuarios del ecosistema.
 * Procesa accesos, registros de cuentas y validaciones criptográficas de logins.
 */
public class UsuarioDAO extends Usuario {

    // =========================================================================
    // SENTENCIAS SQL
    // =========================================================================
    private static final String INSERT = "INSERT INTO usuario (nombre, correo, contraseña, rol) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE usuario SET nombre = ?, correo = ?, contraseña = ?, rol = ? WHERE id_usuario = ?";
    private static final String DELETE = "DELETE FROM usuario WHERE id_usuario = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM usuario WHERE id_usuario = ?";
    private static final String SELECT_BY_CORREO = "SELECT * FROM usuario WHERE correo = ?";

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================
    public UsuarioDAO() {
        super();
    }

    /** Constructor parametrizado completo */
    public UsuarioDAO(int id, String nombre, String correo, String pass, RolUsuario rol) {
        super(id, nombre, correo, pass, rol);
    }

    /** Constructor por copia: Sincroniza al usuario logueado en la sesión hacia una instancia activa */
    public UsuarioDAO(Usuario u) {
        super(u.getIdUsuario(), u.getNombre(), u.getCorreo(), u.getContrasena(), u.getRol());
    }

    // =========================================================================
    // MÉTODOS CRUD DE INSTANCIA (Active Record)
    // =========================================================================

    /** Registra e inserta un nuevo perfil de usuario en la base de datos */
    public boolean insert() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.getNombre());
            ps.setString(2, this.getCorreo());
            ps.setString(3, this.getContrasena());
            ps.setString(4, this.getRol().name());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) this.setIdUsuario(rs.getInt(1));
                return true;
            }
        }
        return false;
    }

    /** Actualiza la información personal o contraseñas del usuario */
    public boolean update() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, this.getNombre());
            ps.setString(2, this.getCorreo());
            ps.setString(3, this.getContrasena());
            ps.setString(4, this.getRol().name());
            ps.setInt(5, this.getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA ESTÁTICOS
    // =========================================================================

    /** Encuentra un usuario específico por su ID indexado en la base de datos */
    public static UsuarioDAO getById(int id) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UsuarioDAO(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("contraseña"), // Sincronizado con la col de la BD en español
                        RolUsuario.valueOf(rs.getString("rol"))
                );
            }
        }
        return null;
    }

    /** Busca un usuario por su email único (Esencial para comprobar credenciales en el Login) */
    public static Usuario findByCorreo(String correo) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_CORREO)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UsuarioDAO(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        RolUsuario.valueOf(rs.getString("rol"))
                );
            }
        }
        return null;
    }

    /** Elimina la cuenta física de un usuario del sistema */
    public static boolean delete(int id) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
