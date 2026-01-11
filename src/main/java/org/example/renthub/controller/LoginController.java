package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.model.Usuario;
import org.example.renthub.connection.MySQLConnection;
import java.sql.Connection;
import org.example.renthub.model.enums.RolUsuario;
import java.io.IOException;
import javafx.scene.Scene;
import org.example.renthub.services.Sesion;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    // =========================
    // LOGIN
    // =========================
    @FXML
    private void onLogin(ActionEvent event) {
        String correo = emailField.getText();
        String password = passwordField.getText();

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Debes rellenar todos los campos");
            return;
        }

        try {
            Connection conn = MySQLConnection.getConnection();
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);

            Usuario usuario = usuarioDAO.findByCorreo(correo);

            if (usuario == null || !usuario.getContrasena().equals(password)) {
                mostrarAlerta("Error", "Correo o contraseña incorrectos");
                return;
            }

            Sesion.setUsuario(usuario);

            // Redirigir según rol
            if (usuario.getRol() == RolUsuario.HUESPED) {
                cambiarPantalla("MenuHuesped.fxml", event);
            } else {
                cambiarPantalla("MenuPropietario.fxml", event);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al iniciar sesión");
        }
    }

    // =========================
    // IR A REGISTRO
    // =========================
    @FXML
    private void irARegistro(ActionEvent event) {
        cambiarPantalla("PantallaRegistro.fxml", event);
    }

    // =========================
    // UTILIDAD CAMBIO PANTALLA
    // =========================
    private void cambiarPantalla(String fxml, ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/" + fxml)
            );
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void mostrarAlerta (String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
