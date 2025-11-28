package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.DAO.ImagenInmuebleDAO;
import org.example.renthub.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservarInmuebleController {

    private Usuario usuarioActual;
    private Inmueble inmuebleActual;

    // ==========================
    // ELEMENTOS FXML
    // ==========================

    @FXML private ImageView imgInmueble;

    @FXML private Label lblTitulo;
    @FXML private Label lblUbicacion;
    @FXML private Label lblPrecioNoche;

    @FXML private DatePicker dateEntrada;
    @FXML private DatePicker dateSalida;

    @FXML private Spinner<Integer> spinnerHuespedes;

    @FXML private Label lblPrecioBase;
    @FXML private Label lblNoches;
    @FXML private Label lblTotal;

    @FXML private Label lblError;

    // ==========================
    // INICIALIZACIÓN
    // ==========================

    @FXML
    public void initialize() {
        spinnerHuespedes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
    }

    // ==========================
    // RECIBIR DATOS
    // ==========================

    public void setData(Usuario usuario, Inmueble inmueble) {
        this.usuarioActual = usuario;
        this.inmuebleActual = inmueble;

        cargarDatos();
    }

    // ==========================
    // CARGAR DATOS DEL INMUEBLE
    // ==========================

    private void cargarDatos() {
        lblTitulo.setText(inmuebleActual.getTitulo());
        lblUbicacion.setText(inmuebleActual.getCiudad());
        lblPrecioNoche.setText(inmuebleActual.getPrecioNoche() + " €/noche");

        lblPrecioBase.setText(inmuebleActual.getPrecioNoche() + " €");

        // Imagen
        List<ImagenInmueble> imagenes = ImagenInmuebleDAO.getByInmuebleId(inmuebleActual.getId());
        if (!imagenes.isEmpty()) {
            imgInmueble.setImage(new Image(imagenes.get(0).getUrl()));
        }
    }

    // ==========================
    // CÁLCULO DE PRECIO
    // ==========================

    @FXML
    private void actualizarCalculo() {
        lblError.setText("");

        LocalDate entrada = dateEntrada.getValue();
        LocalDate salida = dateSalida.getValue();

        if (entrada == null || salida == null) {
            return;
        }

        if (!salida.isAfter(entrada)) {
            lblError.setText("La fecha de salida debe ser después que la de entrada.");
            lblNoches.setText("-");
            lblTotal.setText("-");
            return;
        }

        long noches = ChronoUnit.DAYS.between(entrada, salida);
        lblNoches.setText(String.valueOf(noches));

        double total = noches * inmuebleActual.getPrecioNoche();
        lblTotal.setText(total + " €");
    }

    // ==========================
    // CONFIRMAR RESERVA
    // ==========================

    @FXML
    private void confirmar(ActionEvent event) {
        lblError.setText("");

        LocalDate entrada = dateEntrada.getValue();
        LocalDate salida = dateSalida.getValue();

        if (entrada == null || salida == null) {
            lblError.setText("Selecciona las fechas.");
            return;
        }

        if (!salida.isAfter(entrada)) {
            lblError.setText("La fecha de salida debe ser posterior a la entrada.");
            return;
        }

        long noches = ChronoUnit.DAYS.between(entrada, salida);
        double total = noches * inmuebleActual.getPrecioNoche();

        // Crear reserva
        Reserva reserva = new Reserva();
        reserva.setInmueble(inmuebleActual);
        reserva.setHuesped(usuarioActual);
        reserva.setFechaEntrada(entrada);
        reserva.setFechaSalida(salida);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setTotal(total);

        ReservaDAO rDao = new ReservaDAO(reserva);

        if (!rDao.save()) {
            lblError.setText("Error al guardar la reserva.");
            return;
        }

        // Volver a detalles del inmueble
        volver(event);
    }

    // ==========================
    // VOLVER
    // ==========================

    @FXML
    private void volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/DetallesInmueble.fxml")
            );
            Parent root = loader.load();

            // Pasamos usuario + inmueble
            Object controller = loader.getController();
            controller.getClass()
                    .getMethod("setData", Usuario.class, Inmueble.class)
                    .invoke(controller, usuarioActual, inmuebleActual);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

