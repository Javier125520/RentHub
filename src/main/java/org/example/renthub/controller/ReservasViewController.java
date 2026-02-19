package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Usuario;
import org.example.renthub.model.enums.EstadoReserva;
import org.example.renthub.services.Sesion;
import java.sql.Connection;
import java.util.List;

public class ReservasViewController {

    @FXML private VBox reservasContainer;
    @FXML private Label lblTotalIngresos;
    @FXML private Label lblTotalReservas;

    private final Connection conn = MySQLConnection.getConnection();
    private final ReservaDAO reservaDAO = new ReservaDAO(conn);

    @FXML
    public void initialize() {
        cargarReservas();
    }

    private void cargarReservas() {

        reservasContainer.getChildren().clear();

        try {

            Usuario propietario = Sesion.getUsuario();

            // 🔥 Necesitamos un método en DAO que traiga reservas por propietario
            List<Reserva> reservas = reservaDAO.findByPropietario(propietario.getIdUsuario());

            double totalIngresos = 0;
            int totalConfirmadas = 0;

            for (Reserva r : reservas) {

                // Crear card visual
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/ReservaCardPropietario.fxml")
                );

                VBox card = loader.load();

                CardReservaPropietarioController controller = loader.getController();
                controller.setReserva(r);

                reservasContainer.getChildren().add(card);

                // Solo contar reservas confirmadas
                if (r.getEstado() == EstadoReserva.CONFIRMADA) {
                    totalIngresos += r.getPrecioTotal();
                    totalConfirmadas++;
                }
            }

            lblTotalIngresos.setText(String.format("€ %.2f", totalIngresos));
            lblTotalReservas.setText("Total de reservas confirmadas: " + totalConfirmadas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

