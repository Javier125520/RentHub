package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.DAO.ImagenInmuebleDAO;
import org.example.renthub.model.*;

import java.util.List;

public class MisReservasController {

    private Usuario usuarioActual;

    @FXML private VBox contenedorReservas;
    @FXML private Label lblMensaje;

    // ==========================
    // Recibir usuario
    // ==========================

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarReservas();
    }

    // ==========================
    // Cargar Reservas
    // ==========================

    private void cargarReservas() {
        contenedorReservas.getChildren().clear();
        lblMensaje.setText("");

        List<Reserva> reservas = ReservaDAO.getByUsuario(usuarioActual.getId());

        if (reservas.isEmpty()) {
            lblMensaje.setText("No tienes reservas aún.");
            return;
        }

        for (Reserva r : reservas) {
            contenedorReservas.getChildren().add(crearTarjeta(r));
        }
    }

    // ==========================
    // Crear tarjeta individual
    // ==========================

    private HBox crearTarjeta(Reserva reserva) {
        HBox tarjeta = new HBox(20);
        tarjeta.setStyle("-fx-background-color: #1f2937; -fx-padding: 15; -fx-background-radius: 15;");

        // IMAGEN DEL INMUEBLE
        ImageView imagen = new ImageView();
        imagen.setFitWidth(160);
        imagen.setFitHeight(120);
        imagen.setPreserveRatio(true);

        List<ImagenInmueble> imagenes = ImagenInmuebleDAO.getByInmuebleId(reserva.getInmueble().getId());
        if (!imagenes.isEmpty()) {
            try {
                imagen.setImage(new Image(imagenes.get(0).getUrl()));
            } catch (Exception ignored) {}
        }

        // INFORMACIÓN
        VBox info = new VBox(6);

        Label titulo = new Label(reserva.getInmueble().getTitulo());
        titulo.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        Label fechas = new Label(
                "Del " + reserva.getFechaEntrada() + " al " + reserva.getFechaSalida()
        );
        fechas.setStyle("-fx-text-fill: #cbd5e0;");

        Label precio = new Label("Total: " + reserva.getTotal() + " €");
        precio.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 16px;");

        Label estado = new Label("Estado: " + reserva.getEstado().name());
        estado.setStyle("-fx-text-fill: #a5b4fc;");

        // BOTÓN DETALLES
        Label boton = new Label("Ver detalles →");
        boton.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 14px; -fx-underline: true;");
        boton.setOnMouseClicked(e -> abrirDetallesReserva(reserva, e));

        info.getChildren().addAll(titulo, fechas, precio, estado, boton);

        tarjeta.getChildren().addAll(imagen, info);
        return tarjeta;
    }

    // ==========================
    // Abrir DetallesReserva
    // ==========================

    private void abrirDetallesReserva(Reserva reserva, javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/DetallesReserva.fxml")
            );
            Parent root = loader.load();

            Object controller = loader.getController();
            controller.getClass()
                    .getMethod("setData", Usuario.class, Reserva.class)
                    .invoke(controller, usuarioActual, reserva);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ==========================
    // VOLVER
    // ==========================

    @FXML
    private void volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/MenuHuesped.fxml")
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

