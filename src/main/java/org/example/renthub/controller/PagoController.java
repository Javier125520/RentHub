package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.renthub.DAO.PagoDAO;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.model.Pago;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.enums.EstadoPago;
import org.example.renthub.model.enums.EstadoReserva;
import org.example.renthub.model.enums.MetodoPago;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PagoController {

    @FXML private Label lblFechas;
    @FXML private Label lblInmueble;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;
    @FXML private ToggleGroup metodoPagoGroup;

    private Reserva reserva;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private boolean pagoRealizado = false;
    public boolean isPagoRealizado() {
        return pagoRealizado;
    }


    // =========================
    // RECIBIR RESERVA
    // =========================
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        cargarDatos();
    }

    private void cargarDatos() {

        lblFechas.setText(
                reserva.getFechaEntrada().format(formatter)
                        + " - " +
                        reserva.getFechaSalida().format(formatter)
        );

        lblInmueble.setText(
                reserva.getInmueble().getTitulo()
        );

        double subtotal = reserva.getPrecioTotal();
        double iva = subtotal * 0.21;
        double total = subtotal + iva;

        lblSubtotal.setText(String.format("€%.2f", subtotal));
        lblIva.setText(String.format("€%.2f", iva));
        lblTotal.setText(String.format("€%.2f", total));
    }

    // =========================
    // CONFIRMAR PAGO
    // =========================
    @FXML
    private void onConfirmarPago() {

        try {
            Toggle selectedToggle = metodoPagoGroup.getSelectedToggle();

            if (selectedToggle == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Selecciona un método de pago");
                alert.showAndWait();
                return;
            }

            String metodoSeleccionado = selectedToggle.getUserData().toString();
            MetodoPago metodo = MetodoPago.valueOf(metodoSeleccionado);

            // 1️⃣ Crear objeto pago
            Pago pago = new Pago();
            pago.setMetodo(MetodoPago.valueOf(metodoSeleccionado));
            pago.setFechaPago(LocalDateTime.now());
            pago.setMonto(reserva.getPrecioTotal() + reserva.getPrecioTotal()*0.21);
            pago.setEstado(EstadoPago.valueOf("COMPLETADO"));
            pago.setReserva(reserva);


            PagoDAO pagoDAO = new PagoDAO();
            boolean idPagoGenerado = pagoDAO.insert(pago);

            if (!idPagoGenerado) {
                throw new SQLException("No se pudo generar el pago");
            }

            // 2️⃣ Actualizar reserva con ese pago
            ReservaDAO reservaDAO = new ReservaDAO();
            reservaDAO.confirmarReserva(reserva.getIdReserva());

            reserva.setEstado(EstadoReserva.CONFIRMADA);
            pagoRealizado = true;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Pago realizado correctamente");
            alert.showAndWait();

            cerrar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // =========================
    // CANCELAR
    // =========================
    @FXML
    private void onCancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) lblTotal.getScene().getWindow();
        stage.close();
    }
}
