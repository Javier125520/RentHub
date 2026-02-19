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

public class CardResenaHuespedController {

    @FXML private Label lblTituloInmueble;
    @FXML private Label lblUbicacion;
    @FXML private Label lblEstrellas;
    @FXML private Label lblPuntuacion;
    @FXML private Label lblComentario;
    @FXML private Label lblFecha;

    private Reseña resena;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMMM yyyy");

    // =========================
    // SET RESEÑA
    // =========================
    public void setResena(Reseña resena) {
        this.resena = resena;
        cargarDatos();
    }

    // =========================
    // CARGAR DATOS
    // =========================
    private void cargarDatos() {

        lblTituloInmueble.setText(
                resena.getInmueble().getTitulo()
        );

        lblUbicacion.setText(
                resena.getInmueble().getCiudad() + "\n" +
                        resena.getInmueble().getDireccion()
        );

        lblComentario.setText(resena.getComentario());

        lblFecha.setText(
                resena.getFecha().format(formatter)
        );

        lblPuntuacion.setText(String.valueOf(resena.getPuntuacion()));

        // Generar estrellas visuales
        lblEstrellas.setText(generarEstrellas(resena.getPuntuacion()));
    }

    // =========================
    // GENERAR ESTRELLAS
    // =========================
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

    // =========================
    // EDITAR
    // =========================
    @FXML
    private void onEditar() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormReseña.fxml")
            );

            Parent root = loader.load();

            // Obtener controller del formulario
            FormResenaController controller = loader.getController();
            controller.setResena(resena); // 🔥 le pasamos la reseña

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar reseña");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // 🔥 Cuando se cierre, refrescamos el card
            actualizarCard();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarCard() {
        lblComentario.setText(resena.getComentario());
        lblPuntuacion.setText(String.valueOf(resena.getPuntuacion()));
        lblEstrellas.setText("⭐".repeat(resena.getPuntuacion()));
    }

    // =========================
    // ELIMINAR
    // =========================
    @FXML
    private void onEliminar() {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("¿Eliminar esta reseña?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                ReseñaDAO dao = new ReseñaDAO();
                dao.delete(resena.getId());

                // 🔥 Eliminar visualmente el card
                lblComentario.getParent().setVisible(false);
                lblComentario.getParent().setManaged(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
