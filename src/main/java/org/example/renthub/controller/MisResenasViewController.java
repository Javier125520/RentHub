package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.model.Reseña;
import org.example.renthub.model.Usuario;
import java.util.List;

/**
 * Controlador de subvista para el historial de opiniones publicadas por el Huésped.
 * Recupera de forma iterativa las valoraciones y las inyecta como nodos gráficos dentro de un contenedor vertical.
 */
public class MisResenasViewController {

    // =========================================================================
    // COMPONENTES VISUALES ENLAZADOS AL FXML
    // =========================================================================
    @FXML private VBox contenedorResenas; // Layout vertical receptor de las tarjetas de reseñas

    private Usuario usuario;

    /**
     * Enlaza al usuario de la sesión contextual e inicia de forma inmediata la carga de las opiniones.
     * @param usuario Instancia del usuario autenticado que emitió las valoraciones.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarResenas();
    }

    /**
     * Consulta el histórico de reseñas del cliente en MySQL a través del DAO activo
     * e hidrata dinámicamente el layout con componentes FXML independientes.
     */
    private void cargarResenas() {
        try {
            // Limpieza preventiva de seguridad de elementos residuales
            contenedorResenas.getChildren().clear();

            // Invocación Active Record para recuperar las valoraciones del usuario de la BD
            List<Reseña> resenas = ReseñaDAO.findByUsuario(usuario);

            // Bucle de renderizado gráfico adaptativo
            for (Reseña resena : resenas) {
                // Instanciamos el cargador independiente para la plantilla de la tarjeta
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/ReseñaCardHuesped.fxml")
                );
                Parent card = loader.load();

                // Recuperamos por reflexión el controlador específico del card para inyectarle el modelo
                CardResenaHuespedController controller = loader.getController();
                controller.setResena(resena);

                // Añadimos el nodo gráfico renderizado al contenedor vertical de la vista
                contenedorResenas.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}