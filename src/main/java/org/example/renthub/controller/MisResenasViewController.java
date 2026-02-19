package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.model.Reseña;
import org.example.renthub.model.Usuario;

import java.util.List;

public class MisResenasViewController {

    @FXML private VBox contenedorResenas;

    private Usuario usuario;

    // =========================
    // INICIALIZAR CON USUARIO
    // =========================
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarResenas();
    }

    // =========================
    // CARGAR RESEÑAS DEL USUARIO
    // =========================
    private void cargarResenas() {
        try {
            contenedorResenas.getChildren().clear();
            ReseñaDAO reseñaDAO = new ReseñaDAO();
            List<Reseña> resenas = reseñaDAO.findByUsuario(usuario);

            for (Reseña resena : resenas) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/ReseñaCardHuesped.fxml")
                );
                Parent card = loader.load();

                // Pasar la reseña al card
                CardResenaHuespedController controller = loader.getController();
                controller.setResena(resena);

                contenedorResenas.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
