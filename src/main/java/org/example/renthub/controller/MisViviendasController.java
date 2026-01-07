package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.example.renthub.DAO.InmuebleDAO;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.Usuario;
import org.example.renthub.utils.Sesion;

import java.io.IOException;
import java.util.List;

public class MisViviendasController {

    @FXML
    private FlowPane contenedorViviendas;

    private final InmuebleDAO inmuebleDAO = new InmuebleDAO();

    @FXML
    public void initialize() {
        cargarViviendas();
    }

    private void cargarViviendas() {
        contenedorViviendas.getChildren().clear();

        Usuario propietario = Sesion.getUsuarioActual();

        List<Inmueble> viviendas = inmuebleDAO.findByPropietario(propietario.getId());

        for (Inmueble inmueble : viviendas) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/view/cards/CardViviendaPropietario.fxml")
                );

                Node card = loader.load();

                CardViviendaPropietarioController controller = loader.getController();
                controller.setInmueble(inmueble);
                controller.setParentController(this);

                contenedorViviendas.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onNuevaVivienda() {
        // Abrir modal o cambiar vista
        // Ejemplo:
        Ventanas.abrirModal(
                "/org/example/renthub/view/forms/FormVivienda.fxml",
                "Añadir Vivienda"
        );

        // Al cerrar el modal, refrescamos
        cargarViviendas();
    }

    public void refrescar() {
        cargarViviendas();
    }
}

