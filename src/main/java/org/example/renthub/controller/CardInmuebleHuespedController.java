package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardInmuebleHuespedController {

    @FXML
    private ImageView imagen;

    @FXML
    private Label titulo;

    @FXML
    private Label ubicacion;

    @FXML
    private Label descripcion;

    @FXML
    private Label precio;

    @FXML
    private Label capacidad;

    /* =========================
       SET DATOS
       ========================= */

    public void setDatos(String titulo,
                         String ubicacion,
                         String descripcion,
                         String precio,
                         String capacidad) {

        this.titulo.setText(titulo);
        this.ubicacion.setText(ubicacion);
        this.descripcion.setText(descripcion);
        this.precio.setText(precio);
        this.capacidad.setText(capacidad);
    }

    /* =========================
       ACCIÓN
       ========================= */

    @FXML
    private void onReservar() {
        System.out.println("Reservar inmueble: " + titulo.getText());
        // Aquí luego abres pantalla de reserva
    }
}

