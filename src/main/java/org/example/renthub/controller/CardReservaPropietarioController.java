package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.enums.EstadoReserva;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controlador informativo para la tarjeta de reservas recibidas en el panel del Propietario.
 * Despliega de forma analítica los datos del huésped, fechas de check-in/out e ingresos netos de caja.
 */
public class CardReservaPropietarioController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private VBox root;
    @FXML private Label lblVivienda;
    @FXML private Label lblUbicacion;
    @FXML private Label lblEstado;
    @FXML private Label lblHuesped;
    @FXML private Label lblEmail;
    @FXML private Label lblCheckIn;
    @FXML private Label lblCheckOut;
    @FXML private Label lblIngreso;
    @FXML private Label lblFechaReserva;

    // Formateador reducido de fechas (ej: 15 ene 2026) localizado explícitamente en español
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("es", "ES"));

    /**
     * Setea y desglosa las propiedades del modelo de la reserva sobre las etiquetas fijas de la tarjeta.
     * @param reserva La reserva adjudicada.
     */
    public void setReserva(Reserva reserva) {
        // 🏠 Información del Inmueble
        lblVivienda.setText(reserva.getInmueble().getTitulo());
        lblUbicacion.setText(reserva.getInmueble().getCiudad());

        // 📌 Gestión de Estados y Estilos
        lblEstado.setText(reserva.getEstado().name());
        aplicarEstiloEstado(reserva.getEstado());

        // 👤 Metadata de contacto del cliente
        lblHuesped.setText(reserva.getHuesped().getNombre());
        lblEmail.setText(reserva.getHuesped().getCorreo());

        // 📅 Intervalo de Hospedaje
        lblCheckIn.setText(reserva.getFechaEntrada().format(formatter));
        lblCheckOut.setText(reserva.getFechaSalida().format(formatter));

        // 💰 Liquidación económica
        lblIngreso.setText(String.format("€ %.2f", reserva.getPrecioTotal()));

        // 📆 Estampa cronológica de creación de la reserva en el sistema
        if (reserva.getFechaRegistro() != null) {
            lblFechaReserva.setText(
                    "Reserva creada el " + reserva.getFechaRegistro().toLocalDateTime().format(formatter)
            );
        }
    }

    /**
     * Mantenimiento de clases de estiloCSS de JavaFX.
     * Purga los selectores previos e inyecta el color de la cápsula según el estado actual del Enum.
     */
    private void aplicarEstiloEstado(EstadoReserva estado) {
        // Limpieza de seguridad de estados volátiles para evitar acumulación de clases
        lblEstado.getStyleClass().removeAll("confirmed", "pending", "cancelled");

        switch (estado) {
            case CONFIRMADA -> lblEstado.getStyleClass().add("confirmed");
            case PENDIENTE -> lblEstado.getStyleClass().add("pending");
            case CANCELADA -> lblEstado.getStyleClass().add("cancelled");
        }
    }
}