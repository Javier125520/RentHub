package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReseñaDAO extends Reseña {

    // ======================== SQL ========================

    private static final String INSERT =
            "INSERT INTO resena (puntuacion, comentario, fecha, inmueble_id, usuario_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE resena SET puntuacion=?, comentario=?, fecha=?, inmueble_id=?, usuario_id=? " +
                    "WHERE id=?";

    private static final String DELETE =
            "DELETE FROM resena WHERE id=?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM resena WHERE id=?";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM resena WHERE inmueble_id=? ORDER BY fecha DESC";

    private static final String SELECT_BY_USUARIO =
            "SELECT * FROM resena WHERE usuario_id=? ORDER BY fecha DESC";

    private static final String SELECT_ALL =
            "SELECT * FROM resena ORDER BY fecha DESC";


    // ======================== CONSTRUCTORES ========================

    public ReseñaDAO() {
        super();
    }

    public ReseñaDAO(Reseña r) {
        super(
                r.getId(),
                r.getPuntuacion(),
                r.getComentario(),
                r.getFecha(),
                r.getInmueble(),
                r.getHuesped()
        );
    }

    public ReseñaDAO(int id) {
        super();
        loadById(id);
    }

    public ReseñaDAO(int puntuacion, String comentario,
                     LocalDate fecha, Inmueble inmueble, Usuario usuario) {
        super(0, puntuacion, comentario, fecha, inmueble, usuario);
    }


    // ======================== SAVE ========================

    public boolean save() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, getPuntuacion());
            ps.setString(2, getComentario());
            ps.setDate(3, Date.valueOf(getFecha()));
            ps.setInt(4, getInmueble().getIdInmueble());
            ps.setInt(5, getHuesped().getIdUsuario());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) setId(rs.getInt(1));
            }

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ======================== UPDATE ========================

    public boolean update() {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setInt(1, getPuntuacion());
            ps.setString(2, getComentario());
            ps.setDate(3, Date.valueOf(getFecha()));
            ps.setInt(4, getInmueble().getIdInmueble());
            ps.setInt(5, getHuesped().getIdUsuario());
            ps.setInt(6, getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ======================== DELETE ========================

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


    // ======================== LOAD BY ID ========================

    public void loadById(int id) {
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                setId(rs.getInt("id"));
                setPuntuacion(rs.getInt("puntuacion"));
                setComentario(rs.getString("comentario"));
                setFecha(rs.getDate("fecha").toLocalDate());

                setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                setHuesped(new UsuarioDAO(rs.getInt("usuario_id")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ======================== MÉTODOS ESTÁTICOS ========================

    public static List<Reseña> getByInmueble(int inmuebleId) {
        List<Reseña> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {

            ps.setInt(1, inmuebleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Reseña r = new Reseña();

                r.setId(rs.getInt("id"));
                r.setPuntuacion(rs.getInt("puntuacion"));
                r.setComentario(rs.getString("comentario"));
                r.setFecha(rs.getDate("fecha").toLocalDate());
                r.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                r.setHuesped(new UsuarioDAO(rs.getInt("usuario_id")));

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<Reseña> getByUsuario(int usuarioId) {
        List<Reseña> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_USUARIO)) {

            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Reseña r = new Reseña();

                r.setId(rs.getInt("id"));
                r.setPuntuacion(rs.getInt("puntuacion"));
                r.setComentario(rs.getString("comentario"));
                r.setFecha(rs.getDate("fecha").toLocalDate());
                r.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                r.setHuesped(new UsuarioDAO(rs.getInt("usuario_id")));

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<Reseña> getAll() {
        List<Reseña> lista = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Reseña r = new Reseña();

                r.setId(rs.getInt("id"));
                r.setPuntuacion(rs.getInt("puntuacion"));
                r.setComentario(rs.getString("comentario"));
                r.setFecha(rs.getDate("fecha").toLocalDate());
                r.setInmueble(new InmuebleDAO(rs.getInt("inmueble_id")));
                r.setHuesped(new UsuarioDAO(rs.getInt("usuario_id")));

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}

