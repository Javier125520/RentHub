package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.renthub.model.Reserva;

public class ReservaCardController {

    @FXML private Label lblInmueble;
    @FXML private Label lblHuesped;
    @FXML private Label lblFechas;
    @FXML private Label lblEstado;
    @FXML private Button btnDetalles;

    private Reserva reserva;

    public void setDatos(Reserva reserva) {
        this.reserva = reserva;

        lblInmueble.setText(reserva.getInmueble().getTitulo());
        lblHuesped.setText("Huésped: " + reserva.getHuesped().getNombre());
        lblFechas.setText(reserva.getFechaEntrada() + " → " + reserva.getFechaSalida());
        lblEstado.setText(reserva.getEstado().toString());
    }

    @FXML
    private void verDetalles() {
        // TODO: Abrir pantalla DetallesReserva.fxml
    }
}