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
import javafx.beans.property.SimpleStringProperty;

/**
 * Controlador principal para la gestión del panel del huésped.
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

    @FXML
    public void initialize() {
        tablaAlojamientos.setFixedCellSize(100.0);

        // Configuración de columnas usando métodos auxiliares para evitar código duplicado
        colAlojamiento.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAlojamiento().getNombre()));
        colFechas.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFechaEntrada() + " al " + d.getValue().getFechaSalida()));

        setupColumn(colNombreAlojamiento, "nombre");
        setupColumn(colDireccion, "direccion");
        colAnfitrion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAnfitrion().getNombre()));

        setupFotoColumn();

        cargarTodosLosAlojamientos();
    }


    private <T> void setupColumn(TableColumn<T, String> column, String property) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
    }

    private void setupFotoColumn() {
        colFoto.setCellValueFactory(new PropertyValueFactory<>("fotoUrl"));
        colFoto.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isBlank()) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(url, 130, 90, true, true));
                    setGraphic(imageView);
                }
            }
        });
    }

    public void setUsuario(Huesped huesped) {
        this.huespedLogueado = huesped;
        cargarMisReservas();
    }

    private void cargarMisReservas() {
        if (huespedLogueado == null) return;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Reserva> reservas = em.createQuery("SELECT r FROM Reserva r WHERE r.huesped.id = :idHuesped", Reserva.class)
                    .setParameter("idHuesped", huespedLogueado.getIdHuesped())
                    .getResultList();
            tablaReservas.setItems(FXCollections.observableArrayList(reservas));
        } finally {
            em.close();
        }
    }

    private void cargarTodosLosAlojamientos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Alojamiento> todos = em.createQuery("SELECT a FROM Alojamiento a", Alojamiento.class).getResultList();
            tablaAlojamientos.setItems(FXCollections.observableArrayList(todos));
        } finally {
            em.close();
        }
    }

    @FXML
    private void handleReservar() {
        if (huespedLogueado == null) {
            lblMensaje.setText("Error: Debes iniciar sesión.");
            return;
        }

        Alojamiento seleccionado = tablaAlojamientos.getSelectionModel().getSelectedItem();
        if (seleccionado == null || dateInicio.getValue() == null || dateFin.getValue() == null) {
            lblMensaje.setText("Selecciona alojamiento y fechas válidas.");
            return;
        }

        if (dateFin.getValue().isBefore(dateInicio.getValue())) {
            lblMensaje.setText("La fecha de salida debe ser posterior a la de entrada.");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {

            String jpql = "SELECT COUNT(r) FROM Reserva r WHERE r.alojamiento.id = :idAlojamiento " +
                    "AND NOT (r.fechaSalida <= :inicio OR r.fechaEntrada >= :fin)";

            Long solapamientos = em.createQuery(jpql, Long.class)
                    .setParameter("idAlojamiento", seleccionado.getIdAlojamiento())
                    .setParameter("inicio", dateInicio.getValue())
                    .setParameter("fin", dateFin.getValue())
                    .getSingleResult();

            if (solapamientos > 0) {
                lblMensaje.setText("Error: El alojamiento ya está reservado en esas fechas.");
                return;
            }


            em.getTransaction().begin();

            long numNoches = Math.max(1, ChronoUnit.DAYS.between(dateInicio.getValue(), dateFin.getValue()));
            BigDecimal total = seleccionado.getPrecioDia().multiply(BigDecimal.valueOf(numNoches));

            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setAlojamiento(em.find(Alojamiento.class, seleccionado.getIdAlojamiento()));
            nuevaReserva.setHuesped(em.find(Huesped.class, huespedLogueado.getIdHuesped()));
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
        } finally {
            em.close();
        }
    }

    @FXML
    private void handleCerrarSesion() {
        try {
            // 1. Obtenemos la URL
            var url = getClass().getResource("/vistas/LoginVista.fxml");

            // 2. Comprobamos si existe
            if (url == null) {
                throw new Exception("No se ha encontrado el archivo FXML en /vistas/LoginVista.fxml");
            }

            // 3. Cargamos la vista
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(url)));
            stage.setTitle("Login - Roomly");
            stage.show();

            // 4. Cerramos la ventana actual
            ((Stage) lblMensaje.getScene().getWindow()).close();

        } catch (Exception e) {
            System.err.println("Error crítico al cargar la vista de login: " + e.getMessage());
            lblMensaje.setText("Error al cerrar sesión.");
        }
    }




}