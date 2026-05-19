package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.renthub.DAO.ServicioExtraDAO;
import org.example.renthub.model.ServicioExtra;

/**
 * Controlador para la creación y edición de las opciones del catálogo global de Servicios Extras.
 * Permite al casero añadir servicios reutilizables (ej: Piscina, WiFi) para luego indexarlos en sus inmuebles.
 */
public class FormServicioController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;

    // Instancia Active Record operativa
    private ServicioExtraDAO servicio;

    /**
     * Inicialización base por defecto de JavaFX.
     * Configura la ventana inicialmente en modo de creación.
     */
    @FXML
    public void initialize() {
        configurarModoCrear();
    }

    /**
     * Intercepta y mapea un servicio enviado desde el exterior convirtiendo la vista a Modo Edición.
     * @param servicioRecibido El servicio que se desea rectificar.
     */
    public void setDatos(ServicioExtra servicioRecibido) {
        if (servicioRecibido != null) {
            // Envolvemos el modelo en su DAO de persistencia activo
            this.servicio = new ServicioExtraDAO(servicioRecibido);

            configurarModoEditar();
            cargarDatos();
        }
    }

    /** Prepara las etiquetas textuales predeterminadas para dar de alta un registro */
    private void configurarModoCrear() {
        lblTitulo.setText("Añadir Nuevo Servicio");
        lblSubtitulo.setText("Crea un nuevo servicio global que podrás asociar a tus propiedades");
        btnGuardar.setText("Añadir Servicio");
    }

    /** Altera los encabezados de la ventana para operaciones de modificación */
    private void configurarModoEditar() {
        lblTitulo.setText("Editar Servicio");
        lblSubtitulo.setText("Modifica los datos del servicio");
        btnGuardar.setText("Guardar Cambios");
    }

    /** Vuelca las propiedades textuales de la base de datos sobre los campos de entrada */
    private void cargarDatos() {
        txtNombre.setText(servicio.getNombre());
        txtDescripcion.setText(servicio.getDescripcion());
    }

    /**
     * Procesa y valida el guardado seguro del servicio distinguiendo entre altas de catálogo o modificaciones.
     */
    @FXML
    private void onGuardar() {
        try {
            // Validación perimetral obligatoria de campos vacíos
            if (txtNombre.getText().isBlank()) {
                mostrarError("El nombre del servicio es obligatorio");
                return;
            }

            if (servicio == null) {
                servicio = new ServicioExtraDAO();
            }

            servicio.setNombre(txtNombre.getText());
            servicio.setDescripcion(txtDescripcion.getText());

            // Lógica Active Record: si el ID es 0, es nuevo (insert), si ya tiene ID numérico (update)
            if (servicio.getIdServicio() == 0) {
                servicio.insert();
            } else {
                servicio.update();
            }

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error crítico al guardar el servicio");
        }
    }

    @FXML private void onCerrar() { cerrar(); }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String msg) {
        System.err.println(msg); // Aquí puedes acoplar un Alert modal si lo prefieres
    }
}