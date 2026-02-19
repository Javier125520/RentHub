package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.renthub.DAO.InmuebleDAO;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;
import java.time.format.DateTimeFormatter;

public class PerfilPropietarioController {

    @FXML private Label lblNombre;
    @FXML private Label lblEmail;

    @FXML private Label lblViviendas;
    @FXML private Label lblReservas;
    @FXML private Label lblPendientes;
    @FXML private Label lblResenas;

    @FXML private Label lblUltimaReserva;
    @FXML private Label lblUltimaResena;

    private final InmuebleDAO inmuebleDAO = new InmuebleDAO();
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

            int viviendas = inmuebleDAO.countByPropietario(usuario.getIdUsuario());
            int reservas = reservaDAO.countByPropietario(usuario.getIdUsuario());
            int pendientes = reservaDAO.countPendientesByPropietario(usuario.getIdUsuario());
            int resenas = resenaDAO.countByPropietario(usuario.getIdUsuario());

            lblViviendas.setText(String.valueOf(viviendas));
            lblReservas.setText(String.valueOf(reservas));
            lblPendientes.setText(String.valueOf(pendientes));
            lblResenas.setText(String.valueOf(resenas));

            var ultimaReserva = reservaDAO.findUltimaByPropietario(usuario.getIdUsuario());
            if (ultimaReserva != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy");
                lblUltimaReserva.setText(
                        "Última reserva: " +
                                ultimaReserva.getFechaEntrada().format(formatter)
                );
            }

            var ultimaResena = resenaDAO.findUltimaByPropietario(usuario.getIdUsuario());
            if (ultimaResena != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy");
                lblUltimaResena.setText(
                        "Última reseña recibida: " +
                                ultimaResena.getFecha().format(formatter)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

