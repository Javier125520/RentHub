package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;
import java.io.IOException;
import java.net.URL;

public class MenuPropietarioController {

    @FXML
    private Label lblUsuario;

    @FXML
    private StackPane contenidoCentral;

    @FXML
    private Button btnMisViviendas, btnServicios, btnReservas, btnResenas;

    @FXML
    public void initialize() {
        Usuario usuario = Sesion.getUsuario();
        if (usuario != null) {
            lblUsuario.setText(usuario.getNombre());
        }

        lblUsuario.setStyle("-fx-cursor: hand;");
        lblUsuario.setOnMouseClicked(evt -> abrirPerfil());

        // Vista por defecto
        verMisViviendas();
    }

    /* =========================
       NAVEGACIÓN
       ========================= */

    @FXML
    private void verMisViviendas() {
        cargarVista("MisViviendasView.fxml");
        marcarActivo(btnMisViviendas);
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

    private void abrirPerfil() {

    }

    /* =========================
       MÉTODOS AUXILIARES
       ========================= */

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

    private void marcarActivo(Button activo) {
        btnMisViviendas.getStyleClass().remove("tab-active");
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
                    getClass().getResource("/org/example/renthub/PantallaLogin.fxml")
            );
            contenidoCentral.getScene().setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

