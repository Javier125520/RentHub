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

/**
 * Controlador de vista para el catálogo y pool global de Servicios Extras (Exclusivo de Propietarios).
 * Despliega los servicios dados de alta y ofrece el punto de entrada para crear nuevas prestaciones.
 */
public class ServiciosViewController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private FlowPane contenedorServicios; // Rejilla elástica con ajuste horizontal automático de tarjetas [cite: 239]

    private final ServicioExtraDAO servicioDAO = new ServicioExtraDAO();

    /** Inicialización base por defecto de JavaFX [cite: 236] */
    @FXML
    public void initialize() {
        cargarServicios();
    }

    /**
     * Consulta el catálogo totalizado en MySQL mediante Active Record
     * e hidrata las tarjetas de gestión individual de servicios[cite: 236].
     */
    private void cargarServicios() {
        contenedorServicios.getChildren().clear();

        try {
            List<ServicioExtra> servicios = servicioDAO.findAll();

            for (ServicioExtra servicio : servicios) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/ServicioCard.fxml")
                );
                Node card = loader.load();

                // 🔑 ENLACE DE RETROALIMENTACIÓN: Asignamos el modelo de datos e inyectamos el puntero 'this'
                // de esta vista general hacia la tarjeta hija para posibilitar recargas reactivas al borrar [cite: 236]
                CardServicioController controller = loader.getController();
                controller.setServicio(servicio);
                controller.setParentController(this);

                contenedorServicios.getChildren().add(card);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Evento al hacer clic en '➕ Nuevo Servicio'. Abre el formulario limpio en un modal flotante bloqueante[cite: 238].
     */
    @FXML
    private void onNuevoServicio() {
        Ventanas.abrirModal(
                "/org/example/renthub/FormServicio.fxml",
                "Añadir Servicio Extra"
        );

        // Forzamos un repintado de actualización al regresar de la operación del formulario emergente [cite: 238]
        cargarServicios();
    }

    /**
     * Metodo puente expuesto de forma pública para recibir solicitudes de recarga reactivas desde los sub-cards hijos[cite: 236].
     */
    public void refrescar() {
        cargarServicios();
    }
}

