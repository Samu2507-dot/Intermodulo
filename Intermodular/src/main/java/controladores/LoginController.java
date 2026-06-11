package controladores;

import entidades.*;
import servicios.UsuarioServicio;
import utilidades.JPAUtil;
import excepciones.AutenticacionException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private ComboBox<String> regRol;
    @FXML private VBox panelLogin, panelRegistro;
    @FXML private TextField txtUsuario, regNombre, regApellidos, regUsuario, regEmail, regTelefono;
    @FXML private PasswordField txtPassPlana, regPass;
    @FXML private Label lblError;

    private UsuarioServicio usuarioServicio;

    @FXML
    public void initialize() {
        this.usuarioServicio = new UsuarioServicio(JPAUtil.getEntityManager());
    }

    @FXML private void mostrarRegistro() { panelLogin.setVisible(false); panelRegistro.setVisible(true); lblError.setText(""); }
    @FXML private void mostrarLogin() { panelRegistro.setVisible(false); panelLogin.setVisible(true); lblError.setText(""); }

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
    private void cargarInterfazPorRol(Object usuario) {
        String fxmlRuta = null;

        if (usuario instanceof Huesped) fxmlRuta = "/vistas/PanelHuesped.fxml";
        else if (usuario instanceof Anfitrion) fxmlRuta = "/vistas/PanelAnfitrion.fxml";
        else if (usuario instanceof OperarioMantenimiento) fxmlRuta = "/vistas/PanelMantenimiento.fxml";

        try {
            if (fxmlRuta == null) throw new IOException("Rol no reconocido: " + usuario.getClass().getName());

            // DEPURACIÓN: Comprobamos si el recurso existe antes de intentar cargarlo
            java.net.URL url = getClass().getResource(fxmlRuta);
            System.out.println("DEBUG: Buscando archivo en -> " + fxmlRuta);
            System.out.println("DEBUG: ¿URL encontrada? -> " + (url != null));

            if (url == null) {
                throw new IOException("Archivo FXML no encontrado en el classpath: " + fxmlRuta);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText("Error al cargar pantalla: " + e.getMessage());
            e.printStackTrace(); // Esto mostrará el error real en tu consola
        }
    }

}