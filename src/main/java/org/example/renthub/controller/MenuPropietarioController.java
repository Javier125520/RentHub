package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.example.renthub.model.Usuario;
import org.example.renthub.services.Sesion;
import java.io.IOException;
import java.net.URL;

/**
 * Controlador principal y maestro de navegación para el Panel del Propietario.
 * Intercambia dinámicamente las herramientas del casero (viviendas, servicios, cobros) en el StackPane central.
 */
public class MenuPropietarioController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private Label lblUsuario;
    @FXML private StackPane contenidoCentral; // Lienzo receptor donde se alternan las subvistas
    @FXML private Button btnMisViviendas;
    @FXML private Button btnServicios;
    @FXML private Button btnReservas;
    @FXML private Button btnResenas;

    /**
     * Inicialización base de JavaFX.
     * Recupera el estado del casero autenticado y configura el listado de propiedades por defecto.
     */
    @FXML
    public void initialize() {
        Usuario usuario = Sesion.getUsuario();
        if (usuario != null) {
            lblUsuario.setText(usuario.getNombre());
        }

        // Enlazamos un disparador para abrir el panel de control del perfil personal al hacer clic en el nombre
        lblUsuario.setStyle("-fx-cursor: hand;");
        lblUsuario.setOnMouseClicked(evt -> abrirPerfil());

        // Vista de inicio por defecto
        verMisViviendas();
    }

    // =========================================================================
    // SECCIÓN DE NAVEGACIÓN ENTRE SUBVISTAS
    // =========================================================================

    @FXML
    private void verMisViviendas() {
        cargarVista("MisViviendasView.fxml");
        marcarActivo(btnMisViviendas);
    }

    @FXML
    private void verServicios() {
        cargarVista("ServiciosView.fxml");
        marcarActivo(btnServicios);
    }

    @FXML
    private void verReservas() {
        cargarVista("ReservasView.fxml");
        marcarActivo(btnReservas);
    }

    @FXML
    private void verResenas() {
        cargarVista("ReseñasView.fxml");
        marcarActivo(btnResenas);
    }

    /** Despliega el panel de control con las métricas y datos personales del arrendador */
    private void abrirPerfil() {
        cargarVista("PerfilPropietario.fxml");

        // Purgamos las iluminaciones de la barra de navegación al salir de las secciones fijas
        btnMisViviendas.getStyleClass().remove("tab-active");
        btnServicios.getStyleClass().remove("tab-active");
        btnReservas.getStyleClass().remove("tab-active");
        btnResenas.getStyleClass().remove("tab-active");
    }

    /**
     * Inyecta de forma asíncrona la estructura FXML solicitada vaciando los nodos anteriores del StackPane.
     */
    private void cargarVista(String fxml) {
        try {
            String path = "/org/example/renthub/" + fxml;
            URL resource = getClass().getResource(path);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            // Seteamos la vista limpiando de raíz el árbol de nodos previo
            contenidoCentral.getChildren().setAll(view);

        } catch (Exception e) {
            System.err.println("Error al cargar vista: " + fxml);
            e.printStackTrace();
        }
    }

    /** Gestión y mantenimiento estético de la clase .tab-active del CSS */
    private void marcarActivo(Button activo) {
        btnMisViviendas.getStyleClass().remove("tab-active");
        btnServicios.getStyleClass().remove("tab-active");
        btnReservas.getStyleClass().remove("tab-active");
        btnResenas.getStyleClass().remove("tab-active");

        activo.getStyleClass().add("tab-active");
    }

    /**
     * Cierra la Sesion y te envia a la pantalla de login
     */
    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/PantallaLogin.fxml")
            );
            // Desmantelamos el menú completo sobreescribiendo la raíz de la escena del escenario principal
            contenidoCentral.getScene().setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

