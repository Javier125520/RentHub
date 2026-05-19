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
import org.example.renthub.services.Sesion;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal para el formulario de Creación y Edición de Inmuebles.
 * Gestiona el guardado transaccional multi-tabla bajo Active Record (Inmuebles, Imágenes y Servicios).
 */
public class FormInmuebleController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCiudad;
    @FXML private ComboBox<TipoInmueble> cmbTipo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Spinner<Double> spPrecio;
    @FXML private Spinner<Integer> spHabitaciones;
    @FXML private Spinner<Integer> spCapacidad;
    @FXML private VBox serviciosExtrasContainer; // Contenedor vertical donde se inyectan las tarjetas de selección
    @FXML private Label lblImagenes;

    // =========================================================================
    // ATRIBUTOS DE OPERACIÓN
    // =========================================================================
    private InmuebleDAO inmueble; // Objeto Active Record operativo
    private final List<File> imagenesSeleccionadas = new ArrayList<>();

    /**
     * Inicialización por defecto de JavaFX.
     * Configura selectores y define las factorías de control numérico seguro para los Spinners.
     */
    @FXML
    public void initialize() {
        cmbTipo.getItems().setAll(TipoInmueble.values());
        cmbEstado.getItems().setAll("DISPONIBLE", "NO DISPONIBLE");

        // Formateo de rangos: (mínimo, máximo, valor_por_defecto, pasos)
        spPrecio.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(10, 10000, 50, 5));
        spHabitaciones.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 1));
        spCapacidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 2));

        // Inyecta dinámicamente el catálogo total de servicios extras disponibles en el VBox
        cargarServiciosExtras();
    }

    /**
     * Carga de manera iterativa el listado global de servicios extras transformándolos en nodos gráficos.
     */
    private void cargarServiciosExtras() {
        serviciosExtrasContainer.getChildren().clear();
        try {
            for (ServicioExtra s : ServicioExtraDAO.findAll()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/renthub/ServicioExtraCard.fxml"));
                VBox nodo = loader.load();

                CardServicioExtraController controller = loader.getController();
                controller.setServicio(s);

                // Guardamos la referencia del controlador dentro del nodo gráfico usando el UserData de JavaFX
                nodo.setUserData(controller);
                serviciosExtrasContainer.getChildren().add(nodo);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre un explorador de archivos nativo de la máquina del usuario para capturar múltiples imágenes adjuntas.
     */
    @FXML
    private void onSeleccionarImagenes() {
        FileChooser fc = new FileChooser();
        // Filtro estricto para aceptar únicamente extensiones de imagen estándar
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        List<File> files = fc.showOpenMultipleDialog(txtTitulo.getScene().getWindow());
        if (files != null) {
            imagenesSeleccionadas.addAll(files);
            lblImagenes.setText(imagenesSeleccionadas.size() + " imágenes seleccionadas");
        }
    }

    /**
     * Valida, empaqueta y guarda la información del formulario discriminando entre una Inserción o una Modificación.
     */
    @FXML
    private void onGuardar() {
        try {
            if (inmueble == null) {
                inmueble = new InmuebleDAO();
            }

            // Recolección y mapeado de campos de texto e inputs hacia la instancia Active Record
            inmueble.setTitulo(txtTitulo.getText());
            inmueble.setDescripcion(txtDescripcion.getText());
            inmueble.setDireccion(txtDireccion.getText());
            inmueble.setCiudad(txtCiudad.getText());
            inmueble.setTipoInmueble(cmbTipo.getValue());
            inmueble.setPrecioNoche(spPrecio.getValue());
            inmueble.setNumeroHabitaciones(spHabitaciones.getValue());
            inmueble.setCapacidad(spCapacidad.getValue());
            inmueble.setDisponible("DISPONIBLE".equals(cmbEstado.getValue()));
            inmueble.setPropietario(Sesion.getUsuario()); // Vincula el inmueble al casero autenticado

            // Lógica Active Record: Si el ID es 0, no existe en BD (INSERT), si ya tiene ID (UPDATE)
            if (inmueble.getIdInmueble() == 0) {
                inmueble.insert();
            } else {
                inmueble.update();
                // Si estamos editando, limpiamos previamente las relaciones antiguas multi-tabla para evitar duplicados
                InmuebleServicioDAO.deleteByInmueble(inmueble);
            }

            // Guardado secuencial y enlazado de las tablas hijas dependientes
            guardarServicios();
            guardarImagenes();

            cerrar();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al guardar el inmueble");
        }
    }

    /** Recorre el VBox de servicios e inserta las relaciones marcadas vinculándolas al ID del inmueble */
    private void guardarServicios() throws Exception {
        for (var node : serviciosExtrasContainer.getChildren()) {
            CardServicioExtraController c = (CardServicioExtraController) node.getUserData();
            if (c.isSeleccionado()) {
                InmuebleServicioDAO relacion = new InmuebleServicioDAO();
                relacion.setInmueble(this.inmueble);
                relacion.setServicio(c.getServicio());
                relacion.setPrecioAdicional(c.getPrecio());
                relacion.setEstado(EstadoServicio.valueOf(String.valueOf(c.getEstado())));
                relacion.insert();
            }
        }
    }

    /** Mapea las rutas absolutas de los ficheros del disco guardándolas en MySQL */
    private void guardarImagenes() throws Exception {
        for (File f : imagenesSeleccionadas) {
            ImagenInmuebleDAO imgDAO = new ImagenInmuebleDAO();
            imgDAO.setInmueble(this.inmueble);
            imgDAO.setUrl(f.getAbsolutePath());
            imgDAO.insert();
        }
    }

    /** Sincroniza visualmente los servicios del alojamiento al abrir en Modo Edición */
    private void marcarServiciosSeleccionados() throws SQLException {
        List<InmuebleServicio> serviciosInmueble = InmuebleServicioDAO.findByInmueble(inmueble);
        for (var node : serviciosExtrasContainer.getChildren()) {
            CardServicioExtraController c = (CardServicioExtraController) node.getUserData();
            for (InmuebleServicio is : serviciosInmueble) {
                if (is.getServicio().getIdServicio() == c.getServicio().getIdServicio()) {
                    c.marcarSeleccionado(is.getPrecioAdicional(), is.getEstado());
                }
            }
        }
    }

    /**
     * Método externo invocado por reflexión al abrir la ventana.
     * Hidrata la vista si recibe un Inmueble existente (Modo Edición) o prepara un objeto vacío (Modo Creación).
     */
    public void setDatos(Inmueble inmuebleRecibido) throws SQLException {
        if (inmuebleRecibido == null) {
            this.inmueble = new InmuebleDAO();
        } else if (inmuebleRecibido instanceof InmuebleDAO) {
            this.inmueble = (InmuebleDAO) inmuebleRecibido;
        } else {
            this.inmueble = new InmuebleDAO(inmuebleRecibido); // Envoltura en el objeto activo
        }

        // Volcado de memoria del modelo hacia los controles gráficos
        txtTitulo.setText(inmueble.getTitulo());
        txtDescripcion.setText(inmueble.getDescripcion());
        txtDireccion.setText(inmueble.getDireccion());
        txtCiudad.setText(inmueble.getCiudad());
        cmbTipo.setValue(inmueble.getTipoInmueble());
        spPrecio.getValueFactory().setValue(inmueble.getPrecioNoche());
        spHabitaciones.getValueFactory().setValue(inmueble.getNumeroHabitaciones());
        spCapacidad.getValueFactory().setValue(inmueble.getCapacidad());
        cmbEstado.setValue(inmueble.isDisponible() ? "DISPONIBLE" : "NO DISPONIBLE");

        lblImagenes.setText(inmueble.getImagenes().size() + " imágenes existentes");
        marcarServiciosSeleccionados();
    }

    @FXML private void onCancelar() { cerrar(); }
    private void cerrar() { ((Stage) txtTitulo.getScene().getWindow()).close(); }
    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}