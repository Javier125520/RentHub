package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.example.renthub.DAO.ImagenInmuebleDAO;
import org.example.renthub.model.*;

import java.util.List;

public class DetallesReservaController {

    private Usuario usuarioActual;
    private Reserva reservaActual;

    // ==========================
    // ELEMENTOS FXML
    // ==========================

    @FXML private ImageView imgInmueble;

    @FXML private Label lblTitulo;
    @FXML private Label lblUbicacion;

    @FXML private Label lblFecha;
    @FXML private Label lblPrecioNoche;
    @FXML private Label lblTotal;
    @FXML private Label lblEstado;


    // ==========================
    // RECIBIR DATOS
    // ==========================

    public void setData(Usuario usuario, Reserva reserva) {
        this.usuarioActual = usuario;
        this.reservaActual = reserva;

        cargarDatos();
    }

    // ==========================
    // CARGAR DATOS EN LA UI
    // ==========================

    private void cargarDatos() {

        Inmueble inm = reservaActual.getInmueble();

        lblTitulo.setText(inm.getTitulo());
        lblUbicacion.setText(inm.getCiudad());

        lblFecha.setText(
                "Del " + reservaActual.getFechaEntrada() +
                        " al " + reservaActual.getFechaSalida()
        );

        lblPrecioNoche.setText(
                String.format("%.2f €/noche", inm.getPrecioNoche())
        );

        lblTotal.setText(
                String.format("Total: %.2f €", reservaActual.getTotal())
        );

        lblEstado.setText("Estado: " + reservaActual.getEstado().name());

        cargarImagen();
    }

    private void cargarImagen() {
        try {
            List<ImagenInmueble> imagenes =
                    ImagenInmuebleDAO.getByInmuebleId(reservaActual.getInmueble().getId());

            if (!imagenes.isEmpty()) {
                imgInmueble.setImage(new Image(imagenes.get(0).getUrl()));
            }

        } catch (Exception ignored) {}
    }

    // ==========================
    // BOTÓN VOLVER
    // ==========================

    @FXML
    private void volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/MisReservas.fxml")
            );
            Parent root = loader.load();

            Object controller = loader.getController();
            controller.getClass()
                    .getMethod("setUsuario", Usuario.class)
                    .invoke(controller, usuarioActual);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

