package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.connection.Session;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    @FXML
    private void iniciarSesion(ActionEvent event) {
        lblError.setText("");

        String correo = txtCorreo.getText();
        String pass = txtContrasena.getText();

        if (correo.isEmpty() || pass.isEmpty()) {
            lblError.setText("Rellena todos los campos.");
            return;
        }

        UsuarioDAO usuarioDAO = UsuarioDAO.buscarPorCorreo(correo);

        if (usuarioDAO == null) {
            lblError.setText("Usuario no encontrado.");
            return;
        }

        if (!usuarioDAO.getContrasena().equals(pass)) {
            lblError.setText("Contraseña incorrecta.");
            return;
        }

        // Guardar usuario logueado
        Session.setUsuario(usuarioDAO);

        // Redirigir según rol
        if (usuarioDAO.getRol().name().equals("PROPIETARIO")) {
            Navigation.goTo(event, "MenuPropietario.fxml");
        } else {
            Navigation.goTo(event, "MenuHuesped.fxml");
        }
    }

    @FXML
    private void volver(ActionEvent event) {
        Navigation.goTo(event, "PantallaInicio.fxml");
    }
}

