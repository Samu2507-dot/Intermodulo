package controladores;

import entidades.*;
import servicios.AnfitrionServicio;
import utilidades.JPAUtil;
import excepciones.MantenimientoException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;

public class PanelAnfitrionController {

    private AnfitrionServicio servicio;
    private Anfitrion anfitrionLogueado;

    @FXML private TextField txtNombre, txtDireccion, txtPrecio;
    @FXML private TextField txtIdModificar, txtNuevoNombre, txtNuevoPrecio;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {
        this.servicio = new AnfitrionServicio(JPAUtil.getEntityManager());
    }

    // Método para recibir al usuario que se acaba de loguear
    public void setAnfitrion(Anfitrion anfitrion) {
        this.anfitrionLogueado = anfitrion;
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
}