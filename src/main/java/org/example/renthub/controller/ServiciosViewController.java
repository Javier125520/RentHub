package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.example.renthub.DAO.ServicioExtraDAO;
import org.example.renthub.model.ServicioExtra;
import org.example.renthub.utils.Ventanas;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ServiciosViewController {

    @FXML
    private FlowPane contenedorServicios;

    private final ServicioExtraDAO servicioDAO = new ServicioExtraDAO();

    @FXML
    public void initialize() {
        cargarServicios();
    }

    /* =========================
       CARGA DE SERVICIOS
       ========================= */

    private void cargarServicios() {
        contenedorServicios.getChildren().clear();

        try {
            List<ServicioExtra> servicios = servicioDAO.findAll();

            for (ServicioExtra servicio : servicios) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/ServicioCard.fxml")
                );

                Node card = loader.load();

                CardServicioController controller = loader.getController();
                controller.setServicio(servicio);
                controller.setParentController(this);

                contenedorServicios.getChildren().add(card);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       ACCIONES
       ========================= */

    @FXML
    private void onNuevoServicio() {
        Ventanas.abrirModal(
                "/org/example/renthub/FormServicio.fxml",
                "Añadir Servicio Extra"
        );

        cargarServicios();
    }

    public void refrescar() {
        cargarServicios();
    }
}

