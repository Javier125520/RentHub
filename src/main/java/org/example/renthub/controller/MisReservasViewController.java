package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class MisReservasViewController {

    // =========================
    // FXML
    // =========================
    @FXML
    private VBox contenedorReservas;

    @FXML
    private Label lblContador;

    // =========================
    // DAO
    // =========================
    private final Connection conn = MySQLConnection.getConnection();
    private final ReservaDAO reservaDAO = new ReservaDAO(conn);

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        cargarReservas();
    }

    // =========================
    // CARGAR RESERVAS
    // =========================
    private void cargarReservas() {
        contenedorReservas.getChildren().clear();

        Usuario usuario = Sesion.getUsuario();

        if (usuario == null) {
            lblContador.setText("No hay sesión iniciada");
            return;
        }

        try {
            List<Reserva> reservas = reservaDAO.findByHuesped(usuario.getIdUsuario());

            lblContador.setText(reservas.size() + " reservas realizadas");

            if (reservas.isEmpty()) {
                Label vacio = new Label("No tienes reservas todavía");
                vacio.getStyleClass().add("empty-text");
                contenedorReservas.getChildren().add(vacio);
                return;
            }

            for (Reserva r : reservas) {
                cargarCardReserva(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // CARGAR CARD
    // =========================
    private void cargarCardReserva(Reserva reserva) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/renthub/ReservaCardHuesped.fxml")
        );

        VBox card = loader.load();

        CardReservaHuespedController controller = loader.getController();
        controller.setReserva(reserva);

        contenedorReservas.getChildren().add(card);
    }
}

