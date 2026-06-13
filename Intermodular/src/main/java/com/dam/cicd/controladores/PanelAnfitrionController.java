package com.dam.cicd.controladores;

import com.dam.cicd.entidades.*;
import com.dam.cicd.servicios.AnfitrionServicio;
import com.dam.cicd.utilidades.JPAUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.List;

/**
 * Controlador principal para la gestión del panel del anfitrión en la plataforma Roomly.
 * Permite la creación y modificación de alojamientos, la visualización de reservas recibidas,
 * el cálculo de ingresos y la gestión de la sesión del usuario.
 */
public class PanelAnfitrionController {

    private Anfitrion anfitrionLogueado;
    private AnfitrionServicio servicio;

    @FXML private TextField txtNombre, txtDireccion, txtPrecio, txtFotoUrl;
    @FXML private Label lblMensaje;
    @FXML private Label lblFacturacion;

    // Tabla Reservas
    @FXML private TableView<Reserva> tablaReservasRecibidas;
    @FXML private TableColumn<Reserva, String> colAlojamiento;
    @FXML private TableColumn<Reserva, String> colHuesped;
    @FXML private TableColumn<Reserva, String> colEntrada;
    @FXML private TableColumn<Reserva, String> colEstadoReserva;

    // Tabla Mis Alojamientos
    @FXML private TableView<Alojamiento> tablaMisAlojamientos;
    @FXML private TableColumn<Alojamiento, String> colFoto;
    @FXML private TableColumn<Alojamiento, String> colNombreAlojamiento;
    @FXML private TableColumn<Alojamiento, String> colDireccionAlojamiento;
    @FXML private TableColumn<Alojamiento, BigDecimal> colPrecioAlojamiento;

    /**
     * Inicializa los componentes de la vista, configurando los listeners de las tablas
     * y los factories de celdas para el renderizado de datos.
     */
    @FXML
    public void initialize() {
        this.servicio = new AnfitrionServicio(JPAUtil.getEntityManager());

        tablaMisAlojamientos.setFixedCellSize(100.0);

        tablaMisAlojamientos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNombre.setText(newSel.getNombre());
                txtDireccion.setText(newSel.getDireccion());
                txtPrecio.setText(newSel.getPrecioDia().toString());
                txtFotoUrl.setText(newSel.getFotoUrl());
            }
        });

        // Columnas personalizadas
        colAlojamiento.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlojamiento().getNombre()));
        colHuesped.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHuesped().getNombre()));
        colEntrada.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaEntrada().toString()));
        setupColumn(colEstadoReserva, "estado");

        // Columnas usando método genérico
        setupFotoColumn();
        setupColumn(colNombreAlojamiento, "nombre");
        setupColumn(colDireccionAlojamiento, "direccion");
        setupColumn(colPrecioAlojamiento, "precioDia");
    }

    private <T, U> void setupColumn(TableColumn<T, U> column, String property) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
    }

    private void setupFotoColumn() {
        colFoto.setCellValueFactory(new PropertyValueFactory<>("fotoUrl"));
        colFoto.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isBlank()) setGraphic(null);
                else {
                    Image img = new Image(url, 130, 90, true, true);
                    imageView.setImage(img);
                    setGraphic(imageView);
                }
            }
        });
    }

    public void setAnfitrion(Anfitrion anfitrion) {
        this.anfitrionLogueado = anfitrion;
        cargarDatos();
    }

    private void cargarDatos() {
        Platform.runLater(() -> {
            cargarReservasRecibidas();
            cargarMisAlojamientos();
        });
    }

    private void cargarMisAlojamientos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Alojamiento> lista = em.createQuery("SELECT a FROM Alojamiento a WHERE a.anfitrion.id = :id", Alojamiento.class)
                    .setParameter("id", anfitrionLogueado.getIdAnfitrion())
                    .getResultList();
            tablaMisAlojamientos.setItems(FXCollections.observableArrayList(lista));
        } finally {
            em.close();
        }
    }

    private void cargarReservasRecibidas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Reserva> lista = em.createQuery("SELECT r FROM Reserva r JOIN FETCH r.alojamiento a JOIN FETCH r.huesped WHERE a.anfitrion.id = :id", Reserva.class)
                    .setParameter("id", anfitrionLogueado.getIdAnfitrion())
                    .getResultList();
            tablaReservasRecibidas.setItems(FXCollections.observableArrayList(lista));
        } finally {
            em.close();
        }
    }

    @FXML
    private void handlePublicar() {
        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText());
            servicio.publicarAlojamiento(anfitrionLogueado.getIdAnfitrion(), txtNombre.getText(), txtDireccion.getText(), precio, txtFotoUrl.getText());
            lblMensaje.setText("¡Alojamiento publicado!");
            cargarDatos();
            handleLimpiar();
        } catch (Exception e) {
            lblMensaje.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuardarCambios() {
        Alojamiento seleccionado = tablaMisAlojamientos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Selecciona un alojamiento.");
            return;
        }
        try {
            servicio.modificarAnuncio(
                    seleccionado.getIdAlojamiento(),
                    txtNombre.getText(),
                    txtDireccion.getText(),
                    new BigDecimal(txtPrecio.getText())
            );

            seleccionado.setNombre(txtNombre.getText());
            seleccionado.setDireccion(txtDireccion.getText());
            seleccionado.setPrecioDia(new BigDecimal(txtPrecio.getText()));

            tablaMisAlojamientos.refresh();
            lblMensaje.setText("Cambios guardados correctamente.");
        } catch (NumberFormatException e) {
            lblMensaje.setText("Error: El precio debe ser un número válido.");
        } catch (Exception e) {
            lblMensaje.setText("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleVerFacturacion() {
        BigDecimal total = servicio.obtenerTotalFacturado(anfitrionLogueado.getIdAnfitrion());
        lblFacturacion.setText("Total: " + total + " €");
    }

    @FXML
    private void handleLimpiar() {
        txtNombre.clear();
        txtDireccion.clear();
        txtPrecio.clear();
        txtFotoUrl.clear();
        tablaMisAlojamientos.getSelectionModel().clearSelection();
    }

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