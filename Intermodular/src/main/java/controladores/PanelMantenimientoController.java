package controladores;

import entidades.*;
import servicios.MantenimientoServicio;
import utilidades.JPAUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PanelMantenimientoController {

    private MantenimientoServicio servicio;
    private OperarioMantenimiento operarioLogueado;

    @FXML private TableView<Mantenimiento> tablaTareas;
    @FXML private TableColumn<Mantenimiento, String> colDescripcion;
    @FXML private TableColumn<Mantenimiento, String> colEstado;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {
        this.servicio = new MantenimientoServicio(JPAUtil.getEntityManager());

        // Configurar columnas
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    public void setOperario(OperarioMantenimiento operario) {
        this.operarioLogueado = operario;
        cargarMisTareas();
    }

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
            cargarMisTareas(); // Refrescar la tabla
        } catch (Exception e) {
            lblMensaje.setText("Error: " + e.getMessage());
        }
    }

    private void cargarMisTareas() {
        if (operarioLogueado == null) return;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Buscamos solo las tareas asignadas a este operario
            List<Mantenimiento> tareas = em.createQuery(
                            "SELECT m FROM Mantenimiento m WHERE m.operario.id = :id AND m.estado != 'Completado'",
                            Mantenimiento.class)
                    .setParameter("id", operarioLogueado.getIdOperario())
                    .getResultList();

            tablaTareas.setItems(FXCollections.observableArrayList(tareas));
        } finally {
            em.close();
        }
    }
}