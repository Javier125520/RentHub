package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.renthub.model.Usuario;

public class MenuPropietarioController {

    private Usuario usuarioActual;

    @FXML private Label lblBienvenida;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        if (lblBienvenida != null) {
            lblBienvenida.setText("Hola, " + usuarioActual.getNombre());
        }
    }

    // ==============================
    // MÉTODOS DE NAVEGACIÓN
    // ==============================

    @FXML
    private void abrirGestionInmuebles(ActionEvent event) {
        cargarPantallaConUsuario(event, "PantallaMisInmuebles.fxml");
    }

    @FXML
    private void abrirPerfil(ActionEvent event) {
        cargarPantallaConUsuario(event, "PerfilHuesped.fxml");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        cambiarPantalla(event, "PantallaInicio.fxml");
    }

    public void abrirGestionReseñas(ActionEvent event) {
        cambiarPantalla(event, "GestionarReseñas.fxml");
    }

    public void abrirReservasMisInmuebles(ActionEvent event) {
        cambiarPantalla(event, "GestionarReservasPropietario.fxml");
    }


    // ==============================
    // MÉTODOS BASE
    // ==============================

    private void cambiarPantalla(ActionEvent e, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/renthub/" + fxml));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarPantallaConUsuario(ActionEvent e, String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/renthub/" + fxml));
            Parent root = loader.load();

            // Pasar usuario al siguiente controller
            Object controller = loader.getController();
            controller.getClass()
                    .getMethod("setUsuario", Usuario.class)
                    .invoke(controller, usuarioActual);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void añadirInmueble(MouseEvent event) {
    }

    public void verEstadisticas(MouseEvent event) {
    }
}

