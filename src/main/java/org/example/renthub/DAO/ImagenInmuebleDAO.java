package org.example.renthub.DAO;

import org.example.renthub.model.ImagenInmueble;
import org.example.renthub.model.Inmueble;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para las imágenes asociadas a los inmuebles.
 * Implementa el patrón Active Record heredando del modelo base ImagenInmueble.
 */
public class ImagenInmuebleDAO extends ImagenInmueble {

    // =========================================================================
    // SENTENCIAS SQL
    // =========================================================================
    private static final String INSERT =
            "INSERT INTO imagen_inmueble (inmueble_id, url) VALUES (?, ?);";

    private static final String SELECT_BY_INMUEBLE =
            "SELECT * FROM imagen_inmueble WHERE inmueble_id = ?;";

    private static final String DELETE_BY_ID =
            "DELETE FROM imagen_inmueble WHERE id = ?;";

    private static final String DELETE_BY_INMUEBLE =
            "DELETE FROM imagen_inmueble WHERE inmueble_id = ?;";

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================

    /** Constructor vacío por defecto */
    public ImagenInmuebleDAO() {
        super();
    }

    /** Constructor por copia para transformar un modelo plano en un objeto activo */
    public ImagenInmuebleDAO(ImagenInmueble img) {
        super();
        this.setId(img.getId());
        this.setInmueble(img.getInmueble());
        this.setUrl(img.getUrl());
    }

    /** Constructor parametrizado completo */
    public ImagenInmuebleDAO(int id, Inmueble inmueble, String url) {
        super(id, inmueble, url);
    }

    // =========================================================================
    // MÉTODOS CRUD DE INSTANCIA (Active Record)
    // =========================================================================

    /**
     * Inserta la imagen actual en la base de datos y recupera el ID autogenerado.
     * @return true si la inserción fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre algún error en la consulta SQL.
     */
    public boolean insert() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, this.getInmueble().getIdInmueble());
            ps.setString(2, this.getUrl());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // Recuperar el ID incremental asignado por MySQL
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Elimina de la base de datos la imagen correspondiente a la instancia actual.
     * @return true si se eliminó correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error en la consulta SQL.
     */
    public boolean remove() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setInt(1, this.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA ESTÁTICOS
    // =========================================================================

    /**
     * Recupera todas las imágenes indexadas para un inmueble específico.
     * @param inmueble El objeto Inmueble sobre el que buscar.
     * @return Lista de objetos ImagenInmueble pertenecientes a la propiedad.
     * @throws SQLException Si ocurre algún error en la consulta SQL.
     */
    public static List<ImagenInmueble> findByInmueble(Inmueble inmueble) throws SQLException {
        List<ImagenInmueble> imagenes = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_INMUEBLE)) {
            ps.setInt(1, inmueble.getIdInmueble());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Mapeamos los resultados construyendo directamente instancias Active Record
                    ImagenInmuebleDAO img = new ImagenInmuebleDAO(
                            rs.getInt("id"),
                            inmueble,
                            rs.getString("url")
                    );
                    imagenes.add(img);
                }
            }
        }
        return imagenes;
    }
}