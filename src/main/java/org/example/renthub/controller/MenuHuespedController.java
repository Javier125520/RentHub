package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;
import org.example.renthub.utils.Ventanas;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MenuHuespedController {

    @FXML
    private Label lblUsuario;

    @FXML
    private StackPane contenidoCentral;

    private Usuario usuario;

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        usuario = Sesion.getUsuario();
        if (usuario != null) {
            lblUsuario.setText(usuario.getNombre());
        }

        lblUsuario.setStyle("-fx-cursor: hand;");
        lblUsuario.setOnMouseClicked(evt -> abrirPerfil());

        // Vista por defecto
        verViviendas();
    }

    // =========================
    // NAVEGACIÓN
    // =========================
    @FXML
    private void verViviendas() {
        cargarVista("ViviendasView.fxml");
    }

    @FXML
    private void verReservas() {
        cargarVista("MisReservasView.fxml");
    }

    @FXML
    private void verReseñas() {
        cargarVista("MisReseñasView.fxml");
    }

    private void abrirPerfil() {
    }

    // =========================
    // LOGOUT
    // =========================
    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/PantallaLogin.fxml")
            );
            contenidoCentral.getScene().setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // MÉTODO AUXILIAR
    // =========================
    private void cargarVista(String fxml) {
        try {
            String path = "/org/example/renthub/" + fxml;
            URL resource = getClass().getResource(path);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            contenidoCentral.getChildren().setAll(view);

        } catch (Exception e) {
            System.err.println("Error al cargar vista: " + fxml);
            e.printStackTrace();
        }
    }
}


