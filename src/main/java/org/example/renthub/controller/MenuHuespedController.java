package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuHuespedController implements Initializable {

    @FXML
    private Label lblUsuario;

    @FXML
    private FlowPane contenedorInmuebles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Usuario de ejemplo (luego vendrá de sesión)
        lblUsuario.setText("Ana López");

        cargarInmuebles();
    }

    /* =========================
       NAVEGACIÓN
       ========================= */

    @FXML
    private void verViviendas() {
        contenedorInmuebles.getChildren().clear();
        cargarInmuebles();
    }

    @FXML
    private void verReservas() {
        contenedorInmuebles.getChildren().clear();
        // Aquí luego cargas cards de reservas
    }

    @FXML
    private void verReseñas() {
        contenedorInmuebles.getChildren().clear();
        // Aquí luego cargas cards de reseñas
    }

    /* =========================
       CARGA DE INMUEBLES
       ========================= */

    private void cargarInmuebles() {
        try {
            for (int i = 0; i < 6; i++) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/InmuebleCardHuesped.fxml")
                );
                VBox card = loader.load();

                CardInmuebleHuespedController controller = loader.getController();
                controller.setDatos(
                        "Apartamento Moderno",
                        "Madrid, España",
                        "Hermoso apartamento completamente equipado",
                        "85 €/noche",
                        "2 hab · 4 huéspedes"
                );

                contenedorInmuebles.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       LOGOUT
       ========================= */

    @FXML
    private void onLogout() {
        cambiarPantalla("login.fxml");
    }

    private void cambiarPantalla(String fxml) {
        try {
            Stage stage = (Stage) lblUsuario.getScene().getWindow();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/example/renthub/view/" + fxml)
            );
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

