package controladores;

import entidades.*;
import servicios.HuespedServicio;
import utilidades.JPAUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PanelHuespedController {

    private HuespedServicio servicio;

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colAlojamiento;
    @FXML private TableColumn<Reserva, String> colFechas;

    @FXML
    public void initialize() {
        this.servicio = new HuespedServicio(JPAUtil.getEntityManager());


        colAlojamiento.setCellValueFactory(new PropertyValueFactory<>("alojamiento"));
        colFechas.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));

        cargarMisReservas();
    }

    private void cargarMisReservas() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Reserva> reservas = em.createQuery("SELECT r FROM Reserva r", Reserva.class).getResultList();
        ObservableList<Reserva> lista = FXCollections.observableArrayList(reservas);
        tablaReservas.setItems(lista);
        em.close();
    }
}