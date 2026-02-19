package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.InmuebleDAO;
import org.example.renthub.DAO.ServicioExtraDAO;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.ServicioExtra;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViviendasViewController {

    // =========================
    // FXML
    // =========================
    @FXML private TextField txtPrecioMin;
    @FXML private TextField txtPrecioMax;
    @FXML private TextField txtCiudad;
    @FXML private Spinner<Integer> spHabitaciones;
    @FXML private Spinner<Integer> spCapacidad;
    @FXML private TextField searchField;

    @FXML private DatePicker dpFechaEntrada;
    @FXML private DatePicker dpFechaSalida;

    @FXML private VBox panelFiltros;
    @FXML private FlowPane contenedorInmuebles;
    @FXML private FlowPane contenedorServiciosFiltro;

    // =========================
    // DAO
    // =========================
    private final Connection conn = MySQLConnection.getConnection();
    private final InmuebleDAO inmuebleDAO = new InmuebleDAO(conn);
    private final ServicioExtraDAO servicioDAO = new ServicioExtraDAO();

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {

        spHabitaciones.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0)
        );
        spCapacidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1)
        );

        cargarServiciosFiltro();
        cargarInmuebles();
    }

    // =========================
    // MOSTRAR / OCULTAR FILTROS
    // =========================
    @FXML
    private void abrirFiltros() {
        boolean visible = panelFiltros.isVisible();
        panelFiltros.setVisible(!visible);
        panelFiltros.setManaged(!visible);
    }

    // =========================
    // CARGAR SERVICIOS FILTRO
    // =========================
    private void cargarServiciosFiltro() {
        try {
            contenedorServiciosFiltro.getChildren().clear();

            for (ServicioExtra s : servicioDAO.findAll()) {
                CheckBox cb = new CheckBox(s.getNombre());
                cb.setUserData(s); // 🔑 guardamos el servicio
                contenedorServiciosFiltro.getChildren().add(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // CARGAR INMUEBLES SIN FILTRO
    // =========================
    private void cargarInmuebles() {
        contenedorInmuebles.getChildren().clear();

        try {
            for (Inmueble i : inmuebleDAO.findDisponibles()) {
                cargarCard(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // APLICAR FILTROS
    // =========================
    @FXML
    private void aplicarFiltros() {
        contenedorInmuebles.getChildren().clear();

        try {
            Double precioMin = txtPrecioMin.getText().isEmpty()
                    ? null : Double.parseDouble(txtPrecioMin.getText());

            Double precioMax = txtPrecioMax.getText().isEmpty()
                    ? null : Double.parseDouble(txtPrecioMax.getText());

            String ubicacion = txtCiudad.getText().trim();
            String busqueda = searchField.getText().trim();

            Integer habitacionesMin = spHabitaciones.getValue();
            Integer capacidad = spCapacidad.getValue();

            LocalDate fechaEntrada = dpFechaEntrada.getValue();
            LocalDate fechaSalida = dpFechaSalida.getValue();

            // Servicios seleccionados
            List<Integer> serviciosIds = new ArrayList<>();
            for (var node : contenedorServiciosFiltro.getChildren()) {
                CheckBox cb = (CheckBox) node;
                if (cb.isSelected()) {
                    ServicioExtra s = (ServicioExtra) cb.getUserData();
                    serviciosIds.add(s.getIdServicio());
                }
            }
            List<Inmueble> resultados = inmuebleDAO.buscar(
                    busqueda,
                    ubicacion,
                    precioMin,
                    precioMax,
                    capacidad,
                    habitacionesMin,
                    fechaEntrada,
                    fechaSalida,
                    serviciosIds
            );

            for (Inmueble i : resultados) {
                cargarCard(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // CARGAR CARD
    // =========================
    private void cargarCard(Inmueble i) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/renthub/InmuebleCardHuesped.fxml")
        );
        VBox card = loader.load();

        CardInmuebleHuespedController controller = loader.getController();
        controller.setInmueble(i);

        contenedorInmuebles.getChildren().add(card);
    }
}


