package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.renthub.DAO.ServicioExtraDAO;
import org.example.renthub.model.ServicioExtra;

public class FormServicioController {

    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;

    @FXML private Button btnGuardar;

    private ServicioExtra servicio; // null = añadir | != null = editar
    private final ServicioExtraDAO servicioDAO = new ServicioExtraDAO();

    /* =========================
       INIT
       ========================= */

    @FXML
    public void initialize() {
        // Por defecto: modo añadir
        configurarModoCrear();
    }

    /* =========================
       CONFIGURACIÓN MODOS
       ========================= */

    public void setDatos(ServicioExtra servicio) {
        this.servicio = servicio;

        if (servicio != null) {
            configurarModoEditar();
            cargarDatos();
        }
    }

    private void configurarModoCrear() {
        lblTitulo.setText("Añadir Nuevo Servicio");
        lblSubtitulo.setText("Crea un nuevo servicio global que podrás asociar a tus propiedades");
        btnGuardar.setText("Añadir Servicio");
    }

    private void configurarModoEditar() {
        lblTitulo.setText("Editar Servicio");
        lblSubtitulo.setText("Modifica los datos del servicio");
        btnGuardar.setText("Guardar Cambios");
    }

    private void cargarDatos() {
        txtNombre.setText(servicio.getNombre());
        txtDescripcion.setText(servicio.getDescripcion());
    }

    /* =========================
       ACCIONES
       ========================= */

    @FXML
    private void onGuardar() {
        try {
            if (txtNombre.getText().isBlank()) {
                mostrarError("El nombre del servicio es obligatorio");
                return;
            }

            if (servicio == null) {
                servicio = new ServicioExtra();
            }

            servicio.setNombre(txtNombre.getText());
            servicio.setDescripcion(txtDescripcion.getText());

            if (servicio.getIdServicio() == 0) {
                servicioDAO.insert(servicio);
            } else {
                servicioDAO.update(servicio);
            }

            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al guardar el servicio");
        }
    }

    @FXML
    private void onCerrar() {
        cerrar();
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    /* =========================
       UTILIDADES
       ========================= */

    private void mostrarError(String mensaje) {
        System.out.println("ERROR: " + mensaje);
        // Aquí puedes meter un Alert si quieres
    }
}
