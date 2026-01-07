package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MenuPropietarioController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button btnViviendas, btnServicios, btnReservas, btnResenas;

    @FXML
    public void initialize() {
        // Vista por defecto
        verViviendas();
    }

    /* =========================
       NAVEGACIÓN
       ========================= */

    @FXML
    private void verViviendas() {
        cargarVista("MisViviendasView.fxml");
        marcarActivo(btnViviendas);
    }

    @FXML
    private void verServicios() {
        cargarVista("ServiciosView.fxml");
        marcarActivo(btnServicios);
    }

    @FXML
    private void verReservas() {
        cargarVista("ReservasView.fxml");
        marcarActivo(btnReservas);
    }

    @FXML
    private void verResenas() {
        cargarVista("ReseñasView.fxml");
        marcarActivo(btnResenas);
    }

    /* =========================
       MÉTODOS AUXILIARES
       ========================= */

    private void cargarVista(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/view/" + fxml)
            );
            Parent view = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void marcarActivo(Button activo) {
        btnViviendas.getStyleClass().remove("tab-active");
        btnServicios.getStyleClass().remove("tab-active");
        btnReservas.getStyleClass().remove("tab-active");
        btnResenas.getStyleClass().remove("tab-active");

        activo.getStyleClass().add("tab-active");
    }

    /* =========================
       LOGOUT
       ========================= */

    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/view/LoginView.fxml")
            );
            contentPane.getScene().setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

