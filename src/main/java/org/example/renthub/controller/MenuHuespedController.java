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
 * Controlador maestro de navegación para el Panel Principal del Huésped.
 * Gestiona el intercambio dinámico de vistas hijas dentro del contenedor StackPane central.
 */
public class MenuHuespedController {

    // =========================================================================
    // COMPONENTES FXML
    // =========================================================================
    @FXML private Label lblUsuario;
    @FXML private StackPane contenidoCentral; // 💡 Contenedor donde se "inyectan" las vistas dinámicas
    @FXML private Button btnViviendas;
    @FXML private Button btnMisReservas;
    @FXML private Button btnMisResenas;

    private Usuario usuario;

    /**
     * Inicialización de JavaFX.
     * Carga el estado del cliente autenticado y define los oyentes del perfil.
     */
    @FXML
    public void initialize() {
        // Recuperamos los datos de la sesión para maquetar el Header
        usuario = Sesion.getUsuario();
        if (usuario != null) {
            lblUsuario.setText(usuario.getNombre());
        }

        // Convertimos el nombre en un hipervínculo interactivo para abrir los datos de cuenta
        lblUsuario.setStyle("-fx-cursor: hand;");
        lblUsuario.setOnMouseClicked(evt -> abrirPerfil());

        // Cargamos la cartelera de viviendas disponibles como vista por defecto al arrancar
        verViviendas();
    }

    /** Intercambia la zona de trabajo inyectando el listado exploratorio de viviendas */
    @FXML
    private void verViviendas() {
        cargarVista("ViviendasView.fxml");
        marcarActivo(btnViviendas); // Actualiza los estilos de las pestañas
    }

    /** Intercambia la zona de trabajo inyectando el historial de reservas solicitadas */
    @FXML
    private void verReservas() {
        cargarVista("MisReservasView.fxml");
        marcarActivo(btnMisReservas);
    }

    /** Intercambia la zona de trabajo inyectando el historial de comentarios publicados */
    @FXML
    private void verReseñas() {
        cargarVista("MisReseñasView.fxml");
        marcarActivo(btnMisResenas);
    }

    /** Despliega el panel de control de datos personales del perfil */
    private void abrirPerfil() {
        cargarVista("PerfilHuesped.fxml");

        // Limpiamos los estilos de la barra de pestañas ya que el perfil es una sección independiente
        btnViviendas.getStyleClass().remove("tab-active");
        btnMisReservas.getStyleClass().remove("tab-active");
        btnMisResenas.getStyleClass().remove("tab-active");
    }

    /**
     * Cierra la sesión destruyendo los datos de memoria y regresando al formulario de acceso.
     */
    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/renthub/PantallaLogin.fxml")
            );
            // Sobreescribimos el nodo raíz de la escena para desmantelar el menú completo de golpe
            contenidoCentral.getScene().setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Conmutador estético de clases de diseño CSS.
     * Remueve la iluminación de las pestañas previas y tiñe la opción seleccionada.
     */
    private void marcarActivo(Button activo) {
        btnViviendas.getStyleClass().remove("tab-active");
        btnMisReservas.getStyleClass().remove("tab-active");
        btnMisResenas.getStyleClass().remove("tab-active");

        activo.getStyleClass().add("tab-active");
    }

    /**
     * MOTOR DE INYECCIÓN GRÁFICA ASÍNCRONA.
     * Carga el FXML solicitado y reemplaza la totalidad de los nodos hijos del StackPane central.
     * @param fxml El nombre de la subvista a cargar.
     */
    private void cargarVista(String fxml) {
        try {
            String path = "/org/example/renthub/" + fxml;
            URL resource = getClass().getResource(path);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            // Interceptamos si la vista requiere inyección de datos contextuales desde el controlador maestro
            if (fxml.equals("MisReseñasView.fxml")) {
                MisResenasViewController controller = loader.getController();
                controller.setUsuario(usuario); // Sincroniza al usuario logueado en la subvista
            }

            // 💡 SOLUCIÓN: Limpiamos el contenedor central e inyectamos la nueva estructura de la vista
            contenidoCentral.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
