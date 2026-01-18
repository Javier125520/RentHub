package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import org.example.renthub.model.ServicioExtra;
import org.example.renthub.model.enums.EstadoServicio;

public class CardServicioExtraController {

    @FXML private CheckBox checkServicio;
    @FXML private Label lblNombreServicio;
    @FXML private Label lblDescripcionServicio;
    @FXML private VBox configuracionBox;
    @FXML private Spinner<Double> spPrecio;
    @FXML private ComboBox<EstadoServicio> cmbEstado;

    private ServicioExtra servicio;

    @FXML
    public void initialize() {
        spPrecio.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1000, 0, 1)
        );
        cmbEstado.getItems().setAll(EstadoServicio.values());
    }

    public void setServicio(ServicioExtra servicio) {
        this.servicio = servicio;
        lblNombreServicio.setText(servicio.getNombre());
        lblDescripcionServicio.setText(servicio.getDescripcion());
    }

    @FXML
    private void onToggleServicio() {
        boolean activo = checkServicio.isSelected();
        configuracionBox.setVisible(activo);
        configuracionBox.setManaged(activo);
    }

    // 👉 USADO AL EDITAR
    public void marcarSeleccionado(double precio, EstadoServicio estado) {
        checkServicio.setSelected(true);
        configuracionBox.setVisible(true);
        configuracionBox.setManaged(true);
        spPrecio.getValueFactory().setValue(precio);
        cmbEstado.setValue(estado);
    }

    public boolean isSeleccionado() {
        return checkServicio.isSelected();
    }

    public double getPrecio() {
        return spPrecio.getValue();
    }

    public EstadoServicio getEstado() {
        return cmbEstado.getValue();
    }

    public ServicioExtra getServicio() {
        return servicio;
    }
}
