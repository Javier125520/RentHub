package org.example.renthub.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.renthub.DAO.InmuebleDAO;
import org.example.renthub.DAO.ServicioExtraDAO;
import org.example.renthub.model.Inmueble;
import org.example.renthub.model.ServicioExtra;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la cartelera general de exploración de inmuebles del Huésped[cite: 240].
 * Permite realizar búsquedas por texto e inyectar de forma combinada filtros dinámicos (precios, número de plazas,
 * intervalos de fechas libres de ocupación y características específicas de servicios)[cite: 240].
 */
public class ViviendasViewController {

    // =========================================================================
    // COMPONENTES VISUALES ENLAZADOS AL FXML
    // =========================================================================
    @FXML private TextField txtPrecioMin;
    @FXML private TextField txtPrecioMax;
    @FXML private TextField txtCiudad;
    @FXML private Spinner<Integer> spHabitaciones;
    @FXML private Spinner<Integer> spCapacidad;
    @FXML private TextField searchField;

    @FXML private DatePicker dpFechaEntrada;
    @FXML private DatePicker dpFechaSalida;

    @FXML private VBox panelFiltros;                 // Panel desplegable que contiene las opciones de filtrado [cite: 241]
    @FXML private FlowPane contenedorInmuebles;        // Rejilla elástica receptora de las tarjetas de viviendas [cite: 252]
    @FXML private FlowPane contenedorServiciosFiltro;  // Caja horizontal donde se inyectan las casillas de verificación [cite: 247]

    // Inicialización de DAOs de consulta estáticos operativos
    private final InmuebleDAO inmuebleDAO = new InmuebleDAO();
    private final ServicioExtraDAO servicioDAO = new ServicioExtraDAO();

    /**
     * Método del ciclo de vida automático de JavaFX[cite: 240].
     * Configura los rangos de los controles numéricos e hidrata las colecciones de la base de datos[cite: 240].
     */
    @FXML
    public void initialize() {
        // Inicialización obligatoria de factorías para restringir valores válidos en los Spinners
        spHabitaciones.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0)
        );
        spCapacidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1)
        );

        // Carga secuencial de la interfaz gráfica
        cargarServiciosFiltro();
        cargarInmuebles();
    }

    // =========================================================================
    // ACCIONES DE EXPANSIÓN Y DESPLEGADO DE LA INTERFAZ
    // =========================================================================

    /**
     * Conmuta la visualización del panel de filtros adicionales.
     * Utiliza la propiedad managed de JavaFX para liberar espacio de pantalla adaptativamente al ocultarse.
     */
    @FXML
    private void abrirFiltros() {
        boolean visible = panelFiltros.isVisible();
        panelFiltros.setVisible(!visible);
        panelFiltros.setManaged(!visible); // Evita que un nodo oculto descuadre el layout [cite: 241]
    }

    /**
     * Recupera el catálogo completo de prestaciones de la BD y genera checkboxes al vuelo en el FlowPane[cite: 247].
     */
    private void cargarServiciosFiltro() {
        try {
            contenedorServiciosFiltro.getChildren().clear();

            for (ServicioExtra s : servicioDAO.findAll()) {
                CheckBox cb = new CheckBox(s.getNombre());

                // 🔑 CRÍTICO: Almacenamos el modelo de datos en el nodo gráfico mediante su UserData para su posterior rescate [cite: 247]
                cb.setUserData(s);
                contenedorServiciosFiltro.getChildren().add(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga el tablón inicial trayendo la totalidad de inmuebles disponibles en el sistema.
     */
    private void cargarInmuebles() {
        contenedorInmuebles.getChildren().clear();

        try {
            // Invocación Active Record para traer únicamente propiedades marcadas como disponibles (True)
            for (Inmueble i : inmuebleDAO.findDisponibles()) {
                cargarCard(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recolecta minuciosamente las entradas de texto y estados de los nodos, procesa conversiones numéricas de seguridad
     * e invoca al buscador avanzado por parámetros dinámicos de MySQL.
     */
    @FXML
    private void aplicarFiltros() {
        // Purgamos la cuadrícula de viviendas anteriores
        contenedorInmuebles.getChildren().clear();

        try {
            // Evaluamos y parseamos de forma segura las cajas de texto controlando excepciones por cadenas vacías
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

            // Recorremos los nodos del FlowPane para recolectar las IDs de los servicios marcados
            List<Integer> serviciosIds = new ArrayList<>();
            for (var node : contenedorServiciosFiltro.getChildren()) {
                CheckBox cb = (CheckBox) node;
                if (cb.isSelected()) {
                    // Rescatamos el objeto del UserData inyectado al inicializar la vista [cite: 247]
                    ServicioExtra s = (ServicioExtra) cb.getUserData();
                    serviciosIds.add(s.getIdServicio());
                }
            }

            // 🔥 Ejecutamos la consulta dinámica adaptativa multicapa en base de datos
            List<Inmueble> resultados = InmuebleDAO.buscar(
                    busqueda, ubicacion, precioMin, precioMax, capacidad,
                    habitacionesMin, fechaEntrada, fechaSalida, serviciosIds
            );

            // Pintamos los registros resultantes de la búsqueda
            for (Inmueble i : resultados) {
                cargarCard(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Instancia de manera asíncrona la sub-plantilla FXML para el card del inmueble inyectándole su respectivo modelo.
     * @param i El inmueble a renderizar en formato de tarjeta.
     */
    private void cargarCard(Inmueble i) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/renthub/InmuebleCardHuesped.fxml")
        );
        VBox card = loader.load();

        // Extraemos el controlador hijo asignándole su información correspondiente
        CardInmuebleHuespedController controller = loader.getController();
        controller.setInmueble(i);

        contenedorInmuebles.getChildren().add(card);
    }
}
