package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Reseña;
import java.time.LocalDate;

/**
 * Controlador para el formulario interactivo de Reseñas / Valoraciones.
 * Gestiona el sistema interactivo de puntuación por estrellas haciendo clic sobre las etiquetas.
 */
public class FormResenaController {

    // =========================================================================
    // ETIQUETAS DE TEXTO PARA LAS 5 ESTRELLAS DEL PANEL
    // =========================================================================
    @FXML private Label star1;
    @FXML private Label star2;
    @FXML private Label star3;
    @FXML private Label star4;
    @FXML private Label star5;

    @FXML private TextArea txtComentario;

    // Atributos de enlace contextual
    private Reserva reserva;
    private int puntuacionSeleccionada = 0; // Almacena la nota numérica final del 1 al 5
    private Reseña resena;
    private boolean modoEdicion = false;

    /**
     * Vincula la reserva contextual para saber a qué inmueble y huésped pertenece la reseña futura.
     */
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    /**
     * Vincula una reseña existente obligando al formulario a pasar a Modo Edición.
     */
    public void setResena(Reseña resena) {
        this.resena = resena;
        this.modoEdicion = true;

        // Carga los datos previos almacenados en la base de datos en los campos
        txtComentario.setText(resena.getComentario());
        puntuacionSeleccionada = resena.getPuntuacion();

        // Pinta las estrellas doradas iniciales correspondientes a la nota previa
        actualizarVisualEstrellas();
    }

    /**
     * Inicialización de JavaFX. Enlaza los escuchadores de clics a las estrellas.
     */
    @FXML
    private void initialize() {
        configurarEstrellas();
    }

    /**
     * Indexa las etiquetas de las estrellas en un array y asocia un evento de clic a cada una.
     */
    private void configurarEstrellas() {
        Label[] estrellas = {star1, star2, star3, star4, star5};

        for (int i = 0; i < estrellas.length; i++) {
            int valor = i + 1; // La nota real corresponde al índice del array + 1

            // Al hacer clic, fijamos la nota global y redibujamos el panel
            estrellas[i].setOnMouseClicked(e -> {
                puntuacionSeleccionada = valor;
                actualizarVisualEstrellas();
            });
        }
    }

    /**
     * Recorre las etiquetas cambiando el estiloCSS en tiempo de ejecución.
     * Ilumina en dorado las estrellas inferiores o iguales a la nota, tiñendo de gris las restantes.
     */
    private void actualizarVisualEstrellas() {
        Label[] estrellas = {star1, star2, star3, star4, star5};

        for (int i = 0; i < estrellas.length; i++) {
            if (i < puntuacionSeleccionada) {
                estrellas[i].setStyle("-fx-text-fill: gold;");
            } else {
                estrellas[i].setStyle("-fx-text-fill: grey;");
            }
        }
    }

    /**
     * Procesa la inserción o actualización de la opinión en la base de datos MySQL bajo Active Record.
     */
    @FXML
    private void onGuardar() {
        // Validación de seguridad obligatoria
        if (puntuacionSeleccionada == 0) {
            mostrarAlerta("Selecciona una puntuación");
            return;
        }

        try {
            if (modoEdicion) {
                // UPDATE
                ReseñaDAO activeResena = new ReseñaDAO(this.resena);

                activeResena.setComentario(txtComentario.getText());
                activeResena.setPuntuacion(puntuacionSeleccionada);
                activeResena.update(); // Actualiza la fila respetando su ID original

                // Sincronización en caliente para refrescar los textos de la interfaz gráfica sin recargar
                this.resena.setComentario(txtComentario.getText());
                this.resena.setPuntuacion(puntuacionSeleccionada);

            } else {
                // INSERT
                ReseñaDAO nuevaResenaActive = new ReseñaDAO();

                nuevaResenaActive.setPuntuacion(puntuacionSeleccionada);
                nuevaResenaActive.setComentario(txtComentario.getText());
                nuevaResenaActive.setFecha(LocalDate.now()); // Estampa de tiempo actual obligatoria
                nuevaResenaActive.setInmueble(reserva.getInmueble());
                nuevaResenaActive.setHuesped(reserva.getHuesped());

                nuevaResenaActive.insert();
            }

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private void cerrar() {
        Stage stage = (Stage) txtComentario.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(mensaje);
        alert.showAndWait();
    }
}

