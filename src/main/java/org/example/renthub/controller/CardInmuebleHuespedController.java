package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la tarjeta visual de un inmueble en la vista del Huésped.
 * Gestiona el carrusel de imágenes local, la muestra de servicios y la apertura del formulario de reserva.
 */
public class CardInmuebleHuespedController {

    // =========================================================================
    // COMPONENTES VISUALES ENLAZADOS AL FXML
    // =========================================================================
    @FXML private ImageView imagen;
    @FXML private Label titulo;
    @FXML private Label precio;
    @FXML private Label ciudad;
    @FXML private Label direccion;
    @FXML private Label descripcion;
    @FXML private Label habitaciones;
    @FXML private Label capacidad;
    @FXML private FlowPane contenedorServicios;

    // =========================================================================
    // ATRIBUTOS DE LÓGICA E INTERNOCONEXIÓN
    // =========================================================================
    private Inmueble inmueble;
    private List<ImagenInmueble> imagenes = new ArrayList<>();
    private int indiceImagen = 0;

    /**
     * Enlaza el modelo del inmueble a la tarjeta e inicia la carga de componentes visuales.
     * @param inmueble Instancia del alojamiento seleccionado.
     */
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
        this.imagenes = inmueble.getImagenes();

        cargarDatos();
        cargarImagenes();
        cargarServicios();
    }

    /**
     * Setea los textos e información básica del alojamiento en las etiquetas correspondientes.
     */
    private void cargarDatos() {
        titulo.setText(inmueble.getTitulo());
        precio.setText(String.format("%.2f € / noche", inmueble.calcularPrecioFinalPorNoche()));
        ciudad.setText(inmueble.getCiudad());
        direccion.setText(inmueble.getDireccion());
        descripcion.setText(inmueble.getDescripcion());

        habitaciones.setText("🛏 " + inmueble.getNumeroHabitaciones() + " hab.");
        capacidad.setText("👥 " + inmueble.getCapacidad() + " huéspedes");
    }

    /**
     * Valida e inicializa la carga del carrusel gráfico de fotografías de la vivienda.
     */
    private void cargarImagenes() {
        if (imagenes == null || imagenes.isEmpty()) {
            imagen.setImage(null);
            return;
        }
        mostrarImagenActual();
    }

    /**
     * Procesa la ruta local de la imagen indexada en el índice actual y la renderiza en el ImageView.
     */
    private void mostrarImagenActual() {
        if (imagenes == null || imagenes.isEmpty()) {
            imagen.setImage(null);
            return;
        }

        ImagenInmueble img = imagenes.get(indiceImagen);
        File file = new File(img.getUrl());

        if (file.exists()) {
            // Conversión segura de ruta de disco a URI compatible con el motor gráfico de JavaFX
            Image image = new Image(
                    file.toURI().toString(),
                    300,
                    180,
                    false,
                    true,
                    false
            );
            imagen.setImage(image);
        } else {
            System.out.println("Imagen NO encontrada en el sistema: " + img.getUrl());
            imagen.setImage(null);
        }
    }

    /**
     * Retrocede una fotografía en el carrusel circular.
     */
    @FXML
    private void imagenAnterior() {
        if (imagenes.isEmpty()) return;

        indiceImagen = (indiceImagen - 1 + imagenes.size()) % imagenes.size();
        mostrarImagenActual();
    }

    /**
     * Avanza una fotografía en el carrusel circular.
     */
    @FXML
    private void imagenSiguiente() {
        if (imagenes.isEmpty()) return;

        indiceImagen = (indiceImagen + 1) % imagenes.size();
        mostrarImagenActual();
    }

    /**
     * Genera dinámicamente etiquetas con formato de pastilla (chips) para los servicios del inmueble.
     */
    private void cargarServicios() {
        contenedorServicios.getChildren().clear();

        if (inmueble.getServicios() == null) return;

        for (InmuebleServicio s : inmueble.getServicios()) {
            Label chip = new Label("✔ " + s.getServicio().getNombre());
            chip.getStyleClass().add("service-chip"); // Estilo definido en tu css
            contenedorServicios.getChildren().add(chip);
        }
    }

    /**
     * Evento al hacer clic en 'Reservar'. Carga el formulario modal inyectándole los datos del inmueble.
     */
    @FXML
    private void onReservar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormReserva.fxml")
            );
            VBox root = loader.load();

            // Transferencia de datos segura hacia el controlador modal
            FormReservaController controller = loader.getController();
            controller.setInmueble(inmueble);

            // Configuración del escenario emergente bloqueante
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

