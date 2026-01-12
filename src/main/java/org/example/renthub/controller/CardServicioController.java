package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.renthub.DAO.ServicioExtraDAO;
import org.example.renthub.model.ServicioExtra;
import org.example.renthub.utils.Ventanas;

import java.sql.SQLException;

public class CardServicioController {

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblDescripcion;

    private ServicioExtra servicio;
    private ServiciosViewController parentController;

    private final ServicioExtraDAO servicioDAO = new ServicioExtraDAO();

    /* =========================
       SETTERS
       ========================= */

    public void setServicio(ServicioExtra servicio) {
        this.servicio = servicio;
        cargarDatos();
    }

    public void setParentController(ServiciosViewController controller) {
        this.parentController = controller;
    }

    /* =========================
       DATOS
       ========================= */

    private void cargarDatos() {
        lblNombre.setText(servicio.getNombre());
        lblDescripcion.setText(servicio.getDescripcion());
    }

    /* =========================
       ACCIONES
       ========================= */

    @FXML
    private void onEditar() {
        Ventanas.abrirModalConDatos(
                "/org/example/renthub/FormServicio.fxml",
                "Editar Servicio",
                servicio
        );

        // refrescamos la vista al cerrar
        parentController.refrescar();
    }

    @FXML
    private void onEliminar() {
        try {
            servicioDAO.delete(servicio.getIdServicio());
            parentController.refrescar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

