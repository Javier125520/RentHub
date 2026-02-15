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

public class FormResenaController {

    @FXML private Label star1;
    @FXML private Label star2;
    @FXML private Label star3;
    @FXML private Label star4;
    @FXML private Label star5;

    @FXML private TextArea txtComentario;

    private Reserva reserva;
    private int puntuacionSeleccionada = 0;

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    @FXML
    private void initialize() {
        configurarEstrellas();
    }

    private void configurarEstrellas() {

        Label[] estrellas = {star1, star2, star3, star4, star5};

        for (int i = 0; i < estrellas.length; i++) {
            int valor = i + 1;

            estrellas[i].setOnMouseClicked(e -> {
                puntuacionSeleccionada = valor;
                actualizarVisualEstrellas();
            });
        }
    }

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

    @FXML
    private void onPublicar() {

        if (puntuacionSeleccionada == 0) {
            mostrarAlerta("Selecciona una puntuación");
            return;
        }

        try {

            Reseña resena = new Reseña();
            resena.setPuntuacion(puntuacionSeleccionada);
            resena.setComentario(txtComentario.getText());
            resena.setFecha(LocalDate.now());

            // 🔥 RELACIONES SEGÚN TU MODELO
            resena.setInmueble(reserva.getInmueble());
            resena.setHuesped(reserva.getHuesped());

            ReseñaDAO dao = new ReseñaDAO();
            dao.insert(resena);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Reseña publicada correctamente");
            alert.showAndWait();

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelar() {
        cerrar();
    }

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


