package com.dam.cicd.controladores;

import com.dam.cicd.entidades.*;
import com.dam.cicd.utilidades.JPAUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Controlador principal para la gestión del panel del huésped.
 * Facilita la visualización de alojamientos disponibles, la creación de nuevas reservas
 * y el seguimiento de las reservas históricas del usuario.
 */
public class PanelHuespedController {

    private Huesped huespedLogueado;

    @FXML private Label lblMensaje;
    @FXML private DatePicker dateInicio;
    @FXML private DatePicker dateFin;

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colAlojamiento;
    @FXML private TableColumn<Reserva, String> colFechas;

    @FXML private TableView<Alojamiento> tablaAlojamientos;
    @FXML private TableColumn<Alojamiento, String> colFoto;
    @FXML private TableColumn<Alojamiento, String> colNombreAlojamiento;
    @FXML private TableColumn<Alojamiento, String> colDireccion;
    @FXML private TableColumn<Alojamiento, String> colAnfitrion;

    /**
     * Inicializa los componentes de la interfaz, configurando los factories de celdas
     * para las tablas de reservas y alojamientos, y cargando el catálogo disponible.
     */
    @FXML
    public void initialize() {
        tablaAlojamientos.setFixedCellSize(100.0);

        colAlojamiento.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAlojamiento().getNombre()));

        colFechas.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFechaEntrada() + " al " + data.getValue().getFechaSalida()));

        colFoto.setCellValueFactory(new PropertyValueFactory<>("fotoUrl"));
        colFoto.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isBlank()) {
                    setGraphic(null);
                } else {
                    Image img = new Image(url, 130, 90, true, true);
                    imageView.setImage(img);
                    setGraphic(imageView);
                }
            }
        });

        colNombreAlojamiento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colAnfitrion.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAnfitrion().getNombre()));

        cargarTodosLosAlojamientos();
    }

    /**
     * Establece el huésped que ha iniciado sesión y carga sus reservas actuales.
     * @param huesped El usuario logueado en el sistema.
     */
    public void setUsuario(Huesped huesped) {
        this.huespedLogueado = huesped;
        cargarMisReservas();
    }

    /**
     * Consulta la base de datos para obtener y listar todas las reservas realizadas por el huésped logueado.
     */
    private void cargarMisReservas() {
        if (huespedLogueado == null) return;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Reserva> reservas = em.createQuery(
                            "SELECT r FROM Reserva r WHERE r.huesped.id = :idHuesped", Reserva.class)
                    .setParameter("idHuesped", huespedLogueado.getIdHuesped())
                    .getResultList();
            tablaReservas.setItems(FXCollections.observableArrayList(reservas));
        } finally {
            em.close();
        }
    }

    /**
     * Carga el catálogo completo de alojamientos disponibles para reserva desde la base de datos.
     */
    private void cargarTodosLosAlojamientos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Alojamiento> todos = em.createQuery("SELECT a FROM Alojamiento a", Alojamiento.class)
                    .getResultList();
            tablaAlojamientos.setItems(FXCollections.observableArrayList(todos));
        } finally {
            em.close();
        }
    }

    /**
     * Valida los datos del formulario y persiste una nueva reserva en la base de datos.
     * Gestiona las transacciones JPA y calcula el coste total según los días seleccionados.
     */
    @FXML
    private void handleReservar() {
        if (huespedLogueado == null) {
            lblMensaje.setText("Error: Debes iniciar sesión.");
            return;
        }

        Alojamiento seleccionado = tablaAlojamientos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Por favor, selecciona un alojamiento.");
            return;
        }
        if (dateInicio.getValue() == null || dateFin.getValue() == null) {
            lblMensaje.setText("Debes seleccionar fecha de entrada y salida.");
            return;
        }
        if (dateFin.getValue().isBefore(dateInicio.getValue())) {
            lblMensaje.setText("La fecha de salida debe ser posterior a la de entrada.");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Huesped huespedGestionado = em.find(Huesped.class, huespedLogueado.getIdHuesped());
            Alojamiento alojamientoGestionado = em.find(Alojamiento.class, seleccionado.getIdAlojamiento());

            long numNoches = ChronoUnit.DAYS.between(dateInicio.getValue(), dateFin.getValue());
            if (numNoches == 0) numNoches = 1;

            BigDecimal precioPorDia = alojamientoGestionado.getPrecioDia();
            BigDecimal total = precioPorDia.multiply(new BigDecimal(numNoches));

            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setAlojamiento(alojamientoGestionado);
            nuevaReserva.setHuesped(huespedGestionado);
            nuevaReserva.setFechaEntrada(dateInicio.getValue());
            nuevaReserva.setFechaSalida(dateFin.getValue());
            nuevaReserva.setPrecioTotal(total);
            nuevaReserva.setEstado("Confirmada");

            em.persist(nuevaReserva);
            em.getTransaction().commit();

            lblMensaje.setText("¡Reserva realizada! Total: " + total + "€");
            tablaAlojamientos.getSelectionModel().clearSelection();
            dateInicio.setValue(null);
            dateFin.setValue(null);
            cargarMisReservas();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            lblMensaje.setText("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Cierra la sesión actual y redirige al usuario a la pantalla de login.
     */
    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/LoginVista.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login - Roomly");
            stage.setScene(new Scene(root));
            stage.show();

            Stage stageActual = (Stage) lblMensaje.getScene().getWindow();
            stageActual.close();
        } catch (Exception e) {
            lblMensaje.setText("Error al cerrar sesión: " + e.getMessage());
        }
    }
}