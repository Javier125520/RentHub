package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.renthub.model.Reseña;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controlador de lectura para las tarjetas de opiniones recibidas por un Propietario.
 * Despliega los textos, estrellas y metadatos del huésped que realizó la valoración.
 */
public class CardResenaPropietarioController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private Label lblVivienda;
    @FXML private Label lblUbicacion;
    @FXML private Label lblPuntuacion;
    @FXML private Label lblMeta;
    @FXML private Label lblComentario;

    // Formateador completo con idioma forzado a español para la metadata de la tarjeta
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

    /**
     * Hidrata y asocia de forma limpia la opinión recibida para pintarla en el feed del casero.
     * @param resena Instancia de la reseña a procesar.
     */
    public void setResena(Reseña resena) {
        // 🏠 Información de la Vivienda
        lblVivienda.setText(resena.getInmueble().getTitulo());
        lblUbicacion.setText("📍 " + resena.getInmueble().getCiudad());

        // ⭐ Puntuación numérico-visual
        lblPuntuacion.setText("⭐ " + resena.getPuntuacion());

        // 👤 Composición de la metadata del Huésped + Fecha de envío
        String fecha = "";
        if (resena.getFecha() != null) {
            fecha = resena.getFecha().format(formatter);
        }

        lblMeta.setText(
                "👤 " + resena.getHuesped().getNombre() + " · " + fecha
        );

        // 💬 Contenido del comentario descriptivo
        lblComentario.setText(resena.getComentario());
    }
}

