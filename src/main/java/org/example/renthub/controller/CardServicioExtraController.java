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

/**
 * Controlador para la tarjeta de preselección de Servicios Extras dentro del formulario de inmuebles.
 * Permite al propietario activar un servicio para su vivienda, asignarle un precio exclusivo y configurar su estado.
 */
public class CardServicioExtraController {

    // =========================================================================
    // COMPONENTES VISUALES ENLAZADOS AL FXML
    // =========================================================================
    @FXML private CheckBox checkServicio;
    @FXML private Label lblNombreServicio;
    @FXML private Label lblDescripcionServicio;
    @FXML private VBox configuracionBox;          // Contenedor que guarda el precio y estado
    @FXML private Spinner<Double> spPrecio;
    @FXML private ComboBox<EstadoServicio> cmbEstado;

    private ServicioExtra servicio;

    /**
     * Método de inicialización automática de JavaFX.
     * Configura los rangos del Spinner numérico y carga los valores del Enum de estados.
     */
    @FXML
    public void initialize() {
        // Inicialización de la factoría del Spinner: rango de 0 a 1000€, valor inicial 0, pasos de 1€
        spPrecio.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1000, 0, 1)
        );
        // Carga los Enums disponibles (ACTIVO, INACTIVO, etc.) en el ComboBox
        cmbEstado.getItems().setAll(EstadoServicio.values());
    }

    /**
     * Enlaza el Servicio Extra base a la tarjeta y maqueta sus textos informativos.
     * @param servicio El servicio extra global a mapear.
     */
    public void setServicio(ServicioExtra servicio) {
        this.servicio = servicio;
        lblNombreServicio.setText(servicio.getNombre());
        lblDescripcionServicio.setText(servicio.getDescripcion());
    }

    /**
     * Captura el evento de clic sobre el CheckBox principal.
     * Muestra u oculta de forma dinámica los campos de precio y estado según la selección.
     */
    @FXML
    private void onToggleServicio() {
        boolean activo = checkServicio.isSelected();
        configuracionBox.setVisible(activo);
        configuracionBox.setManaged(activo); // Impide que un nodo invisible ocupe espacio físico en el layout VBox
    }

    /**
     * Fuerza la selección y rellenado de datos desde el exterior.
     * Se utiliza al abrir el formulario en Modo Edición para marcar los servicios que la vivienda ya tenía grabados.
     */
    public void marcarSeleccionado(double precio, EstadoServicio estado) {
        checkServicio.setSelected(true);
        configuracionBox.setVisible(true);
        configuracionBox.setManaged(true);
        spPrecio.getValueFactory().setValue(precio);
        cmbEstado.setValue(estado);
    }

    // =========================================================================
    // GETTERS DE INTERCONEXIÓN PARA EL FORMULARIO PADRE
    // =========================================================================

    /** Comprueba si el propietario ha dejado el servicio marcado */
    public boolean isSeleccionado() {
        return checkServicio.isSelected();
    }

    /** Recupera el valor económico fijado en el Spinner */
    public double getPrecio() {
        return spPrecio.getValue();
    }

    /** Recupera el estado seleccionado en el ComboBox */
    public EstadoServicio getEstado() {
        return cmbEstado.getValue();
    }

    /** Obtiene el objeto modelo base del servicio */
    public ServicioExtra getServicio() {
        return servicio;
    }
}