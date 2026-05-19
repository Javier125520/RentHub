package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.model.enums.RolUsuario;
import org.example.renthub.model.Usuario;
import java.io.IOException;
import javafx.scene.Scene;

/**
 * Controlador para la Pantalla de Creación y Registro de nuevas cuentas en RentHub.
 * Valida la existencia previa del correo e inserta los datos transaccionalmente mediante Active Record.
 */
public class RegistroController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton rbHuesped;
    @FXML private RadioButton rbPropietario; // RadioButton que determina si la cuenta publica o alquila casas

    /**
     * Procesa y empaqueta el registro de la nueva cuenta capturando los valores del formulario.
     */
    @FXML
    private void onRegistrar(ActionEvent event) {
        String nombre = nombreField.getText();
        String correo = emailField.getText();
        String password = passwordField.getText();

        // Operador ternario para capturar la selección del ToggleGroup de roles de usuario
        RolUsuario rol = rbPropietario.isSelected() ? RolUsuario.PROPIETARIO : RolUsuario.HUESPED;

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Todos los campos son obligatorios");
            return;
        }

        try {
            // Regla de unicidad: Impedimos el registro si el email ya existe en la base de datos MySQL
            if (UsuarioDAO.findByCorreo(correo) != null) {
                showAlert("Error", "Ya existe un usuario con ese correo");
                return;
            }

            // Instanciamos la envoltura Active Record y ejecutamos el guardado físico de la fila
            UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario(0, nombre, correo, password, rol));
            usuarioDAO.insert();

            showInfo("Registro completado", "Cuenta creada correctamente");

            // Redirección inmediata hacia la pantalla de acceso para que el usuario inicie sesión
            cambiarPantalla("PantallaLogin.fxml", event);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error al registrar usuario");
        }
    }

    /** Evento clic para retroceder a la pantalla de Login sin completar el formulario */
    @FXML
    private void IrAInicioSesion(ActionEvent event) {
        cambiarPantalla("PantallaLogin.fxml", event);
    }

    /** Intercambia la escena completa de la ventana principal de forma fluida */
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


