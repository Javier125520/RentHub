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
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.DAO.ReseñaDAO;
import org.example.renthub.model.Usuario;
import org.example.renthub.DAO.UsuarioDAO;
import org.example.renthub.services.Sesion;
import org.example.renthub.utils.Utiles;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

    /**
     *
     */
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

    /**
     *
     * @param event
     */
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

    /**
     *
     */
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
                String nuevaPasswordCifrada = Utiles.hashPassword(nuevaPassword);

                UsuarioDAO activeUsuario = new UsuarioDAO(Sesion.getUsuario());
                activeUsuario.setContrasena(nuevaPasswordCifrada);
                activeUsuario.update();
                Sesion.getUsuario().setContrasena(nuevaPasswordCifrada);

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

    // Método auxiliar para alertas del controlador
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

