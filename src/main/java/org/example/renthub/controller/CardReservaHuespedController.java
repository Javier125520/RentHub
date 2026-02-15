package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.enums.EstadoReserva;

import java.time.format.DateTimeFormatter;

public class CardReservaHuespedController {

    // =========================
    // FXML
    // =========================
    @FXML private Label titulo;
    @FXML private Label ubicacion;
    @FXML private Label estado;

    @FXML private Label entrada;
    @FXML private Label salida;
    @FXML private Label precioTotal;

    @FXML private HBox accionesPago;
    @FXML private HBox accionResena;

    // =========================
    // DATOS
    // =========================
    private Reserva reserva;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMMM yyyy");

    // =========================
    // SET RESERVA
    // =========================
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        cargarDatos();
        configurarEstado();
    }

    // =========================
    // CARGAR DATOS
    // =========================
    private void cargarDatos() {
        titulo.setText(reserva.getInmueble().getTitulo());
        ubicacion.setText(
                reserva.getInmueble().getCiudad() + "\n" +
                        reserva.getInmueble().getDireccion()
        );

        entrada.setText(reserva.getFechaEntrada().format(formatter));
        salida.setText(reserva.getFechaSalida().format(formatter));

        precioTotal.setText(
                String.format("€%.2f", reserva.getPrecioTotal())
        );

        estado.setText(reserva.getEstado().name().replace("_", " "));
        estado.getStyleClass().add("estado-" + reserva.getEstado().name().toLowerCase());
    }

    // =========================
    // CONFIGURAR BOTONES SEGÚN ESTADO
    // =========================
    private void configurarEstado() {

        accionesPago.setVisible(false);
        accionesPago.setManaged(false);

        accionResena.setVisible(false);
        accionResena.setManaged(false);

        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            accionesPago.setVisible(true);
            accionesPago.setManaged(true);
        }

        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            accionResena.setVisible(true);
            accionResena.setManaged(true);
        }
    }

    // =========================
    // ACCIONES
    // =========================
    @FXML
    private void realizarPago() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormPago.fxml")
            );

            Parent root = loader.load();

            PagoController controller = loader.getController();
            controller.setReserva(reserva); // 🔥 PASAMOS LA RESERVA

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isPagoRealizado()) {
                configurarEstado();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void editarReserva() {

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Solo se pueden editar reservas pendientes");
            alert.showAndWait();
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormReserva.fxml")
            );

            Parent root = loader.load();

            FormReservaController controller = loader.getController();
            controller.setReserva(reserva);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarDatos();
            configurarEstado();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void cancelarReserva() {

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Solo se pueden cancelar reservas pendientes");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("¿Seguro que quieres cancelar esta reserva?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        confirm.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {
                try {
                    ReservaDAO reservaDAO = new ReservaDAO();
                    reservaDAO.cancelarReserva(reserva.getIdReserva());

                    // 🔥 Actualizar objeto en memoria
                    reserva.setEstado(EstadoReserva.CANCELADA);

                    // 🔥 Actualizar UI
                    estado.setText("CANCELADA");
                    configurarEstado();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @FXML
    private void añadirResena() {

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormReseña.fxml")
            );

            Parent root = loader.load();

            FormResenaController controller = loader.getController();
            controller.setReserva(reserva);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

