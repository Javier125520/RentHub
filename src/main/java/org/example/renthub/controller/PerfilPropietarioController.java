package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.example.renthub.DAO.InmuebleDAO;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        try {
            Button btnSource = (Button) event.getSource();
            Stage stageActual = (Stage) btnSource.getScene().getWindow();

            stageActual.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/renthub/PantallaLogin.fxml"));
            Parent root = loader.load();

            Stage stageLogin = new Stage();
            stageLogin.setScene(new Scene(root));
            stageLogin.setTitle("RentHub - Iniciar Sesión");
            stageLogin.setResizable(false);
            stageLogin.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo regresar a la pantalla de login.");
        }
    }

    @FXML
    private void onCambiarContrasena() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Seguridad - RentHub");
        dialog.setHeaderText("Cambiar Contraseña");
        dialog.setContentText("Introduce tu nueva contraseña:");

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nuevaPassword -> {
            if (nuevaPassword.trim().isEmpty() || nuevaPassword.length() < 4) {
                mostrarAlerta("Contraseña inválida", "La contraseña debe tener al menos 4 caracteres.");
                return;
            }

            try {
                UsuarioDAO activeUsuario = new UsuarioDAO(Sesion.getUsuario());
                activeUsuario.setContrasena(nuevaPassword);
                activeUsuario.update();
                Sesion.getUsuario().setContrasena(nuevaPassword);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("¡Contraseña actualizada correctamente!");
                alert.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo actualizar la contraseña en la Base de Datos.");
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

