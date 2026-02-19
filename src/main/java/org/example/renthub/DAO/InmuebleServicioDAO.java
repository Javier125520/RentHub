package org.example.renthub.DAO;


import org.example.renthub.model.*;
import org.example.renthub.model.enums.EstadoServicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InmuebleServicioDAO {

    private final Connection conn;

    private static final String INSERT =
            "INSERT INTO inmueble_servicio (id_inmueble, id_servicio, precio_adicional, estado_servicio) VALUES (?, ?, ?, ?)";

    private static final String DELETE =
            "DELETE FROM inmueble_servicio WHERE id_inmueble = ? AND id_servicio = ?";

    private static final String SELECT_SERVIOS_BY_INMUEBLE =
            "SELECT isv.id, isv.precio_adicional, isv.estado, s.* " +
                    "FROM inmueble_servicio isv " +
                    "JOIN servicio_extra s ON s.id_servicio = isv.id_servicio " +
                    "WHERE isv.id_inmueble = ?";

    private static final String SELECT_BY_INMUEBLE =
            """
            SELECT isv.*, se.nombre
            FROM inmueble_servicio isv
            JOIN servicio_extra se 
                ON isv.id_servicio = se.id_servicio
            WHERE isv.id_inmueble = ?
            """;

    private static final String DELETE_BY_INMUEBLE =
            "DELETE FROM inmueble_servicio WHERE id_inmueble = ?";

    public InmuebleServicioDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addServicio(Inmueble inmueble, ServicioExtra servicio, double precioAdicional, EstadoServicio estado) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setInt(1, inmueble.getIdInmueble());
            ps.setInt(2, servicio.getIdServicio());
            ps.setDouble(3, precioAdicional);
            ps.setString(4, estado.name());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<InmuebleServicio> findByInmueble(Inmueble inmueble) throws SQLException {
        List<InmuebleServicio> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InmuebleServicio is = new InmuebleServicio();
                is.setPrecioAdicional(rs.getDouble("precio_adicional"));
                is.setEstado(EstadoServicio.valueOf(rs.getString("estado_servicio")));

                ServicioExtra s = new ServicioExtra();
                s.setIdServicio(rs.getInt("id_servicio"));
                s.setNombre(rs.getString("nombre"));
                is.setServicio(s);

                lista.add(is);
            }
            return lista;
        }
    }

    public void deleteByInmueble (Inmueble inmueble){
        try (PreparedStatement ps = conn.prepareStatement(DELETE_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


