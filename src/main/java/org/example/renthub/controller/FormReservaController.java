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
        lblPrecio.setText(String.format("%.2f € / noche", inmueble.getPrecioNoche()));
    }

    public void setReserva (Reserva reserva) {
        this.inmueble = reserva.getInmueble();

        lblTitulo.setText("Modificar reserva de " + inmueble.getTitulo());
        lblPrecio.setText(String.format("%.2f € / noche", inmueble.getPrecioNoche()));

        fechaInicio.setValue(reserva.getFechaEntrada());
        fechaFin.setValue(reserva.getFechaSalida());
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
        double total = noches * inmueble.getPrecioNoche();

        lblTotal.setText(String.format("Total: %.2f €", total));
        lblDetalle.setText(noches + " noches × " + inmueble.getPrecioNoche() + " €");

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
            Usuario huesped = Sesion.getUsuario();

            long noches = ChronoUnit.DAYS.between(inicio, fin);
            double total = noches * inmueble.getPrecioNoche();

            Reserva reserva = new Reserva();
            reserva.setInmueble(inmueble);
            reserva.setHuesped(huesped);
            reserva.setFechaEntrada(inicio);
            reserva.setFechaSalida(fin);
            reserva.setPrecioTotal(total);
            reserva.setEstado(EstadoReserva.PENDIENTE);

            reservaDAO.insert(reserva);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reserva confirmada");
            alert.setHeaderText("¡Reserva realizada con éxito!");
            alert.setContentText("Tu reserva se ha guardado correctamente.");
            alert.showAndWait();


            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo realizar la reserva");
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
