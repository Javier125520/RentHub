package org.example.renthub.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.InmuebleDAO;
import org.example.renthub.DAO.ServicioExtraDAO;
import org.example.renthub.model.Inmueble;
import org.example.renthub.utils.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MenuHuespedController implements Initializable {

    // --- Atributos FXML ---
    @FXML private TextField txtBuscar;
    @FXML private VBox panelFiltros;
    @FXML private TextField filtroCiudad;
    @FXML private TextField filtroCapacidad;
    @FXML private TextField filtroHabitaciones;
    @FXML private TextField filtroPrecioMin;
    @FXML private TextField filtroPrecioMax;
    @FXML private FlowPane contenedorServicios;
    @FXML private FlowPane contenedorInmuebles;

    // --- DAOs y Datos ---
    private InmuebleDAO inmuebleDAO; // Renombrado de Service a DAO
    private ServicioExtraDAO servicioExtraDAO; // Renombrado de Service a DAO
    private List<Inmueble> todosLosInmuebles; // Lista maestra para no ir a la BD constantemente

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializamos los DAOs (en una app real, se inyectarían)
        this.inmuebleDAO = new InmuebleDAO();
        this.servicioExtraDAO = new ServicioExtraDAO();

        cargarDatosIniciales();
        cargarServiciosEnFiltros();
    }

    private void cargarDatosIniciales() {
        // 1. Obtenemos todos los inmuebles del DAO una sola vez
        this.todosLosInmuebles = InmuebleDAO.getAll();
        // 2. Mostramos todos los inmuebles en la UI
        mostrarInmuebles(this.todosLosInmuebles);
    }

    // --- Métodos para la barra de búsqueda y filtros ---

    @FXML
    void buscar(ActionEvent event) {
        String terminoBusqueda = txtBuscar.getText().toLowerCase().trim();
        System.out.println("Iniciando búsqueda con término: " + terminoBusqueda);

        if (terminoBusqueda.isEmpty()) {
            mostrarInmuebles(todosLosInmuebles);
            return;
        }

        List<Inmueble> resultados = todosLosInmuebles.stream()
                .filter(inmueble -> inmueble.getTitulo().toLowerCase().contains(terminoBusqueda) ||
                        inmueble.getCiudad().toLowerCase().contains(terminoBusqueda))
                .collect(Collectors.toList());

        mostrarInmuebles(resultados);
    }

    @FXML
    void toggleFiltros(ActionEvent event) {
        boolean isVisible = panelFiltros.isVisible();
        panelFiltros.setVisible(!isVisible);
        panelFiltros.setManaged(!isVisible);
    }

    @FXML
    void aplicarFiltros(ActionEvent event) {
        System.out.println("Aplicando filtros...");
        List<Inmueble> resultadosFiltrados = todosLosInmuebles.stream()
                .filter(this::cumpleConFiltros)
                .collect(Collectors.toList());

        mostrarInmuebles(resultadosFiltrados);
        toggleFiltros(null); // Ocultar panel de filtros
    }

    private boolean cumpleConFiltros(Inmueble inmueble) {
        // Filtro por Ciudad
        if (!filtroCiudad.getText().isEmpty() && !inmueble.getCiudad().equalsIgnoreCase(filtroCiudad.getText().trim())) {
            return false;
        }
        // Filtro por Capacidad
        if (!filtroCapacidad.getText().isEmpty()) {
            try {
                int capacidad = Integer.parseInt(filtroCapacidad.getText());
                if (inmueble.getCapacidad() < capacidad) return false;
            } catch (NumberFormatException e) { /* Ignorar si no es número */ }
        }
        // Filtro por Habitaciones
        if (!filtroHabitaciones.getText().isEmpty()) {
            try {
                int habitaciones = Integer.parseInt(filtroHabitaciones.getText());
                if (inmueble.getNumeroHabitaciones() < habitaciones) return false;
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        // Filtro por Precio Mínimo
        if (!filtroPrecioMin.getText().isEmpty()) {
            try {
                double precioMin = Double.parseDouble(filtroPrecioMin.getText());
                if (inmueble.getPrecioNoche() < precioMin) return false;
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        // Filtro por Precio Máximo
        if (!filtroPrecioMax.getText().isEmpty()) {
            try {
                double precioMax = Double.parseDouble(filtroPrecioMax.getText());
                if (inmueble.getPrecioNoche() > precioMax) return false;
            } catch (NumberFormatException e) { /* Ignorar */ }
        }
        // Filtro por Servicios
        List<String> serviciosSeleccionados = getServiciosSeleccionados();
        if (!serviciosSeleccionados.isEmpty()) {
            // Usamos el DAO para obtener los servicios del inmueble
            List<String> serviciosDelInmueble = inmuebleDAO.getServiciosByInmuebleId(inmueble.getId());
            if (!serviciosDelInmueble.containsAll(serviciosSeleccionados)) {
                return false;
            }
        }
        return true; // Si pasa todos los filtros
    }

    @FXML
    void limpiarFiltros(ActionEvent event) {
        System.out.println("Limpiando filtros.");
        filtroCiudad.clear();
        filtroCapacidad.clear();
        filtroHabitaciones.clear();
        filtroPrecioMin.clear();
        filtroPrecioMax.clear();

        contenedorServicios.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .forEach(node -> ((CheckBox) node).setSelected(false));

        mostrarInmuebles(todosLosInmuebles); // Mostrar todos los inmuebles de nuevo
    }

    // --- Métodos de navegación del menú lateral ---

    @FXML void irInicio(ActionEvent event) {
        System.out.println("Recargando Inicio...");
        limpiarFiltros(null);
        txtBuscar.clear();
        cargarDatosIniciales();
    }

    @FXML void irMisReservas(ActionEvent event) {
        System.out.println("Navegando a Mis Reservas...");
        SceneManager.loadScene((Node) event.getSource(), "Huesped/MisReservas.fxml");
    }

    @FXML void irMisReseñas(ActionEvent event) {
        System.out.println("Navegando a Mis Reseñas...");
        SceneManager.loadScene((Node) event.getSource(), "Huesped/MisResenas.fxml");
    }

    @FXML void irPerfil(ActionEvent event) {
        System.out.println("Navegando a Mi Perfil...");
        SceneManager.loadScene((Node) event.getSource(), "Huesped/PerfilHuesped.fxml");
    }

    @FXML void cerrarSesion(ActionEvent event) {
        System.out.println("Cerrando sesión...");
        SceneManager.loadScene((Node) event.getSource(), "Login.fxml");
    }

    // --- Métodos auxiliares (lógica interna) ---

    private void mostrarInmuebles(List<Inmueble> lista) {
        System.out.println("Mostrando " + lista.size() + " inmuebles.");
        contenedorInmuebles.getChildren().clear();

        for (Inmueble inmueble : lista) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/renthub/Huesped/InmuebleCard.fxml"));
                Node card = loader.load();
                InmuebleCardController controller = loader.getController();
                // Pasamos 'this' para que la tarjeta pueda comunicarse de vuelta con este controlador
                controller.setData(inmueble, this);
                contenedorInmuebles.getChildren().add(card);
            } catch (IOException e) {
                System.err.println("Error al cargar la tarjeta de inmueble.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Método público que puede ser llamado desde InmuebleCardController
     * para mostrar la vista de detalles de un inmueble específico.
     */
    public void mostrarDetallesInmueble(Inmueble inmueble) {
        System.out.println("Mostrando detalles para el inmueble: " + inmueble.getTitulo());
        // Aquí iría la lógica para cambiar de vista a la de detalles del inmueble,
        // pasando el objeto 'inmueble' al nuevo controlador.
        // Ejemplo:
        // try {
        //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/renthub/Huesped/DetalleInmueble.fxml"));
        //     Parent root = loader.load();
        //
        //     DetalleInmuebleController controller = loader.getController();
        //     controller.initData(inmueble); // Un método para pasarle el inmueble
        //
        //     Stage stage = (Stage) contenedorInmuebles.getScene().getWindow();
        //     stage.setScene(new Scene(root));
        //
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }


    private void cargarServiciosEnFiltros() {
        contenedorServicios.getChildren().clear();
        // Usamos el DAO para obtener los nombres de los servicios
        List<String> serviciosDisponibles = ServicioExtraDAO.findAllServiceNames();

        for (String nombreServicio : serviciosDisponibles) {
            CheckBox checkBox = new CheckBox(nombreServicio);
            checkBox.getStyleClass().add("checkbox-filter");
            contenedorServicios.getChildren().add(checkBox);
        }
    }

    private List<String> getServiciosSeleccionados() {
        return contenedorServicios.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());
    }
}
