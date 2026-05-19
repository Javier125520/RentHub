package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.renthub.DAO.ServicioExtraDAO;
import org.example.renthub.model.ServicioExtra;
import org.example.renthub.utils.Ventanas;
import java.sql.SQLException;

/**
 * Controlador para la tarjeta del catálogo de Servicios Extras globales (Vistas del casero).
 * Permite la modificación o destrucción del servicio afectando al pool global del software.
 */
public class CardServicioController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private Label lblNombre;
    @FXML private Label lblDescripcion;

    // Atributos de enlace de negocio
    private ServicioExtra servicio;
    private ServiciosViewController parentController; // Puntero de retorno para invocar refrescos automáticos

    /**
     * Setea e hidrata los datos descriptivos del servicio en la tarjeta.
     */
    public void setServicio(ServicioExtra servicio) {
        this.servicio = servicio;
        cargarDatos();
    }

    /**
     * Acopla el controlador del listado contenedor para habilitar actualizaciones reactivas en la escena.
     */
    public void setParentController(ServiciosViewController controller) {
        this.parentController = controller;
    }

    /** Escribe los textos informativos base del servicio */
    private void cargarDatos() {
        lblNombre.setText(servicio.getNombre());
        lblDescripcion.setText(servicio.getDescripcion());
    }

    /**
     * Evento al pulsar 'Editar'. Lanza el modal inyectándole los datos del servicio a rectificar.
     */
    @FXML
    private void onEditar() {
        Ventanas.abrirModalConDatos(
                "/org/example/renthub/FormServicio.fxml",
                "Editar Servicio",
                servicio
        );

        // Al retornar de la operación de guardado, forzamos un redibujado de la cuadrícula global
        parentController.refrescar();
    }

    /**
     * Evento al pulsar el botón de papelera. Elimina de manera física la fila en MySQL.
     */
    @FXML
    private void onEliminar() {
        try {
            // Invocación estática Active Record para remover el servicio
            ServicioExtraDAO.delete(servicio.getIdServicio());

            // Recarga instantánea y reactiva del listado de la interfaz gráfica
            parentController.refrescar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
