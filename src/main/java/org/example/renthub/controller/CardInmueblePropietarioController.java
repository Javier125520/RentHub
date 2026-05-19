package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.renthub.DAO.InmuebleDAO;
import org.example.renthub.model.ImagenInmueble;
import org.example.renthub.model.Inmueble;
import org.example.renthub.utils.Ventanas;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la tarjeta de inmueble dentro del panel de control del Propietario.
 * Ofrece controles directos para editar la vivienda o destruirla de la base de datos de forma reactiva.
 */
public class CardInmueblePropietarioController {

    // =========================================================================
    // COMPONENTES VISUALES FXML
    // =========================================================================
    @FXML private ImageView imagen;
    @FXML private Label lblTitulo;
    @FXML private Label lblDireccion;
    @FXML private Label blbCiudad;
    @FXML private Label lblPrecio;
    @FXML private Label lblTipo;
    @FXML private Label lblDescripcion;
    @FXML private Label habitaciones;
    @FXML private Label huespedes;

    // =========================================================================
    // ATRIBUTOS DE LÓGICA Y RETROALIMENTACIÓN
    // =========================================================================
    private Inmueble inmueble;
    private MisViviendasViewController parentController; // Almacena el puntero de la vista padre para refrescar
    private final InmuebleDAO inmuebleDAO = new InmuebleDAO();
    private List<ImagenInmueble> imagenes;
    private int indiceImagen = 0;

    /**
     * Inicializa los datos físicos y el set de imágenes de la propiedad del arrendador.
     */
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
        this.imagenes = inmueble.getImagenes();
        cargarDatos();
        cargarImagenInicial();
    }

    /**
     * Acopla el controlador del listado global para poder invocar refrescos reactivos automáticos.
     */
    public void setParentController(MisViviendasViewController controller) {
        this.parentController = controller;
    }

    /**
     * Enlaza la información textual, enums y variables numéricas en el empaquetado gráfico.
     */
    private void cargarDatos() {
        lblTitulo.setText(inmueble.getTitulo());
        lblDireccion.setText(inmueble.getDireccion());
        blbCiudad.setText(inmueble.getCiudad());
        lblPrecio.setText("€" + inmueble.calcularPrecioFinalPorNoche() + "/noche");
        lblTipo.setText(inmueble.getTipoInmueble().toString());
        lblDescripcion.setText(inmueble.getDescripcion());
        habitaciones.setText("🛏 " + inmueble.getNumeroHabitaciones());
        huespedes.setText("👥 " + inmueble.getCapacidad());

        imagenes = inmueble.getImagenes();

        if (imagenes != null && !imagenes.isEmpty()) {
            indiceImagen = 0;
            mostrarImagen();
        }
    }

    /** Validar y arrancar el carrusel del casero */
    private void cargarImagenInicial() {
        if (imagenes == null || imagenes.isEmpty()) {
            imagen.setImage(null);
            return;
        }
        mostrarImagen();
    }

    /** Mapea el flujo binario del archivo de imagen local y lo inyecta en la escena */
    private void mostrarImagen() {
        if (imagenes == null || imagenes.isEmpty()) {
            imagen.setImage(null);
            return;
        }

        ImagenInmueble img = imagenes.get(indiceImagen);
        File file = new File(img.getUrl());

        if (file.exists()) {
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
            System.out.println("Imagen NO encontrada: " + img.getUrl());
            imagen.setImage(null);
        }
    }

    /** Pasa a la foto anterior */
    @FXML
    public void imagenAnterior(ActionEvent event) {
        if (imagenes == null || imagenes.isEmpty()) return;

        indiceImagen--;
        if (indiceImagen < 0) {
            indiceImagen = imagenes.size() - 1;
        }
        mostrarImagen();
    }

    /** Pasa a la foto siguiente */
    @FXML
    public void imagenSiguiente(ActionEvent event) {
        if (imagenes == null || imagenes.isEmpty()) return;

        indiceImagen++;
        if (indiceImagen >= imagenes.size()) {
            indiceImagen = 0;
        }
        mostrarImagen();
    }

    /**
     * Abre de forma dinámica el modal de edición rellenando los componentes con los datos actuales.
     */
    @FXML
    private void onEditar() throws SQLException {
        Ventanas.abrirModalConDatos(
                "/org/example/renthub/FormInmueble.fxml",
                "Editar Vivienda",
                inmueble
        );
        // Forzar al panel principal del propietario a recargar la rejilla de inmuebles
        parentController.refrescar();
    }

    /**
     * Borra físicamente la propiedad actual de la base de datos a través de su DAO activo.
     */
    @FXML
    private void onEliminar() throws SQLException {
        inmuebleDAO.delete(inmueble.getIdInmueble());
        parentController.refrescar(); // Actualización reactiva instantánea en la interfaz
    }
}
