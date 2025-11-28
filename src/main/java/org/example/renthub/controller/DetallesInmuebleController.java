package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.example.renthub.DAO.ImagenInmuebleDAO;
import org.example.renthub.DAO.InmuebleServicioDAO;
import org.example.renthub.model.*;

import java.util.List;

public class DetallesInmuebleController {

    private Usuario usuarioActual;
    private Inmueble inmuebleActual;

    // ======================
    // ELEMENTOS FXML
    // ======================

    @FXML private ImageView imgPrincipal;
    @FXML private Label lblTitulo;
    @FXML private Label lblUbicacion;
    @FXML private Label lblPrecio;
    @FXML private Label lblDescripcion;

    @FXML private HBox galeria; // imágenes pequeñas
    @FXML private FlowPane listaServicios; // lista de servicios

    // ======================
    // MÉTODO PARA RECIBIR DATOS
    // ======================

    public void setData(Usuario usuario, Inmueble inmueble) {
        this.usuarioActual = usuario;
        this.inmuebleActual = inmueble;

        cargarDatosBasicos();
        cargarImagenes();
        cargarServicios();
    }

    // ======================
    // CARGA DE DATOS
    // ======================

    private void cargarDatosBasicos() {
        lblTitulo.setText(inmuebleActual.getTitulo());
        lblUbicacion.setText(inmuebleActual.getCiudad());
        lblPrecio.setText(inmuebleActual.getPrecioNoche() + " €/noche");
        lblDescripcion.setText(inmuebleActual.getDescripcion());
    }

    private void cargarImagenes() {
        galeria.getChildren().clear();

        List<ImagenInmueble> imagenes =
                ImagenInmuebleDAO.getByInmuebleId(inmuebleActual.getId());

        if (!imagenes.isEmpty()) {
            // Imagen principal
            imgPrincipal.setImage(new Image(imagenes.get(0).getUrl()));

            // Galería
            for (ImagenInmueble img : imagenes) {
                ImageView mini = new ImageView(new Image(img.getUrl()));
                mini.setFitWidth(150);
                mini.setFitHeight(100);
                mini.setPreserveRatio(true);

                galeria.getChildren().add(mini);

                // Al hacer clic cambiar imagen principal
                mini.setOnMouseClicked(e -> imgPrincipal.setImage(new Image(img.getUrl())));
            }
        }
    }

    private void cargarServicios() {
        listaServicios.getChildren().clear();

        List<InmuebleServicio> servicios =
                InmuebleServicioDAO.getByInmueble(inmuebleActual.getId());

        for (InmuebleServicio is : servicios) {
            Label etiqueta = new Label("• " + is.getServicio().getNombre());
            etiqueta.setStyle("-fx-font-size: 15px; -fx-text-fill: #cbd5e0;");
            listaServicios.getChildren().add(etiqueta);
        }
    }

    // ======================
    // BOTÓN: VOLVER
    // ======================

    @FXML
    private void volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/BuscarInmuebles.fxml")
            );
            Parent root = loader.load();

            // Pasar usuario al controlador anterior
            Object controller = loader.getController();
            controller.getClass()
                    .getMethod("setUsuario", Usuario.class)
                    .invoke(controller, usuarioActual);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ======================
    // BOTÓN: RESERVAR
    // ======================

    @FXML
    private void reservar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/ReservarInmueble.fxml")
            );
            Parent root = loader.load();

            // Pasar usuario + inmueble
            Object controller = loader.getController();
            controller.getClass()
                    .getMethod("setData", Usuario.class, Inmueble.class)
                    .invoke(controller, usuarioActual, inmuebleActual);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

