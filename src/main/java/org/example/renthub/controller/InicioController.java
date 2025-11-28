package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class InicioController {

    @FXML
    private void irLogin(ActionEvent event) throws IOException {
        cambiarPantalla(event, "Login.fxml");
    }

    @FXML
    private void irRegistro(ActionEvent event) throws IOException {
        cambiarPantalla(event, "Registro.fxml");
    }

    // Método genérico para cambiar escena
    private void cambiarPantalla(ActionEvent event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/renthub/" + fxml)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

