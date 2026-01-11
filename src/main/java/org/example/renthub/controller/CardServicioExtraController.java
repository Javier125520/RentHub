package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import org.example.renthub.model.ServicioExtra;

public class CardServicioExtraController {

    @FXML private CheckBox checkServicio;
    @FXML private Label lblNombreServicio;
    @FXML private Label lblDescripcionServicio;

    private ServicioExtra servicio;

    public void setServicio(ServicioExtra servicio) {
        this.servicio = servicio;
        lblNombreServicio.setText(servicio.getNombre());
        lblDescripcionServicio.setText(servicio.getDescripcion());
    }

    public boolean isSeleccionado() {
        return checkServicio.isSelected();
    }

    public ServicioExtra getServicio() {
        return servicio;
    }
}


