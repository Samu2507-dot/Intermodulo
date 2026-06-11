package com.dam.cicd.controladores;

import com.dam.cicd.entidades.Mantenimiento;
import com.dam.cicd.entidades.OperarioMantenimiento;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.dam.cicd.servicios.MantenimientoServicio;
import com.dam.cicd.utilidades.JPAUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para la gestión de tareas de mantenimiento.
 * Recibe al operario logueado y filtra las tareas asignadas según su ID.
 */
public class PanelMantenimientoController {

    private MantenimientoServicio servicio;
    private OperarioMantenimiento operarioLogueado;

    @FXML private TableView<Mantenimiento> tablaTareas;
    @FXML private TableColumn<Mantenimiento, String> colDescripcion;
    @FXML private TableColumn<Mantenimiento, String> colEstado;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {
        // Inicializamos el servicio con el EntityManager de la utilidad JPA
        this.servicio = new MantenimientoServicio(JPAUtil.getEntityManager());

        // Configuración de las columnas de la tabla
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    /**
     * Este método es el punto de entrada para pasar la identidad del usuario
     * desde el LoginController.
     */
    public void setOperario(OperarioMantenimiento operario) {
        this.operarioLogueado = operario;
        cargarMisTareas();
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
    /**
     * Acción para actualizar el estado de una incidencia a "Completado".
     */
    @FXML
    private void handleMarcarCompletado() {
        Mantenimiento seleccionada = tablaTareas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblMensaje.setText("Selecciona una tarea de la tabla primero.");
            return;
        }

        try {
            // Llamamos al servicio para persistir el cambio
            servicio.actualizarEstadoMantenimiento(seleccionada.getIdMantenimiento(), "Completado");
            lblMensaje.setText("¡Tarea marcada como completada!");

            // Refrescamos la vista
            cargarMisTareas();
        } catch (Exception e) {
            lblMensaje.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga las tareas no completadas asignadas al operario actual.
     */
    private void cargarMisTareas() {
        if (operarioLogueado == null) return;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Consulta JPQL filtrando por ID de operario y estado
            List<Mantenimiento> tareas = em.createQuery(
                            "SELECT m FROM Mantenimiento m WHERE m.operario.id = :id AND m.estado != 'Completado'",
                            Mantenimiento.class)
                    .setParameter("id", operarioLogueado.getIdOperario())
                    .getResultList();

            // Pasamos la lista a formato Observable para que la tabla lo entienda
            ObservableList<Mantenimiento> listaTareas = FXCollections.observableArrayList(tareas);
            tablaTareas.setItems(listaTareas);

        } catch (Exception e) {
            lblMensaje.setText("Error al cargar las tareas.");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


}