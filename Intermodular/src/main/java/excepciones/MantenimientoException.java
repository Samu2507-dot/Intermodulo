package excepciones;

public class MantenimientoException extends Exception {

    // Constructor sin parámetros
    public MantenimientoException() {
        super("Error en la gestión de la orden de mantenimiento.");
    }


    public MantenimientoException(String mensaje) {
        super(mensaje);
    }


    public MantenimientoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}