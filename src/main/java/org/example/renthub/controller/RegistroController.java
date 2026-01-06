package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.model.Enum.RolUsuario;
import org.example.renthub.model.Usuario;
import org.example.renthub.utils.Utiles;
import org.example.renthub.connection.MySQLConnection;
import java.sql.Connection;
import java.io.IOException;
import javafx.scene.Scene;

public class RegistroController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private RadioButton rbHuesped;

    @FXML
    private RadioButton rbPropietario;

    // =========================
    // REGISTRO
    // =========================
    @FXML
    private void onRegistrar(ActionEvent event) {

        String nombre = nombreField.getText();
        String correo = emailField.getText();
        String password = passwordField.getText();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Todos los campos son obligatorios");
            return;
        }

        if (!Utiles.correoValido(correo)) {
            showAlert("Error", "Correo no válido");
            return;
        }

        if (!Utiles.passwordValida(password)) {
            showAlert("Error", "La contraseña no cumple los requisitos");
            return;
        }

        RolUsuario rol = rbHuesped.isSelected() ? RolUsuario.HUESPED : RolUsuario.PROPIETARIO;

        try {
            Connection conn = MySQLConnection.getConnection();
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);

            if (usuarioDAO.findByCorreo(correo) != null) {
                showAlert("Error", "Ya existe un usuario con ese correo");
                return;
            }

            Usuario usuario = new Usuario(
                    0,
                    nombre,
                    correo,
                    password,
                    rol
            );

            usuarioDAO.insert(usuario);

            showInfo("Registro completado", "Cuenta creada correctamente");
            cambiarPantalla("PantallaLogin.fxml", event);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error al registrar usuario");
        }
    }

    // =========================
    // IR A LOGIN
    // =========================
    @FXML
    private void IrAInicioSesion(ActionEvent event) {
        cambiarPantalla("PantallaLogin.fxml", event);
    }

    // =========================
    // UTILIDADES
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


    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void showInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}



