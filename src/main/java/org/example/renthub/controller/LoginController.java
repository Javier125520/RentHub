package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.model.Usuario;

import java.util.Objects;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    @FXML
    private void iniciarSesion(ActionEvent e) {
        lblError.setText("");

        String correo = txtCorreo.getText().trim();
        String contra = txtContrasena.getText().trim();

        if (correo.isEmpty() || contra.isEmpty()) {
            lblError.setText("Rellena todos los campos.");
            return;
        }

        UsuarioDAO u = UsuarioDAO.buscarPorCorreo(correo);

        if (u == null) {
            lblError.setText("El usuario no existe.");
            return;
        }

        if (!u.getContrasena().equals(contra)) {
            lblError.setText("Contraseña incorrecta.");
            return;
        }

        // Abrir menú según rol
        if (u.getRol().name().equals("PROPIETARIO")) {
            cargarMenu(e, "MenuPropietario.fxml", u);
        } else {
            cargarMenu(e, "MenuHuesped.fxml", u);
        }
    }

    @FXML
    private void volver(ActionEvent e) {
        cambiarPantalla(e, "PantallaInicio.fxml");
    }

    private void cambiarPantalla(ActionEvent e, String fxml) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/renthub/" + fxml)));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarMenu(ActionEvent e, String fxml, Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/renthub/" + fxml));
            Parent root = loader.load();

            // Paso de datos al menu
            Object controller = loader.getController();

            // El menú tendrá este método:
            // public void setUsuario(Usuario u)
            controller.getClass().getMethod("setUsuario", Usuario.class).invoke(controller, usuario);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void irRegistro(ActionEvent event) {
        cambiarPantalla(event, "Registro.fxml");
    }
}


