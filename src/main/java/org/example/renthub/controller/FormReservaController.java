package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Usuario;
import org.example.renthub.model.enums.EstadoReserva;
import org.example.renthub.services.Sesion;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FormReservaController {

    // =========================
    // FXML
    // =========================
    @FXML private Label lblTitulo;
    @FXML private Label lblPrecio;

    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;

    @FXML private VBox totalContainer;
    @FXML private Label lblTotal;
    @FXML private Label lblDetalle;

    // =========================
    // DAO
    // =========================
    private final Connection conn = MySQLConnection.getConnection();
    private final ReservaDAO reservaDAO = new ReservaDAO(conn);

    // =========================
    // DATOS
    // =========================
    private Inmueble inmueble;
    private Reserva reservaEditando = null;
    private boolean modoEdicion = false;

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        // Escuchamos cambios, pero protegemos el cálculo
        fechaInicio.valueProperty().addListener((obs, o, n) -> calcularTotal());
        fechaFin.valueProperty().addListener((obs, o, n) -> calcularTotal());
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;

        lblTitulo.setText("Reservar " + inmueble.getTitulo());
        lblPrecio.setText(String.format("%.2f € / noche", inmueble.calcularPrecioFinalPorNoche()));
    }

    public void setReserva(Reserva reserva) {

        this.reservaEditando = reserva;
        this.modoEdicion = true;

        this.inmueble = reserva.getInmueble();

        lblTitulo.setText("Modificar reserva de " + inmueble.getTitulo());
        lblPrecio.setText(String.format("%.2f € / noche", inmueble.calcularPrecioFinalPorNoche()));

        fechaInicio.setValue(reserva.getFechaEntrada());
        fechaFin.setValue(reserva.getFechaSalida());

        calcularTotal();
    }


    // =========================
    // CALCULAR TOTAL
    // =========================
    private void calcularTotal() {

        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFin.getValue();

        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            totalContainer.setVisible(false);
            totalContainer.setManaged(false);
            return;
        }

        long noches = ChronoUnit.DAYS.between(inicio, fin);
        double total = noches * inmueble.calcularPrecioFinalPorNoche();

        lblTotal.setText(String.format("Total: %.2f €", total));
        lblDetalle.setText(noches + " noches × " + inmueble.calcularPrecioFinalPorNoche() + " €");

        totalContainer.setVisible(true);
        totalContainer.setManaged(true);
    }

    // =========================
    // CONFIRMAR RESERVA
    // =========================
    @FXML
    private void confirmar() {

        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFin.getValue();

        if (inicio == null || fin == null) {
            mostrarError("Debes seleccionar las fechas de reserva");
            return;
        }

        if (!fin.isAfter(inicio)) {
            mostrarError("La fecha de salida debe ser posterior a la de entrada");
            return;
        }

        try {

            long noches = ChronoUnit.DAYS.between(inicio, fin);
            double total = noches * inmueble.getPrecioNoche();

            if (modoEdicion) {

                // 🔥 UPDATE
                reservaEditando.setFechaEntrada(inicio);
                reservaEditando.setFechaSalida(fin);
                reservaEditando.setPrecioTotal(total);

                reservaDAO.update(reservaEditando);

            } else {

                // 🔥 INSERT
                Usuario huesped = Sesion.getUsuario();

                Reserva nueva = new Reserva();
                nueva.setInmueble(inmueble);
                nueva.setHuesped(huesped);
                nueva.setFechaEntrada(inicio);
                nueva.setFechaSalida(fin);
                nueva.setPrecioTotal(total);
                nueva.setEstado(EstadoReserva.PENDIENTE);

                reservaDAO.insert(nueva);
            }

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo guardar la reserva");
        }
    }


    // =========================
    // CERRAR
    // =========================
    @FXML
    private void cerrar() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }

    // =========================
    // ERROR
    // =========================
    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
