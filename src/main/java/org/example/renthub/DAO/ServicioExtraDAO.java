package org.example.renthub.DAO;

import org.example.renthub.model.ServicioExtra;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioExtraDAO extends ServicioExtra {

    // ────────────────────────────────────────────────
    //  CONSULTAS SQL
    // ────────────────────────────────────────────────

    private static final String INSERT =
            "INSERT INTO servicio_extra (nombre, descripcion) VALUES (?, ?);";

    private static final String UPDATE =
            "UPDATE servicio_extra SET nombre=?, descripcion=? WHERE id_servicio_extra = ?;";

    private static final String DELETE =
            "DELETE FROM servicio_extra WHERE id_servicio_extra = ?;";

    private static final String SELECT_BY_ID =
            "SELECT * FROM servicio_extra WHERE id_servicio_extra = ?;";

    private static final String SELECT_ALL =
            "SELECT * FROM servicio_extra;";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT se.* FROM servicio_extra se " +
                    "JOIN inmueble_servicio ins ON se.id_servicio_extra = ins.id_servicio_extra " +
                    "WHERE ins.id_inmueble = ?;";

    private static final String CHECK_EXISTE_EN_INMUEBLE =
            "SELECT COUNT(*) AS total FROM inmueble_servicio " +
                    "WHERE id_inmueble = ? AND id_servicio_extra = ?;";


    // ────────────────────────────────────────────────
    //  CONSTRUCTORES
    // ────────────────────────────────────────────────

    public ServicioExtraDAO() {
        super();
    }

    public ServicioExtraDAO(ServicioExtra se) {
        super(se.getIdServicio(), se.getNombre(), se.getDescripcion());
    }

    public ServicioExtraDAO(int id) {
        super();
        getById(id);
    }


    // ────────────────────────────────────────────────
    //  MÉTODO SAVE
    // ────────────────────────────────────────────────

    public boolean save() throws SQLException {
        Connection con = MySQLConnection.getConnection();

        // UPDATE
        if (this.getIdServicio() != 0) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
                ps.setString(1, getNombre());
                ps.setString(2, getDescripcion());
                ps.setInt(3, getIdServicio());
                return ps.executeUpdate() > 0;
            }
        }

        // INSERT
        try (PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, getNombre());
            ps.setString(2, getDescripcion());

            boolean inserted = ps.executeUpdate() > 0;

            if (inserted) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) this.setIdServicio(rs.getInt(1));
            }
            return inserted;
        }
    }


    // ────────────────────────────────────────────────
    //  DELETE
    // ────────────────────────────────────────────────

    public boolean remove() throws SQLException {
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(DELETE)) {
            ps.setInt(1, getIdServicio());
            return ps.executeUpdate() > 0;
        }
    }


    // ────────────────────────────────────────────────
    //  SELECT BY ID
    // ────────────────────────────────────────────────

    public void getById(int id) {
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                loadFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ────────────────────────────────────────────────
    //  CONSULTAS ESTÁTICAS
    // ────────────────────────────────────────────────

    public static List<ServicioExtra> getAll() {
        List<ServicioExtra> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_ALL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServicioExtraDAO s = new ServicioExtraDAO();
                s.loadFromResultSet(rs);
                lista.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static List<ServicioExtra> getByInmueble(int idInmueble) {
        List<ServicioExtra> lista = new ArrayList<>();
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_INMUEBLE)) {
            ps.setInt(1, idInmueble);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ServicioExtraDAO se = new ServicioExtraDAO();
                se.loadFromResultSet(rs);
                lista.add(se);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static boolean existeEnInmueble(int idInmueble, int idServicio) {
        Connection con = MySQLConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement(CHECK_EXISTE_EN_INMUEBLE)) {
            ps.setInt(1, idInmueble);
            ps.setInt(2, idServicio);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total") > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    // ────────────────────────────────────────────────
    //  MÉTODO AUXILIAR MAPEO
    // ────────────────────────────────────────────────

    private void loadFromResultSet(ResultSet rs) throws SQLException {
        this.setIdServicio(rs.getInt("id_servicio_extra"));
        this.setNombre(rs.getString("nombre"));
        this.setDescripcion(rs.getString("descripcion"));
    }
}


