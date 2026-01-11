package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.renthub.DAO.*;
import org.example.renthub.model.*;
import org.example.renthub.model.enums.EstadoServicio;
import org.example.renthub.model.enums.TipoInmueble;
import org.example.renthub.connection.MySQLConnection;
import org.example.renthub.services.Sesion;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FormInmuebleController {

    // =========================
    // FXML
    // =========================
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCiudad;

    @FXML private ComboBox<TipoInmueble> cmbTipo;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private Spinner<Double> spPrecio;
    @FXML private Spinner<Integer> spHabitaciones;
    @FXML private Spinner<Integer> spCapacidad;

    @FXML private VBox serviciosExtrasContainer;
    @FXML private Label lblImagenes;

    // =========================
    // DAOs
    // =========================
    private final Connection conn = MySQLConnection.getConnection();
    private final InmuebleDAO inmuebleDAO = new InmuebleDAO(conn);
    private final ServicioExtraDAO servicioDAO = new ServicioExtraDAO(conn);
    private final InmuebleServicioDAO inmuebleServicioDAO = new InmuebleServicioDAO(conn);
    private final ImagenInmuebleDAO imagenDAO = new ImagenInmuebleDAO(conn);

    // =========================
    // Estado
    // =========================
    private Inmueble inmueble; // null = crear | != null = editar
    private final List<File> imagenesSeleccionadas = new ArrayList<>();

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {

        cmbTipo.getItems().setAll(TipoInmueble.values());
        cmbEstado.getItems().setAll("DISPONIBLE", "NO_DISPONIBLE");

        spPrecio.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(10, 10000, 50, 5));
        spHabitaciones.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 1));
        spCapacidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 2));

        cargarServiciosExtras();
    }

    // =========================
    // CARGAR SERVICIOS
    // =========================
    private void cargarServiciosExtras() {
        serviciosExtrasContainer.getChildren().clear();

        try {
            for (ServicioExtra s : servicioDAO.findAll()) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/renthub/ServicioExtraCheck.fxml")
                );
                VBox nodo = loader.load();

                CardServicioExtraController controller = loader.getController();
                controller.setServicio(s);

                serviciosExtrasContainer.getChildren().add(nodo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // IMÁGENES
    // =========================
    @FXML
    private void onSeleccionarImagenes() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        List<File> files = fc.showOpenMultipleDialog(txtTitulo.getScene().getWindow());
        if (files != null) {
            imagenesSeleccionadas.addAll(files);
            lblImagenes.setText(imagenesSeleccionadas.size() + " imágenes seleccionadas");
        }
    }

    // =========================
    // GUARDAR
    // =========================
    @FXML
    private void onGuardar() {
        try {
            if (inmueble == null) {
                inmueble = new Inmueble();
            }

            inmueble.setTitulo(txtTitulo.getText());
            inmueble.setDescripcion(txtDescripcion.getText());
            inmueble.setDireccion(txtDireccion.getText());
            inmueble.setCiudad(txtCiudad.getText());
            inmueble.setTipoInmueble(cmbTipo.getValue());
            inmueble.setPrecioNoche(spPrecio.getValue());
            inmueble.setNumeroHabitaciones(spHabitaciones.getValue());
            inmueble.setCapacidad(spCapacidad.getValue());

            Usuario propietario = Sesion.getUsuario();
            inmueble.setPropietario(propietario);

            // INSERT / UPDATE
            if (inmueble.getIdInmueble() == 0) {
                inmuebleDAO.insert(inmueble);
            } else {
                inmuebleDAO.update(inmueble);
                inmuebleServicioDAO.deleteByInmueble(inmueble);
                imagenDAO.deleteByInmueble(inmueble);
            }

            guardarServicios();
            guardarImagenes();

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al guardar el inmueble");
        }
    }

    // =========================
    // SERVICIOS SELECCIONADOS
    // =========================
    private void guardarServicios() throws Exception {
        for (var node : serviciosExtrasContainer.getChildren()) {
            CardServicioExtraController c =
                    (CardServicioExtraController) node.getUserData();

            if (c.isSeleccionado()) {
                inmuebleServicioDAO.addServicio(
                        inmueble,
                        c.getServicio(),
                        0.0,
                        EstadoServicio.DISPONIBLE
                );
            }
        }
    }

    // =========================
    // GUARDAR IMÁGENES
    // =========================
    private void guardarImagenes() throws Exception {
        for (File f : imagenesSeleccionadas) {
            ImagenInmueble img = new ImagenInmueble();
            img.setInmueble(inmueble);
            img.setUrl(f.getAbsolutePath());
            imagenDAO.insert(img);
        }
    }

    // =========================
    // EDITAR
    // =========================
    public void setDatos(Inmueble inmueble) {
        this.inmueble = inmueble;

        txtTitulo.setText(inmueble.getTitulo());
        txtDescripcion.setText(inmueble.getDescripcion());
        txtDireccion.setText(inmueble.getDireccion());
        txtCiudad.setText(inmueble.getCiudad());
        cmbTipo.setValue(inmueble.getTipoInmueble());
        spPrecio.getValueFactory().setValue(inmueble.getPrecioNoche());
        spHabitaciones.getValueFactory().setValue(inmueble.getNumeroHabitaciones());
        spCapacidad.getValueFactory().setValue(inmueble.getCapacidad());

        lblImagenes.setText(inmueble.getImagenes().size() + " imágenes existentes");
    }

    // =========================
    // UTILIDADES
    // =========================
    @FXML
    private void onCancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
