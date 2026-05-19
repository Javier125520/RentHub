package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.model.Reseña;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controlador de las tarjetas del historial de comentarios del Huésped.
 * Permite la rectificación o la eliminación directa de la opinión emitida por el cliente.
 */
public class CardResenaHuespedController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private Label lblTituloInmueble;
    @FXML private Label lblUbicacion;
    @FXML private Label lblEstrellas;
    @FXML private Label lblPuntuacion;
    @FXML private Label lblComentario;
    @FXML private Label lblFecha;

    private Reseña resena;
    // Formateador localizado en español para las fechas de publicación
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMMM yyyy");

    /**
     * Setea e hidrata los campos de la tarjeta con la reseña seleccionada.
     */
    public void setResena(Reseña resena) {
        this.resena = resena;
        cargarDatos();
    }

    /**
     * Escribe las descripciones, fechas formateadas e inicia la generación algorítmica de estrellas.
     */
    private void cargarDatos() {
        lblTituloInmueble.setText(resena.getInmueble().getTitulo());
        lblUbicacion.setText(
                resena.getInmueble().getCiudad() + "\n" + resena.getInmueble().getDireccion()
        );
        lblComentario.setText(resena.getComentario());
        lblFecha.setText(resena.getFecha().format(formatter));
        lblPuntuacion.setText(String.valueOf(resena.getPuntuacion()));

        // Generar caracteres de estrellas visuales dinámicas según la base de datos
        lblEstrellas.setText(generarEstrellas(resena.getPuntuacion()));
    }

    /**
     * Algoritmo de concatenación para pintar caracteres de estrellas rellenas o vacías según puntuación.
     */
    private String generarEstrellas(int puntuacion) {
        StringBuilder estrellas = new StringBuilder();

        for (int i = 0; i < puntuacion; i++) {
            estrellas.append("★");
        }
        for (int i = puntuacion; i < 5; i++) {
            estrellas.append("☆");
        }
        return estrellas.toString();
    }

    /**
     * Abre el modal del formulario de reseñas en modo edición conservando los datos previos.
     */
    @FXML
    private void onEditar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormReseña.fxml")
            );
            Parent root = loader.load();

            FormResenaController controller = loader.getController();
            controller.setResena(resena); // Fuerza al formulario a entrar en modoEdición = true

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar reseña");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresca la tarjeta local con las modificaciones hechas al volver del modal
            actualizarCard();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Sincroniza dinámicamente las etiquetas de la tarjeta en memoria tras una edición exitosa */
    private void actualizarCard() {
        lblComentario.setText(resena.getComentario());
        lblPuntuacion.setText(String.valueOf(resena.getPuntuacion()));
        lblEstrellas.setText("⭐".repeat(resena.getPuntuacion()));
    }

    /**
     * Lanza una ventana de confirmación para eliminar físicamente la reseña de la base de datos.
     */
    @FXML
    private void onEliminar() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("¿Eliminar esta reseña?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Invocamos el borrado estático de la fila en MySQL
                ReseñaDAO.delete(resena.getId());

                // Ocultamos la tarjeta de la interfaz sin reiniciar modificando los estados del nodo
                lblComentario.getParent().setVisible(false);
                lblComentario.getParent().setManaged(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}