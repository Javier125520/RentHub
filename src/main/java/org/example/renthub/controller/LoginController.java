package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.model.Usuario;
import org.example.renthub.model.enums.RolUsuario;
import java.io.IOException;
import javafx.scene.Scene;
import org.example.renthub.services.Sesion;
import org.example.renthub.utils.Utiles;

/**
 * Controlador de Accesos y Autenticación del ecosistema de RentHub.
 * Valida credenciales contra MySQL y bifurca la navegación al menú correspondiente según el rol del usuario.
 */
public class LoginController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField; // Campo de contraseña enmascarado

    /**
     * Captura las entradas e inicia el flujo de autenticación del usuario.
     */
    @FXML
    private void onLogin(ActionEvent event) {
        String correo = emailField.getText();
        String password = passwordField.getText();

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Debes rellenar todos los campos");
            return;
        }

        try {
            // Buscamos si existe alguna cuenta registrada con ese correo electrónico único
            Usuario usuario = UsuarioDAO.findByCorreo(correo);

            String passwordCifrada = Utiles.hashPassword(password);

            // Validación de seguridad (En un entorno real aquí se acoplaría un desencriptador tipo BCrypt)
            if (usuario == null || !usuario.getContrasena().equals(passwordCifrada)) {
                mostrarAlerta("Error", "Correo o contraseña incorrectos");
                return;
            }

            // Registramos la instancia del usuario en el Singleton global de la Sesión
            Sesion.setUsuario(usuario);

            // Redirección al panel especializado según el Rol asignado
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

    /** Redirige al flujo de creación de cuentas de la Pantalla de Registro */
    @FXML
    private void irARegistro(ActionEvent event) {
        cambiarPantalla("PantallaRegistro.fxml", event);
    }

    /**
     * Método genérico reutilizable para conmutar escenarios de pantallas en el hilo principal de JavaFX.
     * @param fxml El nombre del fichero de vista a cargar.
     */
    private void cambiarPantalla(String fxml, ActionEvent event) {
        try {
            // Conseguimos el Stage (Ventana física) que disparó el ActionEvent
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Cargamos de forma asíncrona la nueva estructura del nodo raíz
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/" + fxml)
            );

            // Intercambiamos la escena manteniendo el tamaño de la ventana
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
