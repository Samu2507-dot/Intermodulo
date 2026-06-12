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
 * Controlador encargado de gestionar el panel de los operarios de mantenimiento.
 * Permite visualizar las tareas asignadas, filtrar las pendientes y actualizar
 * el estado de las incidencias en el sistema Roomly.
 */
public class PanelMantenimientoController {

    private MantenimientoServicio servicio;
    private OperarioMantenimiento operarioLogueado;

    @FXML private TableView<Mantenimiento> tablaTareas;
    @FXML private TableColumn<Mantenimiento, String> colDescripcion;
    @FXML private TableColumn<Mantenimiento, String> colEstado;
    @FXML private Label lblMensaje;

    /**
     * Inicializa los componentes de la interfaz, configurando los factories de celdas
     * y la instancia del servicio de mantenimiento.
     */
    @FXML
    public void initialize() {
        this.servicio = new MantenimientoServicio(JPAUtil.getEntityManager());

        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    /**
     * Define el operario que ha iniciado sesión y carga sus tareas pendientes asignadas.
     * @param operario El operario de mantenimiento autenticado.
     */
    public void setOperario(OperarioMantenimiento operario) {
        this.operarioLogueado = operario;
        cargarMisTareas();
    }

    /**
     * Actualiza el estado de la tarea seleccionada en la tabla a "Completado"
     * y refresca la lista de tareas pendientes.
     */
    @FXML
    private void handleMarcarCompletado() {
        Mantenimiento seleccionada = tablaTareas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblMensaje.setText("Selecciona una tarea de la tabla primero.");
            return;
        }

        try {
            servicio.actualizarEstadoMantenimiento(seleccionada.getIdMantenimiento(), "Completado");
            lblMensaje.setText("¡Tarea marcada como completada!");
            cargarMisTareas();
        } catch (Exception e) {
            lblMensaje.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Consulta y filtra las tareas de mantenimiento asignadas al operario logueado
     * que aún no han sido completadas.
     */
    private void cargarMisTareas() {
        if (operarioLogueado == null) return;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Mantenimiento> tareas = em.createQuery(
                            "SELECT m FROM Mantenimiento m WHERE m.operario.id = :id AND m.estado != 'Completado'",
                            Mantenimiento.class)
                    .setParameter("id", operarioLogueado.getIdOperario())
                    .getResultList();

            ObservableList<Mantenimiento> listaTareas = FXCollections.observableArrayList(tareas);
            tablaTareas.setItems(listaTareas);

        } catch (Exception e) {
            lblMensaje.setText("Error al cargar las tareas.");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Finaliza la sesión actual del operario y redirige la aplicación a la vista de login.
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