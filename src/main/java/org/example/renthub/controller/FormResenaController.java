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
    private Reseña resena;
    private boolean modoEdicion = false;

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public void setResena(Reseña resena) {
        this.resena = resena;
        this.modoEdicion = true;

        // cargar datos en el form
        txtComentario.setText(resena.getComentario());
        puntuacionSeleccionada = resena.getPuntuacion();
        actualizarVisualEstrellas();    }


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
    private void onGuardar() {

        if (puntuacionSeleccionada == 0) {
            mostrarAlerta("Selecciona una puntuación");
            return;
        }

        try {

            ReseñaDAO dao = new ReseñaDAO();

            if (modoEdicion) {

                // 🔥 UPDATE
                resena.setComentario(txtComentario.getText());
                resena.setPuntuacion(puntuacionSeleccionada);

                dao.update(resena);

            } else {

                // 🔥 INSERT
                Reseña nueva = new Reseña();
                nueva.setPuntuacion(puntuacionSeleccionada);
                nueva.setComentario(txtComentario.getText());
                nueva.setFecha(LocalDate.now());
                nueva.setInmueble(reserva.getInmueble());
                nueva.setHuesped(reserva.getHuesped());

                dao.insert(nueva);
            }

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // CANCELAR
    // =========================

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


