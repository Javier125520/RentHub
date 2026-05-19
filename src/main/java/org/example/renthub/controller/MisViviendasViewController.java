package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.example.renthub.DAO.InmuebleDAO;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;
import org.example.renthub.utils.Ventanas;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de subvista para el catálogo de Inmuebles publicados por el Propietario.
 * Despliega las viviendas en una cuadrícula adaptativa (FlowPane) con capacidades de refresco directo.
 */
public class MisViviendasViewController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private FlowPane contenedorViviendas; // Rejilla contenedora con auto-envoltura horizontal

    private final InmuebleDAO inmuebleDAO = new InmuebleDAO();

    /** Inicialización por defecto de JavaFX */
    @FXML
    public void initialize() {
        try {
            cargarInmuebles();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Consulta las propiedades del casero en MySQL mediante Active Record
     * e hidrata las tarjetas de control de alquileres.
     */
    private void cargarInmuebles() throws SQLException {
        // Purgamos la rejilla para evitar duplicados en operaciones de recarga
        contenedorViviendas.getChildren().clear();

        Usuario propietario = Sesion.getUsuario();

        // Recuperación de registros asociados al id de usuario logueado
        List<Inmueble> viviendas = InmuebleDAO.findByPropietario(propietario.getIdUsuario());

        for (Inmueble inmueble : viviendas) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/InmuebleCardPropietario.fxml")
                );
                Node card = loader.load();

                // 🔑 INYECCIÓN DE RETROALIMENTACIÓN: Enlazamos el modelo y pasamos el puntero 'this' de este
                // controlador hacia la tarjeta hija para habilitar recargas automáticas al borrar o editar
                CardInmueblePropietarioController controller = loader.getController();
                controller.setInmueble(inmueble);
                controller.setParentController(this);

                contenedorViviendas.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Evento al pulsar '+ Añadir Vivienda'. Lanza el formulario de creación en modo modal limpio.
     */
    @FXML
    private void onNuevoInmueble() throws SQLException {
        Ventanas.abrirModal(
                "/org/example/renthub/FormInmueble.fxml",
                "Añadir Vivienda"
        );

        // Al cerrar el modal emergente, forzamos un repintado automático de la cuadrícula
        cargarInmuebles();
    }

    /** Método público puente expuesto para refrescar la lista reactivamente desde las tarjetas hijas */
    public void refrescar() throws SQLException {
        cargarInmuebles();
    }
}

