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

/**
 * Controlador para la ventana de Pasarela de Pagos y Liquidación Contable de Alquileres.
 * Desglosa las tarifas netas de alojamiento, calcula el IVA de ley y emite facturaciones verificadas en la BD.
 */
public class PagoController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private Label lblFechas;
    @FXML private Label lblInmueble;
    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;
    @FXML private ToggleGroup metodoPagoGroup; // Grupo de exclusión mutua de los botones de tarjetas de pago

    private Reserva reserva;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy"); // Formato de fecha compacto (ej: 15 ene 2026)

    private boolean pagoRealizado = false; // Bandera de estado para retroalimentación con la tarjeta padre

    /** Expone el resultado del cobro para su consulta desde el exterior */
    public boolean isPagoRealizado() {
        return pagoRealizado;
    }

    /**
     * Enlaza la reserva a procesar e inicia la carga y cálculo del desglose de precios.
     * @param reserva La reserva seleccionada para liquidar.
     */
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        cargarDatos();
    }

    /**
     * Algoritmo de desglose e IVA. Setea los textos informativos y realiza
     * los cálculos fiscales en base a la estipulación del subtotal del alquiler.
     */
    private void cargarDatos() {
        lblFechas.setText(
                reserva.getFechaEntrada().format(formatter) + " - " + reserva.getFechaSalida().format(formatter)
        );

        lblInmueble.setText(reserva.getInmueble().getTitulo());

        // LÓGICA DE NEGOCIO FISCAL CONTABLE
        double subtotal = reserva.getPrecioTotal();
        double iva = subtotal * 0.21; // Cálculo impositivo del 21% de IVA
        double total = subtotal + iva;

        // Formateo estricto de dos decimales para visualización limpia
        lblSubtotal.setText(String.format("€%.2f", subtotal));
        lblIva.setText(String.format("€%.2f", iva));
        lblTotal.setText(String.format("€%.2f", total));
    }

    /**
     * Procesa la transacción bancaria simulada, valida la opción de cobro seleccionada
     * y actualiza de manera transaccional multi-tabla tanto el Pago como la Reserva en MySQL.
     */
    @FXML
    private void onConfirmarPago() {
        try {
            Toggle selectedToggle = metodoPagoGroup.getSelectedToggle();

            // Validación de seguridad UX perimetral
            if (selectedToggle == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Selecciona un método de pago");
                alert.showAndWait();
                return;
            }

            // Recuperamos la clave string del Enum guardada en el UserData de la opción elegida
            String metodoSeleccionado = selectedToggle.getUserData().toString();

            // Inserción del registro contable de cobros
            PagoDAO pagoDAO = new PagoDAO();
            pagoDAO.setMetodo(MetodoPago.valueOf(metodoSeleccionado));
            pagoDAO.setFechaPago(LocalDateTime.now());
            pagoDAO.setMonto(reserva.getPrecioTotal() + (reserva.getPrecioTotal() * 0.21)); // Importe total bruto con IVA
            pagoDAO.setEstado(EstadoPago.COMPLETADO);
            pagoDAO.setReserva(this.reserva);

            boolean idPagoGenerado = pagoDAO.insert();

            if (!idPagoGenerado) {
                throw new SQLException("No se pudo generar el pago");
            }

            // Modificamos el estado del alquiler de PENDIENTE a CONFIRMADA
            ReservaDAO reservaDAO = new ReservaDAO(this.reserva);
            reservaDAO.confirmarReserva();

            // Sincronizamos en memoria el estado local antes de cerrar la ventana
            this.reserva.setEstado(EstadoReserva.CONFIRMADA);
            this.pagoRealizado = true;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Pago realizado correctamente");
            alert.showAndWait();

            cerrar();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error al procesar el pago en la base de datos");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private void cerrar() {
        Stage stage = (Stage) lblTotal.getScene().getWindow();
        stage.close();
    }
}