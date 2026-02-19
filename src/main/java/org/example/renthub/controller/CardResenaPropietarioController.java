package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.renthub.model.Reseña;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CardResenaPropietarioController {

    @FXML private Label lblVivienda;
    @FXML private Label lblUbicacion;
    @FXML private Label lblPuntuacion;
    @FXML private Label lblMeta;
    @FXML private Label lblComentario;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
                    new Locale("es", "ES"));

    public void setResena(Reseña resena) {

        // 🏠 Vivienda
        lblVivienda.setText(
                resena.getInmueble().getTitulo()
        );

        lblUbicacion.setText(
                "📍 " + resena.getInmueble().getCiudad()
        );

        // ⭐ Puntuación
        lblPuntuacion.setText(
                "⭐ " + resena.getPuntuacion()
        );

        // 👤 Usuario + Fecha
        String fecha = "";

        if (resena.getFecha() != null) {
            fecha = resena.getFecha().format(formatter);
        }

        lblMeta.setText(
                "👤 " + resena.getHuesped().getNombre() +
                        " · " + fecha
        );

        // 💬 Comentario
        lblComentario.setText(
                resena.getComentario()
        );
    }
}

