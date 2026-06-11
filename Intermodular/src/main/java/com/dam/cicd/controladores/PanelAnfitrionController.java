package com.dam.cicd.controladores;

import com.dam.cicd.entidades.Anfitrion;
import com.dam.cicd.entidades.Reserva;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.dam.cicd.servicios.AnfitrionServicio;
import com.dam.cicd.utilidades.JPAUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class PanelAnfitrionController {

    private AnfitrionServicio servicio;
    private Anfitrion anfitrionLogueado;

    @FXML private TextField txtNombre, txtDireccion, txtPrecio;
    @FXML private TextField txtIdModificar, txtNuevoNombre, txtNuevoPrecio;
    @FXML private Label lblMensaje;

    @FXML private TableView<Reserva> tablaReservasRecibidas;
    @FXML private TableColumn<Reserva, String> colAlojamiento;
    @FXML private TableColumn<Reserva, String> colHuesped;
    @FXML private TableColumn<Reserva, String> colEntrada;

    @FXML
    public void initialize() {
        this.servicio = new AnfitrionServicio(JPAUtil.getEntityManager());

        // Configuración de las columnas de la tabla
        colAlojamiento.setCellValueFactory(new PropertyValueFactory<>("alojamiento"));
        colHuesped.setCellValueFactory(new PropertyValueFactory<>("huesped"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
    }

    public void setAnfitrion(Anfitrion anfitrion) {
        this.anfitrionLogueado = anfitrion;
        cargarReservasRecibidas();
    }

    private void cargarReservasRecibidas() {
        if (anfitrionLogueado == null) return;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Filtramos reservas donde el alojamiento pertenece al anfitrión logueado
            List<Reserva> reservas = em.createQuery(
                            "SELECT r FROM Reserva r WHERE r.alojamiento.anfitrion.id = :idAnfitrion", Reserva.class)
                    .setParameter("idAnfitrion", anfitrionLogueado.getIdAnfitrion())
                    .getResultList();

            ObservableList<Reserva> lista = FXCollections.observableArrayList(reservas);
            tablaReservasRecibidas.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @FXML
    private void handlePublicar() {
        try {
            if (anfitrionLogueado == null) throw new Exception("No hay sesión iniciada.");

            servicio.publicarAlojamiento(
                    anfitrionLogueado.getIdAnfitrion(),
                    txtNombre.getText(),
                    txtDireccion.getText(),
                    new BigDecimal(txtPrecio.getText())
            );
            lblMensaje.setText("¡Alojamiento publicado con éxito!");
            limpiarCamposPublicar();
            // Opcional: Recargar tabla si fuera necesario
        } catch (Exception e) {
            lblMensaje.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleModificar() {
        try {
            servicio.modificarAnuncio(
                    Integer.parseInt(txtIdModificar.getText()),
                    txtNuevoNombre.getText(),
                    new BigDecimal(txtNuevoPrecio.getText())
            );
            lblMensaje.setText("Alojamiento actualizado correctamente.");
        } catch (Exception e) {
            lblMensaje.setText("Error: " + e.getMessage());
        }
    }

    private void limpiarCamposPublicar() {
        txtNombre.clear(); txtDireccion.clear(); txtPrecio.clear();
    }

    @FXML
    private void logout() {
        try {
            // Cargar la vista de Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Login.fxml")); // Asegúrate de que la ruta sea correcta
            Parent root = loader.load();

            // Obtener el stage actual
            Stage stage = (Stage) lblMensaje.getScene().getWindow(); // lblMensaje existe en todos tus paneles

            // Cambiar la escena
            stage.setScene(new Scene(root));
            stage.setTitle("Roomly - Login");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}