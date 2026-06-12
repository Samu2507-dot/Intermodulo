package com.dam.cicd.controladores;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.dam.cicd.entidades.Anfitrion;
import com.dam.cicd.entidades.Alojamiento;
import com.dam.cicd.entidades.Reserva;
import com.dam.cicd.servicios.AnfitrionServicio;
import com.dam.cicd.utilidades.JPAUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class PanelAnfitrionController {

    private AnfitrionServicio servicio;
    private Anfitrion anfitrionLogueado;

    // Elementos de la UI
    @FXML private TextField txtNombre, txtDireccion, txtPrecio;
    @FXML private Label lblMensaje;

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

    @FXML
    public void initialize() {
        this.servicio = new AnfitrionServicio(JPAUtil.getEntityManager());

        // Configuración columnas Reservas
        colAlojamiento.setCellValueFactory(new PropertyValueFactory<>("alojamiento"));
        colHuesped.setCellValueFactory(new PropertyValueFactory<>("huesped"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));

        // Configuración columnas Alojamientos
        // --- Configuración de la columna de Foto (con visualización de imagen) ---
        colFoto.setCellValueFactory(new PropertyValueFactory<>("fotoUrl"));
        colFoto.setCellFactory(column -> new TableCell<Alojamiento, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isBlank()) {
                    setGraphic(null);
                } else {
                    // Cargamos la imagen con un ancho fijo para que no deforme la tabla
                    Image img = new Image(url, 80, 50, true, true);
                    imageView.setImage(img);
                    setGraphic(imageView);
                }
            }
        });
        colNombreAlojamiento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccionAlojamiento.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colPrecioAlojamiento.setCellValueFactory(new PropertyValueFactory<>("precioDia"));
    }

    public void setAnfitrion(Anfitrion anfitrion) {
        this.anfitrionLogueado = anfitrion;
        cargarReservasRecibidas();
        cargarMisAlojamientos();
    }

    private void cargarMisAlojamientos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Alojamiento> lista = em.createQuery(
                            "SELECT a FROM Alojamiento a WHERE a.anfitrion.id = :id", Alojamiento.class)
                    .setParameter("id", anfitrionLogueado.getIdAnfitrion())
                    .getResultList();
            tablaMisAlojamientos.setItems(FXCollections.observableArrayList(lista));
        } finally {
            em.close();
        }
    }

    private void cargarReservasRecibidas() {
        // ... (Tu código anterior de carga de reservas)
    }

    @FXML private void handlePublicar() { /* ... tu lógica ... */ }
    // Otros métodos...
}