package org.example.renthub.DAO;

import org.example.renthub.model.*;
import org.example.renthub.connection.MySQLConnection;
import java.sql.*;

/**
 * Clase de Acceso a Datos (DAO) para el registro contable de cobros de transacciones de reservas.
 */
public class PagoDAO extends Pago {

    // =========================================================================
    // SENTENCIAS SQL
    // =========================================================================
    private static final String INSERT =
            "INSERT INTO pago (metodo, fecha_pago, monto, estado, id_reserva) VALUES (?, ?, ?, ?, ?);";

    private static final String DELETE =
            "DELETE FROM pago WHERE id_pago = ?;";

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================
    public PagoDAO() {
        super();
    }

    /** Constructor por copia: Sincroniza la información del modelo con la porción Active Record */
    public PagoDAO(Pago p) {
        super();
        this.setId(p.getId());
        this.setMetodo(p.getMetodo());
        this.setFechaPago(p.getFechaPago());
        this.setMonto(p.getMonto());
        this.setEstado(p.getEstado());
        this.setReserva(p.getReserva());
    }

    // =========================================================================
    // MÉTODOS CRUD DE INSTANCIA (Active Record)
    // =========================================================================

    /** Registra la factura de pago enlazándola con el identificador de su reserva asociada */
    public boolean insert() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.getMetodo().name());
            ps.setTimestamp(2, Timestamp.valueOf(this.getFechaPago())); // Conversión de LocalDateTime a Timestamp de MySQL
            ps.setDouble(3, this.getMonto());
            ps.setString(4, this.getEstado().name());
            ps.setInt(5, this.getReserva().getIdReserva());

            int rows = ps.executeUpdate();
            if (rows > 0) {
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

    /** Elimina el registro contable desde la instancia actual */
    public boolean remove() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, this.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA ESTÁTICOS
    // =========================================================================

    /** Elimina un recibo de pago de forma estática */
    public static boolean delete(int idPago) throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;
        }
    }
}