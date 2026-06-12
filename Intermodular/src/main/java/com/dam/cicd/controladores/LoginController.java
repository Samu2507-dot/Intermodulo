package com.dam.cicd.controladores;

import com.dam.cicd.entidades.Anfitrion;
import com.dam.cicd.entidades.Huesped;
import com.dam.cicd.entidades.OperarioMantenimiento;
import com.dam.cicd.servicios.UsuarioServicio;
import com.dam.cicd.utilidades.JPAUtil;
import com.dam.cicd.excepciones.AutenticacionException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador encargado de gestionar el proceso de autenticación y registro de usuarios en la plataforma Roomly.
 * Maneja la transición entre las vistas de login y registro, así como la inicialización de las interfaces
 * específicas según el rol del usuario autenticado.
 */
public class LoginController {
    @FXML private ComboBox<String> regRol;
    @FXML private VBox panelLogin, panelRegistro;
    @FXML private TextField txtUsuario, regNombre, regApellidos, regUsuario, regEmail, regTelefono;
    @FXML private PasswordField txtPassPlana, regPass;
    @FXML private Label lblError;

    private UsuarioServicio usuarioServicio;

    /**
     * Inicializa el controlador y establece la instancia del servicio de usuarios utilizando el EntityManager de JPA.
     */
    @FXML
    public void initialize() {
        this.usuarioServicio = new UsuarioServicio(JPAUtil.getEntityManager());
    }

    /**
     * Alterna la visibilidad de los paneles para mostrar la interfaz de registro de nuevos usuarios.
     */
    @FXML private void mostrarRegistro() { panelLogin.setVisible(false); panelRegistro.setVisible(true); lblError.setText(""); }

    /**
     * Alterna la visibilidad de los paneles para mostrar la interfaz de inicio de sesión.
     */
    @FXML private void mostrarLogin() { panelRegistro.setVisible(false); panelLogin.setVisible(true); lblError.setText(""); }

    /**
     * Procesa los datos del formulario de registro y delega la creación del usuario al servicio correspondiente según su rol.
     */
    @FXML
    private void procesarRegistro() {
        String rol = regRol.getValue();
        if (rol == null) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText("Por favor, selecciona un tipo de cuenta.");
            return;
        }

        try {
            switch (rol) {
                case "Huésped" -> usuarioServicio.registrarHuesped(regNombre.getText(), regApellidos.getText(), regEmail.getText(), regTelefono.getText(), regUsuario.getText(), regPass.getText());
                case "Anfitrión" -> usuarioServicio.registrarAnfitrion(regNombre.getText(), regApellidos.getText(), regEmail.getText(), regTelefono.getText(), regUsuario.getText(), regPass.getText());
                case "Mantenimiento" -> usuarioServicio.registrarOperario(regNombre.getText(), regUsuario.getText(), regPass.getText());
            }
            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("¡Cuenta creada! Inicia sesión.");
            mostrarLogin();
        } catch (Exception e) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Valida las credenciales introducidas y solicita el acceso al sistema mediante el servicio de usuarios.
     */
    @FXML
    private void procesarLogin() {
        try {
            Object usuarioLogueado = usuarioServicio.login(txtUsuario.getText(), txtPassPlana.getText());
            cargarInterfazPorRol(usuarioLogueado);
        } catch (AutenticacionException e) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText(e.getMessage());
        }
    }

    /**
     * Carga y muestra la vista específica (FXML) según el tipo de entidad (rol) del usuario que inicia sesión,
     * inyectando el objeto usuario correspondiente en el controlador de dicha vista.
     * * @param usuario El objeto que representa al usuario logueado (Huesped, Anfitrion o OperarioMantenimiento).
     */
    private void cargarInterfazPorRol(Object usuario) {
        String fxmlRuta = null;
        if (usuario instanceof Huesped) fxmlRuta = "/vistas/PanelHuesped.fxml";
        else if (usuario instanceof Anfitrion) fxmlRuta = "/vistas/PanelAnfitrion.fxml";
        else if (usuario instanceof OperarioMantenimiento) fxmlRuta = "/vistas/PanelMantenimiento.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent root = loader.load();


            if (usuario instanceof Huesped) {
                PanelHuespedController controller = loader.getController();
                controller.setUsuario((Huesped) usuario);
            }
            else if (usuario instanceof Anfitrion) {
                PanelAnfitrionController controller = loader.getController();
                controller.setAnfitrion((Anfitrion) usuario);
            }


            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText("Error al cargar pantalla: " + e.getMessage());
            e.printStackTrace();
        }
    }

}