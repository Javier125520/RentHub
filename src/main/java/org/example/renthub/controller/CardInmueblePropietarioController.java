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

public class CardInmueblePropietarioController {

    @FXML
    private ImageView imagen;

    @FXML private Label lblTitulo;
    @FXML private Label lblDireccion;
    @FXML private Label blbCiudad;
    @FXML private Label lblPrecio;
    @FXML private Label lblTipo;
    @FXML private Label lblDescripcion;
    @FXML private Label habitaciones;
    @FXML private Label huespedes;

    private Inmueble inmueble;
    private MisViviendasViewController parentController;

    private final InmuebleDAO inmuebleDAO = new InmuebleDAO();

    /* =========================
       CARRUSEL
       ========================= */

    private List<ImagenInmueble> imagenes;
    private int indiceImagen = 0;

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
        this.imagenes = inmueble.getImagenes();
        cargarDatos();
        cargarImagenInicial();
    }

    public void setParentController(MisViviendasViewController controller) {
        this.parentController = controller;
    }

    private void cargarDatos() {
        lblTitulo.setText(inmueble.getTitulo());
        lblDireccion.setText(inmueble.getDireccion());
        blbCiudad.setText(inmueble.getCiudad());
        lblPrecio.setText("€" + inmueble.getPrecioNoche() + "/noche");
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

    /* =========================
       IMÁGENES
       ========================= */

    private void cargarImagenInicial() {
        if (imagenes == null || imagenes.isEmpty()) {
            imagen.setImage(null);
            return;
        }
        mostrarImagen();
    }

    private void mostrarImagen() {
        if (imagenes == null || imagenes.isEmpty()) {
            imagen.setImage(null);
            return;
        }

        ImagenInmueble img = imagenes.get(indiceImagen);
        File file = new File(img.getUrl());

        System.out.println("Cargando imagen: " + file.getAbsolutePath());

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
    public void imagenAnterior(ActionEvent event) {
        if (imagenes == null || imagenes.isEmpty()) return;

        indiceImagen--;
        if (indiceImagen < 0) {
            indiceImagen = imagenes.size() - 1;
        }
        mostrarImagen();
    }

    @FXML
    public void imagenSiguiente(ActionEvent event) {
        if (imagenes == null || imagenes.isEmpty()) return;

        indiceImagen++;
        if (indiceImagen >= imagenes.size()) {
            indiceImagen = 0;
        }
        mostrarImagen();
    }

    /* =========================
       ACCIONES
       ========================= */

    @FXML
    private void onEditar() throws SQLException {
        Ventanas.abrirModalConDatos(
                "/org/example/renthub/FormInmueble.fxml",
                "Editar Vivienda",
                inmueble
        );
        parentController.refrescar();
    }

    @FXML
    private void onEliminar() throws SQLException {
        inmuebleDAO.delete(inmueble.getIdInmueble());
        parentController.refrescar();
    }
}
