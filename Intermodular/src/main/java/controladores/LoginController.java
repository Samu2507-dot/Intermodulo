package controladores;

import entidades.*;
import servicios.UsuarioServicio;
import utilidades.JPAUtil;
import excepciones.AutenticacionException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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

    // --- NAVEGACIÓN ---
    @FXML private void mostrarRegistro() { panelLogin.setVisible(false); panelRegistro.setVisible(true); }
    @FXML private void mostrarLogin() { panelRegistro.setVisible(false); panelLogin.setVisible(true); }

    // --- LOGIN ---
    @FXML
    private void procesarLogin() {
        try {
            Object user = usuarioServicio.login(txtUsuario.getText(), txtPassPlana.getText());
            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("¡Bienvenido!");
            // AQUÍ LLAMARÍAS A LA SIGUIENTE PANTALLA
        } catch (AutenticacionException e) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText(e.getMessage());
        }
    }

    // --- REGISTRO (CONECTADO A TU SERVICIO) ---
    @FXML
    private void procesarRegistro() {
        try {
            // Decidimos registrar como Huésped por defecto, o podrías añadir un ComboBox en el FXML
            usuarioServicio.registrarHuesped(
                    regNombre.getText(), regApellidos.getText(), regEmail.getText(),
                    regTelefono.getText(), regUsuario.getText(), regPass.getText()
            );

            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("Cuenta creada. Ya puedes iniciar sesión.");
            mostrarLogin();

        } catch (AutenticacionException e) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText("Error: " + e.getMessage());
        }
    }
}