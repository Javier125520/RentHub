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

public class MisViviendasViewController {

    @FXML
    private FlowPane contenedorViviendas;

    private final InmuebleDAO inmuebleDAO = new InmuebleDAO();

    @FXML
    public void initialize() {
        try {
            cargarViviendas();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarViviendas() throws SQLException {
        contenedorViviendas.getChildren().clear();

        Usuario propietario = Sesion.getUsuario();

        List<Inmueble> viviendas = inmuebleDAO.findByPropietario(propietario.getIdUsuario());

        for (Inmueble inmueble : viviendas) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/InmuebleCardPropietario.fxml")
                );

                Node card = loader.load();

                CardInmueblePropietarioController controller = loader.getController();
                controller.setInmueble(inmueble);
                controller.setParentController(this);

                contenedorViviendas.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onNuevaVivienda() throws SQLException {
        Ventanas.abrirModal(
                "/org/example/renthub/FormInmueble.fxml",
                "Añadir Vivienda"
        );

        // Al cerrar el modal, refrescamos
        cargarViviendas();
    }

    public void refrescar() throws SQLException {
        cargarViviendas();
    }
}

