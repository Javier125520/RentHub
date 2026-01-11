package org.example.renthub.DAO;


import org.example.renthub.model.*;
import org.example.renthub.model.enums.EstadoServicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InmuebleServicioDAO {

    private final Connection conn;

    private static final String INSERT =
            "INSERT INTO inmueble_servicio (id_inmueble, id_servicio, precio_adicional, estado_servicio) VALUES (?, ?, ?)";

    private static final String DELETE =
            "DELETE FROM inmueble_servicio WHERE id_inmueble = ? AND id_servicio = ?";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT isv.id, isv.precio_adicional, isv.estado, s.* " +
                    "FROM inmueble_servicio isv " +
                    "JOIN servicio_extra s ON s.id_servicio = isv.id_servicio " +
                    "WHERE isv.id_inmueble = ?";

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
        }
    }

    public boolean removeServicio(Inmueble inmueble, ServicioExtra servicio) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, inmueble.getIdInmueble());
            ps.setInt(2, servicio.getIdServicio());
            return ps.executeUpdate() > 0;
        }
    }

    public List<InmuebleServicio> getServiciosByInmueble(Inmueble inmueble) throws SQLException {

        List<InmuebleServicio> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServicioExtra servicio = new ServicioExtra(
                        rs.getInt("id_servicio"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );

                InmuebleServicio is = new InmuebleServicio(
                        rs.getInt("id"),
                        inmueble,
                        servicio,
                        rs.getDouble("precio_adicional"),
                        EstadoServicio.valueOf(rs.getString("estado"))
                );

                lista.add(is);
            }
        }
        return lista;
    }

    public void deleteByInmueble(Inmueble inmueble) {
    }
}


