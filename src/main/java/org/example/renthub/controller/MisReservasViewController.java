package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;
import java.io.IOException;
import java.util.List;

/**
 * Controlador de subvista para el panel de reservas del Huésped.
 * Presenta el catálogo cronológico de solicitudes de alquiler efectuadas por el cliente.
 */
public class MisReservasViewController {

    // =========================================================================
    // COMPONENTES VISUALES ENLAZADOS AL FXML
    // =========================================================================
    @FXML private VBox contenedorReservas; // Layout vertical destino de los alquileres
    @FXML private Label lblContador;       // Etiqueta informativa del número total de operaciones

    /**
     * Inicialización por defecto del ciclo de vida de JavaFX.
     * Invoca automáticamente la sincronización y renderizado de reservas.
     */
    @FXML
    public void initialize() {
        cargarReservas();
    }

    /**
     * Recupera de la sesión global el usuario logueado, consulta sus movimientos contratados
     * en base de datos y los distribuye en pantalla controlando estados vacíos.
     */
    private void cargarReservas() {
        contenedorReservas.getChildren().clear();

        // Recuperación de la identidad activa del Singleton perimetral de sesión
        Usuario usuario = Sesion.getUsuario();

        if (usuario == null) {
            lblContador.setText("No hay sesión iniciada");
            return;
        }

        try {
            // Consulta SQL transaccional mapeada en el pool Active Record de Reservas
            List<ReservaDAO> reservas = ReservaDAO.findByHuesped(usuario.getIdUsuario());

            // Actualizamos dinámicamente el contador del panel
            lblContador.setText(reservas.size() + " reservas realizadas");

            // Gestión UX de bandeja vacía (si el cliente no tiene viajes contratados)
            if (reservas.isEmpty()) {
                Label vacio = new Label("No tienes reservas todavía");
                vacio.getStyleClass().add("empty-text"); // Estilo definido en tu css
                contenedorReservas.getChildren().add(vacio);
                return;
            }

            // Iteración de elementos para la inyección de componentes gráficos individuales
            for (Reserva r : reservas) {
                cargarCardReserva(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga y procesa de forma asíncrona la plantilla FXML para la tarjeta de reserva inyectando su modelo.
     * @param reserva Instancia de la reserva del bucle iterativo.
     * @throws IOException Si el cargador no localiza el recurso FXML en el paquete.
     */
    private void cargarCardReserva(Reserva reserva) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/renthub/ReservaCardHuesped.fxml")
        );
        VBox card = loader.load();

        // Sincronización del controlador hijo
        CardReservaHuespedController controller = loader.getController();
        controller.setReserva(reserva);

        contenedorReservas.getChildren().add(card);
    }
}
