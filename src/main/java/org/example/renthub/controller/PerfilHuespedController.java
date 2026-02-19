package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;
import java.time.format.DateTimeFormatter;

public class PerfilHuespedController {

    @FXML private Label lblNombre;
    @FXML private Label lblEmail;

    @FXML private Label lblTotalReservas;
    @FXML private Label lblPagadas;
    @FXML private Label lblPendientes;
    @FXML private Label lblResenas;
    @FXML private Label lblUltimaReserva;

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ReseñaDAO resenaDAO = new ReseñaDAO();

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {

        try {
            Usuario usuario = Sesion.getUsuario();

            lblNombre.setText(usuario.getNombre());
            lblEmail.setText(usuario.getCorreo());

            int total = reservaDAO.countByHuesped(usuario.getIdUsuario());
            int pagadas = reservaDAO.countPagadasByHuesped(usuario.getIdUsuario());
            int pendientes = reservaDAO.countPendientesByHuesped(usuario.getIdUsuario());
            int resenas = resenaDAO.countByHuesped(usuario.getIdUsuario());

            lblTotalReservas.setText(String.valueOf(total));
            lblPagadas.setText(String.valueOf(pagadas));
            lblPendientes.setText(String.valueOf(pendientes));
            lblResenas.setText(String.valueOf(resenas));

            var ultimaReserva = reservaDAO.findUltimaByHuesped(usuario.getIdUsuario());

            if (ultimaReserva != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy");
                lblUltimaReserva.setText(
                        "Última reserva: " +
                                ultimaReserva.getFechaEntrada().format(formatter)
                );
            } else {
                lblUltimaReserva.setText("Sin reservas todavía");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

