package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.renthub.DAO.ReservaDAO;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.Reserva;
import org.example.renthub.model.Usuario;
import org.example.renthub.model.enums.EstadoReserva;
import org.example.renthub.services.Sesion;
import org.example.renthub.utils.Utiles;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Controlador para la creación y edición temporal de Reservas de alojamiento.
 * Calcula de forma reactiva el importe total en función de los días seleccionados en el calendario.
 */
public class FormReservaController {

    // =========================================================================
    // COMPONENTES VISUALES ENLAZADOS AL FXML
    // =========================================================================
    @FXML private Label lblTitulo;
    @FXML private Label lblPrecio;
    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;
    @FXML private VBox totalContainer; // Contenedor del precio total (se muestra dinámicamente)
    @FXML private Label lblTotal;
    @FXML private Label lblDetalle;

    // Atributos de control de negocio
    private Inmueble inmueble;
    private Reserva reservaEditando = null;
    private boolean modoEdicion = false;

    /**
     * Método de inicialización automática de JavaFX.
     * Configura escuchadores en los DatePickers para recalcular el precio al vuelo.
     */
    @FXML
    public void initialize() {
        // Añadimos oyentes para capturar cualquier cambio de fecha en los calendarios
        fechaInicio.valueProperty().addListener((obs, oldVal, newVal) -> calcularTotal());
        fechaFin.valueProperty().addListener((obs, oldVal, newVal) -> calcularTotal());
    }

    /**
     * Setea e hidrata los datos del Inmueble sobre el que se va a tramitar la solicitud de reserva.
     * @param inmueble El inmueble seleccionado por el huésped.
     */
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
        lblTitulo.setText("Reservar " + inmueble.getTitulo());
        lblPrecio.setText(String.format("%.2f € / noche", inmueble.getPrecioNoche()));
    }

    /**
     * Inyecta una reserva ya existente para cambiar el formulario a Modo Edición (Modificar fechas).
     * @param reserva La reserva del historial que se desea alterar.
     */
    public void setReserva(Reserva reserva) {
        this.reservaEditando = reserva;
        this.inmueble = reserva.getInmueble();
        this.modoEdicion = true;

        // Hidratamos los componentes visuales con los datos almacenados
        lblTitulo.setText("Modificar Reserva - " + inmueble.getTitulo());
        lblPrecio.setText(String.format("%.2f € / noche", inmueble.getPrecioNoche()));
        fechaInicio.setValue(reserva.getFechaEntrada());
        fechaFin.setValue(reserva.getFechaSalida());

        // Forzamos el cálculo inicial de la tarifa en pantalla
        calcularTotal();
    }

    /**
     * Algoritmo de cálculo dinámico de noches e importes de hospedaje.
     * Controla las reglas de negocio (fechas válidas) y despliega el desglose del precio total.
     */
    private void calcularTotal() {
        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFin.getValue();

        // Validación perimetral de seguridad: si faltan datos o las fechas están invertidas
        if (!Utiles.fechasValidas(inicio, fin)) {
            totalContainer.setVisible(false);
            totalContainer.setManaged(false);
            return;
        }

        // Cálculo de días exactos transcurridos usando ChronoUnit de Java Time
        long noches = ChronoUnit.DAYS.between(inicio, fin);
        double total = noches * inmueble.getPrecioNoche();

        // Actualizamos los textos informativos en caliente para el huésped
        lblTotal.setText(String.format("Total: %.2f €", total));
        lblDetalle.setText(String.format("%.2f € x %d noches", inmueble.getPrecioNoche(), noches));

        // Hacemos visible el cuadro de tarifas con efectos adaptativos
        totalContainer.setVisible(true);
        totalContainer.setManaged(true);
    }

    /**
     * Valida la coherencia de datos y ejecuta el guardado de la reserva (INSERT o UPDATE) bajo Active Record.
     */
    @FXML
    private void confirmar() {
        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFin.getValue();

        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            mostrarError("Por favor, selecciona un rango de fechas válido.");
            return;
        }

        try {
            long noches = ChronoUnit.DAYS.between(inicio, fin);
            double total = noches * inmueble.getPrecioNoche();

            if (modoEdicion) {
                // UPDATE
                reservaEditando.setFechaEntrada(inicio);
                reservaEditando.setFechaSalida(fin);
                reservaEditando.setPrecioTotal(total);

                // Envolvemos la reserva base en su DAO activo correspondiente para invocar la base de datos
                ReservaDAO activeReserva = new ReservaDAO(reservaEditando);
                activeReserva.update();

            } else {
                // INSERT
                Usuario huesped = Sesion.getUsuario(); // Recuperamos al usuario autenticado de la sesión global

                ReservaDAO nuevaReservaActive = new ReservaDAO();
                nuevaReservaActive.setInmueble(this.inmueble);
                nuevaReservaActive.setHuesped(huesped);
                nuevaReservaActive.setFechaEntrada(inicio);
                nuevaReservaActive.setFechaSalida(fin);
                nuevaReservaActive.setPrecioTotal(total);
                nuevaReservaActive.setEstado(EstadoReserva.PENDIENTE); // Nace en estado de pre-pago

                boolean exito = nuevaReservaActive.insert();
                if (!exito) {
                    mostrarError("El alojamiento no está disponible en las fechas seleccionadas.");
                    return;
                }
            }

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo guardar la reserva en el sistema.");
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
