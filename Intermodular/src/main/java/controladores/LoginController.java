package controladores;

import entidades.*;
import servicios.UsuarioServicio;
import utilidades.JPAUtil;
import excepciones.AutenticacionException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassPlana;

    @FXML
    private Label lblError;

    private UsuarioServicio usuarioServicio;

    @FXML
    public void initialize() {
        // Inicializamos tu servicio pasándole el EntityManager de tu proyecto
        this.usuarioServicio = new UsuarioServicio(JPAUtil.getEntityManager());
        lblError.setText("");
    }

    @FXML
    private void procesarLogin(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String pass = txtPassPlana.getText();

        if (usuario.isEmpty() || pass.isEmpty()) {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText("Por favor, rellena todos los campos.");
            return;
        }

        try {
            log.info("Intentando autenticar al usuario: {}", usuario);
            Object usuarioLogueado = usuarioServicio.login(usuario, pass);

            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("¡Bienvenido/a! Redirigiendo...");

            // Aquí es donde tu servicio nos dice qué tipo de usuario es
            if (usuarioLogueado instanceof Anfitrion) {
                Anfitrion anfi = (Anfitrion) usuarioLogueado;
                log.info("✅ [LOGIN OK] Rol: Anfitrión | Usuario: {}", anfi.getUsuario());
            } else if (usuarioLogueado instanceof Huesped) {
                Huesped huesped = (Huesped) usuarioLogueado;
                log.info("✅ [LOGIN OK] Rol: Huésped | Usuario: {}", huesped.getUsuario());
            } else if (usuarioLogueado instanceof OperarioMantenimiento) {
                OperarioMantenimiento ope = (OperarioMantenimiento) usuarioLogueado;
                log.info("✅ [LOGIN OK] Rol: Operario | Usuario: {}", ope.getUsuario());
            }

        } catch (AutenticacionException e) {
            log.warn("🚨 Login fallido para '{}': {}", usuario, e.getMessage());
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText(e.getMessage()); // Tu mensaje de la excepción
        } catch (Exception e) {
            log.error("💥 Error imprevisto en el Login: ", e);
            lblError.setText("Error interno del sistema.");
        }
    }
}