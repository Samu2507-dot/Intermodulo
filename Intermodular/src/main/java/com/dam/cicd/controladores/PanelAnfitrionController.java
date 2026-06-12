package com.dam.cicd.controladores;

import com.dam.cicd.entidades.*;
import com.dam.cicd.servicios.AnfitrionServicio;
import com.dam.cicd.utilidades.JPAUtil;
import javafx.application.Platform;
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

        colAlojamiento.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAlojamiento().getNombre()));
        colHuesped.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getHuesped().getNombre()));
        colEntrada.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFechaEntrada().toString()));
        colEstadoReserva.setCellValueFactory(new PropertyValueFactory<>("estado"));

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
        colNombreAlojamiento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccionAlojamiento.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colPrecioAlojamiento.setCellValueFactory(new PropertyValueFactory<>("precioDia"));
    }

    /**
     * Asigna el anfitrión que ha iniciado sesión y carga los datos correspondientes en la interfaz.
     * @param anfitrion El objeto Anfitrión logueado.
     */
    public void setAnfitrion(Anfitrion anfitrion) {
        this.anfitrionLogueado = anfitrion;
        cargarDatos();
    }

    /**
     * Ejecuta las llamadas de carga de datos de forma asíncrona para no bloquear la UI.
     */
    private void cargarDatos() {
        Platform.runLater(() -> {
            cargarReservasRecibidas();
            cargarMisAlojamientos();
        });
    }

    /**
     * Consulta y carga en la tabla todos los alojamientos registrados por el anfitrión.
     */
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

    /**
     * Consulta y carga en la tabla las reservas asociadas a los alojamientos del anfitrión.
     */
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

    /**
     * Gestiona la acción de publicar un nuevo alojamiento validando los datos del formulario.
     */
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

    /**
     * Actualiza la información de un alojamiento previamente seleccionado en la tabla.
     */
    @FXML
    private void handleGuardarCambios() {
        Alojamiento seleccionado = tablaMisAlojamientos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Selecciona un alojamiento.");
            return;
        }
        try {
            servicio.modificarAnuncio(seleccionado.getIdAlojamiento(), txtNombre.getText(), new BigDecimal(txtPrecio.getText()));
            lblMensaje.setText("Cambios guardados correctamente.");
            cargarDatos();
        } catch (Exception e) {
            lblMensaje.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Calcula y muestra el total de ingresos facturados por el anfitrión.
     */
    @FXML
    private void handleVerFacturacion() {
        BigDecimal total = servicio.obtenerTotalFacturado(anfitrionLogueado.getIdAnfitrion());
        lblFacturacion.setText("Total: " + total + " €");
    }

    /**
     * Limpia los campos de texto del formulario y deselecciona cualquier elemento de la tabla.
     */
    @FXML
    private void handleLimpiar() {
        txtNombre.clear();
        txtDireccion.clear();
        txtPrecio.clear();
        txtFotoUrl.clear();
        tablaMisAlojamientos.getSelectionModel().clearSelection();
    }

    /**
     * Cierra la sesión actual y redirige al usuario a la vista de login.
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