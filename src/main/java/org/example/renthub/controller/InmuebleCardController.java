package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import org.example.renthub.model.Inmueble;

public class InmuebleCardController {

    @FXML private ImageView imagenInmueble;
    @FXML private Label lblTitulo;
    @FXML private Label lblCiudad;
    @FXML private Label lblPrecio;

    private Inmueble inmueble;
    private MenuHuespedController parentController;

    public void setData(Inmueble inmueble, MenuHuespedController parent) {
        this.inmueble = inmueble;
        this.parentController = parent;

        lblTitulo.setText(inmueble.getTitulo());
        lblCiudad.setText(inmueble.getCiudad());
        lblPrecio.setText(inmueble.getPrecioNoche() + "€/noche");

        // Cargar imagen (si falla, cargar por defecto)
        try {
            imagenInmueble.setImage(new Image(inmueble.getImagenPrincipal()));
        } catch (Exception e) {
            imagenInmueble.setImage(new Image("/org/example/renthub/assets/no-image.png"));
        }
    }

    @FXML
    public void verDetalles(ActionEvent e) {
        parentController.mostrarDetallesInmueble(inmueble);
    }
}
