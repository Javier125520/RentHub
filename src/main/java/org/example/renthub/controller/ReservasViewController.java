package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Usuario;
import org.example.renthub.model.enums.EstadoReserva;
import org.example.renthub.services.Sesion;
import java.util.List;

/**
 * Controlador encargado de maquetar el panel integral de reservas recibidas por un Propietario[cite: 218].
 * Carga las solicitudes entrantes y realiza sumatorios financieros en tiempo de ejecución para desglosar la caja de ingresos[cite: 218].
 */
public class ReservasViewController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private VBox reservasContainer; // Contenedor apilador vertical de las tarjetas de reservas [cite: 218]
    @FXML private Label lblTotalIngresos;  // Desglose del capital neto percibido [cite: 219]
    @FXML private Label lblTotalReservas;  // Conteo informativo de contratos aprobados y abonados [cite: 219]

    private final ReservaDAO reservaDAO = new ReservaDAO();

    /** Inicialización por defecto de JavaFX [cite: 218] */
    @FXML
    public void initialize() {
        cargarReservas();
    }

    /**
     * Recupera el histórico transaccional de alquileres asociados al arrendador logueado en la sesión,
     * procesa el balance contable financiero e hidrata la vista gráfica
     */
    private void cargarReservas() {
        // Saneamiento y vaciado del contenedor antes de operaciones de refresco
        reservasContainer.getChildren().clear();

        try {
            // Recuperación de la identidad activa de la sesión global del Singleton
            Usuario propietario = Sesion.getUsuario();

            // Consulta al pool Active Record mapeando las reservas entrantes del casero
            List<ReservaDAO> reservas = ReservaDAO.findByPropietario(propietario.getIdUsuario());

            // Variables acumuladoras de contabilidad analítica interna
            double totalIngresos = 0;
            int totalConfirmadas = 0;

            // Bucle iterativo de empaquetado gráfico e inyección
            for (Reserva r : reservas) {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/ReservaCardPropietario.fxml")
                );
                VBox card = loader.load();

                // Traspaso seguro de datos hacia el sub-controlador de la tarjeta
                CardReservaPropietarioController controller = loader.getController();
                controller.setReserva(r);

                reservasContainer.getChildren().add(card);

                // REGLA DE NEGOCIO FINANCIERA: Únicamente sumamos capital al balance neto si el estado está CONFIRMADA
                if (r.getEstado() == EstadoReserva.CONFIRMADA) {
                    totalIngresos += r.getPrecioTotal();
                    totalConfirmadas++;
                }
            }

            // Actualización de los marcadores del balance económico en el resumen inferior del panel [cite: 219]
            lblTotalIngresos.setText(String.format("€ %.2f", totalIngresos));
            lblTotalReservas.setText("Total de reservas confirmadas: " + totalConfirmadas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}