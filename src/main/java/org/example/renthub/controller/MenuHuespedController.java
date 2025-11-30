package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.example.renthub.model.Inmueble;
import org.example.renthub.model.Usuario;

import java.util.List;

public class MenuHuespedController {

    public GridPane gridInmuebles;
    private Usuario usuarioActual; // Usuario logueado

    @FXML private Label lblBienvenida;


    // Recibir usuario desde LoginController
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        if (lblBienvenida != null) {
            lblBienvenida.setText("Hola, " + usuario.getNombre());
        }
    }

    // ================================
    // NAVEGACIÓN DEL MENÚ
    // ================================

    @FXML
    private void buscarInmuebles(ActionEvent event) {
        cargarPantallaConUsuario(event, "BuscarInmuebles.fxml");
    }

    @FXML
    private void verMisReservas(ActionEvent event) {
        cargarPantallaConUsuario(event, "MisReservas.fxml");
    }

    public void verMisResenas(ActionEvent event) {
    }

    @FXML
    private void abrirPerfil(ActionEvent event) {
        cargarPantallaConUsuario(event, "PerfilHuesped.fxml");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        cambiarPantalla(event, "PantallaInicio.fxml");
    }

    // ================================
    // MÉTODOS BASE PARA CAMBIAR PANTALLA
    // ================================

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

            // Pasar usuario al siguiente controlador
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

    public void cargarCards(List<Inmueble> lista) {
        gridInmuebles.getChildren().clear();

        int columna = 0;
        int fila = 0;

        try {
            for (Inmueble inm : lista) {

                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/org/example/renthub/views/huesped/InmuebleCard.fxml"));

                AnchorPane card = loader.load();

                InmuebleCardController cardController = loader.getController();
                cardController.setData(inm, this);

                gridInmuebles.add(card, columna++, fila);

                if (columna == 4) { // 4 columnas por fila
                    columna = 0;
                    fila++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

