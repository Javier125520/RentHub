package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.model.RolUsuario;
import org.example.renthub.model.Usuario;

import java.util.Objects;

public class RegistroController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<String> comboRol;
    @FXML private Label lblError;

    @FXML
    public void initialize() {
        comboRol.getItems().addAll("PROPIETARIO", "HUESPED");
    }

    @FXML
    private void registrar(ActionEvent event) {
        lblError.setText("");

        // Recoger valores
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contra = txtContrasena.getText().trim();
        String rol = comboRol.getValue();

        // Validaciones
        if (nombre.isEmpty() || correo.isEmpty() || contra.isEmpty() || rol == null) {
            lblError.setText("Debes rellenar todos los campos.");
            return;
        }

        // Validar existencia de correo
        UsuarioDAO existente = UsuarioDAO.buscarPorCorreo(correo);
        if (existente != null) {
            lblError.setText("Este correo ya está registrado.");
            return;
        }

        // Crear usuario
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setContrasena(contra);
        nuevo.setRol(RolUsuario.valueOf(rol));

        // Guardar en BD
        UsuarioDAO uDao = new UsuarioDAO(nuevo);

        boolean ok = uDao.save();
        if (!ok) {
            lblError.setText("Error al registrar el usuario.");
            return;
        }

        // Volver al login
        irLogin(event);
    }

    @FXML
    private void volver(ActionEvent event) {
        irLogin(event);
    }

    private void irLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/renthub/Login.fxml")));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

