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
    @FXML private VBox panelLogin;
    @FXML private VBox panelRegistro;
    @FXML private TextField txtUsuario, regNombre, regApellidos, regUsuario, regEmail, regTelefono;
    @FXML private PasswordField txtPassPlana, regPass;
    @FXML private Label lblError;

    private UsuarioServicio usuarioServicio;

    @FXML
    public void initialize() {
        this.usuarioServicio = new UsuarioServicio(JPAUtil.getEntityManager());
        // Llenamos el ComboBox si no lo hiciste en el FXML
        if (regRol != null) {
            regRol.getItems().addAll("Huésped", "Anfitrión", "Mantenimiento");
        }
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
            // Delegamos al servicio según el rol seleccionado
            switch (rol) {
                case "Huésped":
                    usuarioServicio.registrarHuesped(regNombre.getText(), regApellidos.getText(), regEmail.getText(), regTelefono.getText(), regUsuario.getText(), regPass.getText());
                    break;
                case "Anfitrión":
                    usuarioServicio.registrarAnfitrion(regNombre.getText(), regApellidos.getText(), regEmail.getText(), regTelefono.getText(), regUsuario.getText(), regPass.getText());
                    break;
                case "Mantenimiento":
                    usuarioServicio.registrarOperario(
                            regNombre.getText(),
                            regUsuario.getText(),
                            regPass.getText()
                    );
                    break;
            }

            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("¡Cuenta creada! Ya puedes iniciar sesión.");
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
        String fxmlRuta = "";
        if (usuario instanceof Huesped) fxmlRuta = "/vista/PanelHuesped.fxml";
        else if (usuario instanceof Anfitrion) fxmlRuta = "/vista/PanelAnfitrion.fxml";
        else if (usuario instanceof OperarioMantenimiento) fxmlRuta = "/vista/PanelMantenimiento.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            lblError.setText("Error al cargar la interfaz.");
            e.printStackTrace();
        }
    }
}