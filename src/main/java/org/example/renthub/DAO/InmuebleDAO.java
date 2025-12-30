package org.example.renthub.DAO;


import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Enum.TipoInmueble;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InmuebleDAO {

    private final Connection conn;

    // =========================
    // SQL BASE
    // =========================

    private static final String INSERT =
            "INSERT INTO inmueble (tipo_inmueble, titulo, descripcion, direccion, ciudad, capacidad, numero_habitaciones, precio_noche, disponible, propietario_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE inmueble SET tipo_inmueble=?, titulo=?, descripcion=?, direccion=?, ciudad=?, capacidad=?, numero_habitaciones=?, precio_noche=?, disponible=?, propietario_id=? " +
                    "WHERE id_inmueble=?";

    private static final String DELETE =
            "DELETE FROM inmueble WHERE id_inmueble=?";

    private static final String SELECT_BY_ID =
            "SELECT * FROM inmueble WHERE id_inmueble=?";

    // =========================
    // CONSTRUCTORES
    // =========================

    public InmuebleDAO() {
        this.conn = MySQLConnection.getConnection();
    }

    public InmuebleDAO(Connection conn) {
        this.conn = conn;
    }

    // =========================
    // CRUD
    // =========================

    public boolean insert(Inmueble i) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, i.getTipoInmueble().name());
            ps.setString(2, i.getTitulo());
            ps.setString(3, i.getDescripcion());
            ps.setString(4, i.getDireccion());
            ps.setString(5, i.getCiudad());
            ps.setInt(6, i.getCapacidad());
            ps.setInt(7, i.getNumeroHabitaciones());
            ps.setDouble(8, i.getPrecioNoche());
            ps.setBoolean(9, i.isDisponible());
            ps.setInt(10, i.getPropietario().getIdUsuario());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    i.setIdInmueble(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(Inmueble i) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, i.getTipoInmueble().name());
            ps.setString(2, i.getTitulo());
            ps.setString(3, i.getDescripcion());
            ps.setString(4, i.getDireccion());
            ps.setString(5, i.getCiudad());
            ps.setInt(6, i.getCapacidad());
            ps.setInt(7, i.getNumeroHabitaciones());
            ps.setDouble(8, i.getPrecioNoche());
            ps.setBoolean(9, i.isDisponible());
            ps.setInt(10, i.getPropietario().getIdUsuario());
            ps.setInt(11, i.getIdInmueble());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int idInmueble) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idInmueble);
            return ps.executeUpdate() > 0;
        }
    }

    public Inmueble findById(int idInmueble) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, idInmueble);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapInmueble(rs);
            }
        }
        return null;
    }

    // =========================
    // BÚSQUEDA FLEXIBLE (OPCIÓN 2)
    // =========================

    public List<Inmueble> buscar(
            String ciudad,
            Double precioMin,
            Double precioMax,
            Integer capacidad,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            List<Integer> serviciosIncluidos
    ) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT i.* FROM inmueble i "
        );

        // join solo si hay servicios
        if (serviciosIncluidos != null && !serviciosIncluidos.isEmpty()) {
            sql.append("JOIN inmueble_servicio isv ON i.id_inmueble = isv.inmueble_id ");
        }

        sql.append("WHERE i.disponible = TRUE ");

        List<Object> params = new ArrayList<>();

        if (ciudad != null && !ciudad.isBlank()) {
            sql.append("AND i.ciudad LIKE ? ");
            params.add("%" + ciudad + "%");
        }

        if (precioMin != null) {
            sql.append("AND i.precio_noche >= ? ");
            params.add(precioMin);
        }

        if (precioMax != null) {
            sql.append("AND i.precio_noche <= ? ");
            params.add(precioMax);
        }

        if (capacidad != null) {
            sql.append("AND i.capacidad >= ? ");
            params.add(capacidad);
        }

        if (fechaEntrada != null && fechaSalida != null) {
            sql.append("""
                AND i.id_inmueble NOT IN (
                    SELECT r.inmueble_id
                    FROM reserva r
                    WHERE r.fecha_entrada < ?
                      AND r.fecha_salida > ?
                )
                """);
            params.add(Date.valueOf(fechaSalida));
            params.add(Date.valueOf(fechaEntrada));
        }

        if (serviciosIncluidos != null && !serviciosIncluidos.isEmpty()) {
            sql.append("AND isv.servicio_id IN (");
            for (int i = 0; i < serviciosIncluidos.size(); i++) {
                sql.append("?");
                if (i < serviciosIncluidos.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") AND isv.incluido_en_precio = TRUE ");
            params.addAll(serviciosIncluidos);
        }

        List<Inmueble> resultado = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapInmueble(rs));
                }
            }
        }

        return resultado;
    }

    // =========================
    // MAPEADOR
    // =========================

    private Inmueble mapInmueble(ResultSet rs) throws SQLException {

        Inmueble i = new Inmueble();

        i.setIdInmueble(rs.getInt("id_inmueble"));
        i.setTipoInmueble(TipoInmueble.valueOf(rs.getString("tipo_inmueble")));
        i.setTitulo(rs.getString("titulo"));
        i.setDescripcion(rs.getString("descripcion"));
        i.setDireccion(rs.getString("direccion"));
        i.setCiudad(rs.getString("ciudad"));
        i.setCapacidad(rs.getInt("capacidad"));
        i.setNumeroHabitaciones(rs.getInt("numero_habitaciones"));
        i.setPrecioNoche(rs.getDouble("precio_noche"));
        i.setDisponible(rs.getBoolean("disponible"));

        Usuario propietario = new Usuario();
        propietario.setIdUsuario(rs.getInt("propietario_id"));
        i.setPropietario(propietario);

        return i;
    }
}


