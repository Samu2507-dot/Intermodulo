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

    @FXML private VBox panelLogin;
    @FXML private VBox panelRegistro;
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
    private void procesarLogin() {
        try {
            Object usuarioLogueado = usuarioServicio.login(txtUsuario.getText(), txtPassPlana.getText());

            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("¡Bienvenido! Redirigiendo...");

            // Llamamos al método que decide qué pantalla cargar
            cargarInterfazPorRol(usuarioLogueado);

        } catch (AutenticacionException e) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText(e.getMessage());
        }
    }

    private void cargarInterfazPorRol(Object usuario) {
        String fxmlRuta = "";

        // Determinamos qué FXML cargar según el tipo de objeto
        if (usuario instanceof Huesped) {
            fxmlRuta = "/vista/PanelHuesped.fxml";
        } else if (usuario instanceof Anfitrion) {
            fxmlRuta = "/vista/PanelAnfitrion.fxml";
        } else if (usuario instanceof OperarioMantenimiento) {
            fxmlRuta = "/vista/PanelMantenimiento.fxml";
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent root = loader.load();

            // Obtenemos la ventana actual y cambiamos la escena
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Roomly - Panel de " + usuario.getClass().getSimpleName());
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            lblError.setText("Error al cargar la interfaz del rol.");
            e.printStackTrace();
        }
    }

    @FXML
    private void procesarRegistro() {
        try {
            // Nota: Podrías añadir un ChoiceBox en el FXML para elegir rol al registrar
            usuarioServicio.registrarHuesped(
                    regNombre.getText(), regApellidos.getText(), regEmail.getText(),
                    regTelefono.getText(), regUsuario.getText(), regPass.getText()
            );

            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("Cuenta creada. Inicia sesión para entrar.");
            mostrarLogin();

        } catch (AutenticacionException e) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText("Error: " + e.getMessage());
        }
    }
}