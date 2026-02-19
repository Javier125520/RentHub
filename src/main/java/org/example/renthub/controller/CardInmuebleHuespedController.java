package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.renthub.model.ImagenInmueble;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.InmuebleServicio;
import org.example.renthub.model.ServicioExtra;
import org.example.renthub.utils.Ventanas;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardInmuebleHuespedController {

    // =========================
    // FXML
    // =========================
    @FXML private ImageView imagen;

    @FXML private Label titulo;
    @FXML private Label precio;
    @FXML private Label ciudad;
    @FXML private Label direccion;
    @FXML private Label descripcion;
    @FXML private Label habitaciones;
    @FXML private Label capacidad;

    @FXML private FlowPane contenedorServicios;

    // =========================
    // DATOS
    // =========================
    private Inmueble inmueble;

    private List<ImagenInmueble> imagenes = new ArrayList<>();
    private int indiceImagen = 0;

    // =========================
    // SET INMUEBLE
    // =========================
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
        this.imagenes = inmueble.getImagenes();

        cargarDatos();
        cargarImagenes();
        cargarServicios();
    }

    // =========================
    // CARGA DE DATOS
    // =========================
    private void cargarDatos() {
        titulo.setText(inmueble.getTitulo());
        precio.setText(String.format("%.2f € / noche", inmueble.calcularPrecioFinalPorNoche()));
        ciudad.setText(inmueble.getCiudad());
        direccion.setText(inmueble.getDireccion());
        descripcion.setText(inmueble.getDescripcion());

        habitaciones.setText("🛏 " + inmueble.getNumeroHabitaciones() + " hab.");
        capacidad.setText("👥 " + inmueble.getCapacidad() + " huéspedes");
    }

    // =========================
    // IMÁGENES (CARRUSEL)
    // =========================
    private void cargarImagenes() {
        if (imagenes == null || imagenes.isEmpty()) {
            imagen.setImage(null);
            return;
        }
        mostrarImagenActual();
    }


    private void mostrarImagenActual() {
        if (imagenes == null || imagenes.isEmpty()) {
            imagen.setImage(null);
            return;
        }

        ImagenInmueble img = imagenes.get(indiceImagen);
        File file = new File(img.getUrl());

        if (file.exists()) {
            Image image = new Image(
                    file.toURI().toString(),
                    300,    // ancho
                    180,    // alto
                    false,  // preserveRatio
                    true,   // smooth
                    false   // ❌ backgroundLoading DESACTIVADO
            );

            imagen.setImage(image);
        } else {
            System.out.println("Imagen NO encontrada: " + img.getUrl());
            imagen.setImage(null);
        }
    }

    @FXML
    private void imagenAnterior() {
        if (imagenes.isEmpty()) return;

        indiceImagen = (indiceImagen - 1 + imagenes.size()) % imagenes.size();
        mostrarImagenActual();
    }

    @FXML
    private void imagenSiguiente() {
        if (imagenes.isEmpty()) return;

        indiceImagen = (indiceImagen + 1) % imagenes.size();
        mostrarImagenActual();
    }

    // =========================
    // SERVICIOS
    // =========================
    private void cargarServicios() {
        contenedorServicios.getChildren().clear();

        if (inmueble.getServicios() == null) return;

        for (InmuebleServicio s : inmueble.getServicios()) {
            Label chip = new Label("✔ " + s.getServicio().getNombre());
            chip.getStyleClass().add("service-chip");
            contenedorServicios.getChildren().add(chip);
        }
    }

    // =========================
    // RESERVAR
    // =========================
    @FXML
    private void onReservar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormReserva.fxml")
            );

            VBox root = loader.load();

            FormReservaController controller = loader.getController();
            controller.setInmueble(inmueble); // 🔑 AQUÍ ESTÁ LA CLAVE

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Reservar inmueble");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


