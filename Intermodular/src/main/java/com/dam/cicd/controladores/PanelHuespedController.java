package com.dam.cicd.controladores;

import com.dam.cicd.entidades.Huesped;
import com.dam.cicd.entidades.Reserva;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.dam.cicd.servicios.HuespedServicio;
import com.dam.cicd.utilidades.JPAUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.util.List;

public class PanelHuespedController {

    private HuespedServicio servicio;
    private Huesped huespedLogueado; // Identidad del usuario
    @FXML private Label lblMensaje;
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colAlojamiento;
    @FXML private TableColumn<Reserva, String> colFechas;
    @FXML private TextField txtDestino;
    @FXML private DatePicker dateInicio;

    @FXML
    public void initialize() {
        this.servicio = new HuespedServicio(JPAUtil.getEntityManager());

        // Configuración de las columnas de la tabla
        colAlojamiento.setCellValueFactory(new PropertyValueFactory<>("alojamiento"));
        colFechas.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
    }

    /**
     * Este método es llamado desde el LoginController justo después de cargar la escena.
     */
    public void setUsuario(Huesped huesped) {
        this.huespedLogueado = huesped;
        // Una vez que tenemos al huésped, cargamos solo sus datos
        cargarMisReservas();
    }

    /**
     * Carga las reservas filtradas por el ID del huésped logueado.
     */
    private void cargarMisReservas() {
        if (huespedLogueado == null) return;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Filtramos específicamente por el ID del huésped actual
            List<Reserva> reservas = em.createQuery(
                            "SELECT r FROM Reserva r WHERE r.huesped.id = :idHuesped", Reserva.class)
                    .setParameter("idHuesped", huespedLogueado.getIdHuesped())
                    .getResultList();

            ObservableList<Reserva> lista = FXCollections.observableArrayList(reservas);
            tablaReservas.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
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