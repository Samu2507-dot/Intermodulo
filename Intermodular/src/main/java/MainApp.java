import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilidades.JPAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            log.info("Cargando la interfaz gráfica de Roomly Desktop...");


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/LoginVista.fxml"));
            Parent root = loader.load();

            // Configuramos la ventana física de Windows
            primaryStage.setTitle("Roomly - Control de Alojamientos (AWS)");
            primaryStage.setScene(new Scene(root, 800, 550));
            primaryStage.setResizable(false);
            primaryStage.show();

            log.info("✅ Ventana de Login desplegada correctamente.");
        } catch(Exception e) {
            log.error("💥 Error fatal al levantar la vista FXML: ", e);
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("Cerrando la aplicación Roomly Desktop...");
        JPAUtil.shutdown();
        log.info("🔌 Conexión con AWS cerrada de forma segura.");
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}