package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.enums.EstadoReserva;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CardReservaPropietarioController {

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

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("es", "ES"));

    public void setReserva(Reserva reserva) {

        // 🏠 Vivienda
        lblVivienda.setText(reserva.getInmueble().getTitulo());
        lblUbicacion.setText(
                reserva.getInmueble().getCiudad()
        );

        // 📌 Estado
        lblEstado.setText(reserva.getEstado().name());
        aplicarEstiloEstado(reserva.getEstado());

        // 👤 Huésped
        lblHuesped.setText(reserva.getHuesped().getNombre());
        lblEmail.setText(reserva.getHuesped().getCorreo());

        // 📅 Fechas
        lblCheckIn.setText(reserva.getFechaEntrada().format(formatter));
        lblCheckOut.setText(reserva.getFechaSalida().format(formatter));

        // 💰 Precio
        lblIngreso.setText(String.format("€ %.2f", reserva.getPrecioTotal()));

        // 📆 Fecha creación (si la tienes)
        if (reserva.getFechaRegistro() != null) {
            lblFechaReserva.setText(
                    "Reserva creada el " +
                            reserva.getFechaRegistro().toLocalDateTime().format(formatter)
            );
        }
    }

    private void aplicarEstiloEstado(EstadoReserva estado) {

        lblEstado.getStyleClass().removeAll("confirmed", "pending", "cancelled");

        switch (estado) {
            case CONFIRMADA -> lblEstado.getStyleClass().add("confirmed");
            case PENDIENTE -> lblEstado.getStyleClass().add("pending");
            case CANCELADA -> lblEstado.getStyleClass().add("cancelled");
        }
    }
}
