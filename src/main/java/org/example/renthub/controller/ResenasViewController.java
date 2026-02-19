package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Reseña;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResenasViewController {

    @FXML private VBox contenedorReseñas;

    @FXML private Label lblPromedio;
    @FXML private Label lblTotalResenas;
    @FXML private Label lblViviendas;

    private final Connection conn = MySQLConnection.getConnection();
    private final ReseñaDAO reseñaDAO = new ReseñaDAO(conn);

    @FXML
    public void initialize() {
        cargarReseñas();
    }

    private void cargarReseñas() {

        contenedorReseñas.getChildren().clear();

        try {

            Usuario propietario = Sesion.getUsuario();

            List<Reseña> reseñas =
                    reseñaDAO.findByPropietario(propietario.getIdUsuario());

            if (reseñas.isEmpty()) {
                return;
            }

            double suma = 0;
            Set<Integer> viviendasConResena = new HashSet<>();

            for (Reseña r : reseñas) {

                suma += r.getPuntuacion();
                viviendasConResena.add(
                        r.getInmueble().getIdInmueble()
                );

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/org/example/renthub/ReseñaCardPropietaio.fxml"
                        )
                );

                VBox card = loader.load();

                CardResenaPropietarioController controller =
                        loader.getController();

                controller.setResena(r);

                contenedorReseñas.getChildren().add(card);
            }

            double promedio = suma / reseñas.size();

            lblPromedio.setText(
                    String.format("⭐ %.1f", promedio)
            );

            lblTotalResenas.setText(
                    String.valueOf(reseñas.size())
            );

            lblViviendas.setText(
                    String.valueOf(viviendasConResena.size())
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
