package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.model.Reseña;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador de subvista que gestiona el feed analítico de opiniones recibidas por un Propietario.
 * Procesa las puntuaciones acumuladas para calcular medias matemáticas exactas en tiempo real.
 */
public class ResenasViewController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private VBox contenedorReseñas; // Contenedor receptor de las tarjetas de opiniones
    @FXML private Label lblPromedio;       // Muestra la calificación media (ej: ⭐ 4.5)
    @FXML private Label lblTotalResenas;   // Muestra la cantidad total de filas evaluadas
    @FXML private Label lblViviendas;      // Detalla el conteo único de inmuebles que poseen opiniones

    /** Inicialización por defecto de JavaFX. Lanza la recarga del feed */
    @FXML
    public void initialize() {
        cargarReseñas();
    }

    /**
     * Extrae las reseñas correspondientes al casero, calcula las métricas estadísticas
     * e inyecta dinámicamente las tarjetas maquetadas en la subvista.
     */
    private void cargarReseñas() {
        contenedorReseñas.getChildren().clear();

        try {
            Usuario propietario = Sesion.getUsuario();

            // Invocación Active Record para recuperar las opiniones que afectan a las propiedades de este propietario
            List<Reseña> reseñas =
                    ReseñaDAO.findByPropietario(propietario.getIdUsuario());

            // Control perimetral UX si la lista está completamente vacía (sin valoraciones)
            if (reseñas.isEmpty()) {
                lblPromedio.setText("⭐ 0.0");
                lblTotalResenas.setText("0");
                lblViviendas.setText("0");
                return;
            }

            double suma = 0;
            // Estructura Set (Conjunto) para registrar de forma única los IDs de viviendas sin duplicar
            Set<Integer> viviendasConResena = new HashSet<>();

            // Bucle iterativo de hidratación y renderizado gráfico
            for (Reseña r : reseñas) {
                suma += r.getPuntuacion(); // Acumulador aritmético de notas para el cálculo del promedio
                viviendasConResena.add(r.getInmueble().getIdInmueble()); // Añadimos el ID al Set de exclusión de duplicados

                // Instanciación asíncrona de la plantilla individual para la opinión del casero
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/ReseñaCardPropietaio.fxml")
                );
                VBox card = loader.load();

                // Inyección del modelo sobre el controlador de la tarjeta hija
                CardResenaPropietarioController controller = loader.getController();
                controller.setResena(r);

                contenedorReseñas.getChildren().add(card);
            }

            // OPERACIONES MATEMÁTICAS ESTADÍSTICAS
            double promedio = suma / reseñas.size();

            // Volcado formateado a un decimal sobre la cabecera del panel de control
            lblPromedio.setText(String.format("⭐ %.1f", promedio));
            lblTotalResenas.setText(String.valueOf(reseñas.size()));
            lblViviendas.setText(String.valueOf(viviendasConResena.size()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}