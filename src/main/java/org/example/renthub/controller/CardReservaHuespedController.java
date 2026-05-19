package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.enums.EstadoReserva;
import java.time.format.DateTimeFormatter;

/**
 * Controlador para las tarjetas de reservas en el historial del Huésped.
 * Administra de forma dinámica el flujo de estados permitiendo pagar, modificar, cancelar
 * o realizar comentarios según las reglas del negocio de alquileres.
 */
public class CardReservaHuespedController {

    // =========================================================================
    // COMPONENTES VISUALES ENLAZADOS AL FXML
    // =========================================================================
    @FXML private Label titulo;
    @FXML private Label ubicacion;
    @FXML private Label estado;
    @FXML private Label entrada;
    @FXML private Label salida;
    @FXML private Label precioTotal;
    @FXML private HBox accionesPago; // Bloque que contiene los botones de Pagar, Editar y Cancelar
    @FXML private HBox accionResena; // Bloque que contiene el botón de Añadir Reseña

    // =========================================================================
    // ATRIBUTOS INTERNOS DE CONTROL
    // =========================================================================
    private Reserva reserva;
    // Formateador localizado para mostrar las fechas con nombre de mes completo
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMMM yyyy");

    /**
     * Enlaza la reserva contextual a la tarjeta e inicia el renderizado estructural.
     * @param reserva Instancia del alquiler contratado.
     */
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        cargarDatos();
        configurarEstado();
    }

    /**
     * Setea las propiedades del modelo e inyecta dinámicamente las clases del CSS
     * basadas en el Enum de estados para los colores personalizados.
     */
    private void cargarDatos() {
        titulo.setText(reserva.getInmueble().getTitulo());
        ubicacion.setText(
                reserva.getInmueble().getCiudad() + "\n" + reserva.getInmueble().getDireccion()
        );

        entrada.setText(reserva.getFechaEntrada().format(formatter));
        salida.setText(reserva.getFechaSalida().format(formatter));

        precioTotal.setText(
                String.format("€%.2f", reserva.getPrecioTotal())
        );

        // Limpia el formato de guiones bajos en caso de estados compuestos (ej: PENDIENTE_DE_PAGO)
        estado.setText(reserva.getEstado().name().replace("_", " "));
        // Inyecta dinámicamente el estilo CSS de color (.estado-pendiente, .estado-confirmada, etc.)
        estado.getStyleClass().add("estado-" + reserva.getEstado().name().toLowerCase());
    }

    /**
     * Lógica de visibilidad UX. Oculta o despliega los contenedores HBox de acciones
     * garantizando que el usuario solo opere si el estado de la reserva lo permite.
     */
    private void configurarEstado() {
        // Reinicio estructural por defecto
        accionesPago.setVisible(false);
        accionesPago.setManaged(false);

        accionResena.setVisible(false);
        accionResena.setManaged(false);

        // Si está pendiente, se habilitan los flujos de cobro, alteración y anulación
        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            accionesPago.setVisible(true);
            accionesPago.setManaged(true);
        }

        // Si ya está pagada y confirmada, se desbloquea en caliente la opción de opinar
        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            accionResena.setVisible(true);
            accionResena.setManaged(true);
        }
    }

    /**
     * Lanza la pasarela modal de cobros del formulario de pago.
     */
    @FXML
    private void realizarPago() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormPago.fxml")
            );
            Parent root = loader.load();

            PagoController controller = loader.getController();
            controller.setReserva(reserva); // Inyección transaccional de la reserva a facturar

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Si el pago concluyó satisfactoriamente, recalculamos la botonera del card
            if (controller.isPagoRealizado()) {
                configurarEstado();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre de forma modal el formulario de reservas permitiendo alterar fechas de estancias pendientes.
     */
    @FXML
    private void editarReserva() {
        // Control preventivo de seguridad
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Solo se pueden editar reservas pendientes");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormReserva.fxml")
            );
            Parent root = loader.load();

            FormReservaController controller = loader.getController();
            controller.setReserva(reserva);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Al regresar, forzamos un repintado de los campos por si cambiaron los días de estancia
            cargarDatos();
            configurarEstado();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Solicita confirmación explícita al huésped y ejecuta la baja lógica de la reserva en el sistema.
     */
    @FXML
    private void cancelarReserva() {
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Solo se pueden cancelar reservas pendientes");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("¿Seguro que quieres cancelar esta reserva?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Envoltura Active Record para la cancelación en base de datos
                    ReservaDAO activeReserva = new ReservaDAO(this.reserva);
                    activeReserva.cancelarReserva();

                    // Sincronización local en memoria del estado de la UI
                    this.reserva.setEstado(EstadoReserva.CANCELADA);
                    estado.setText("CANCELADA");
                    configurarEstado();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Lanza la ventana emergente para que el cliente redacte una opinión sobre la vivienda disfrutada.
     */
    @FXML
    private void añadirResena() {
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/FormReseña.fxml")
            );
            Parent root = loader.load();

            FormResenaController controller = loader.getController();
            controller.setReserva(reserva); // Sincroniza la reserva con la reseña entrante

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

