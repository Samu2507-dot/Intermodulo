package excepciones;

public class ReservaInvalidaException extends Exception{

    public ReservaInvalidaException() {
        super("La reserva no se ha podido procesar debido a datos inválidos.");
    }


    public ReservaInvalidaException(String mensaje) {
        super(mensaje);
    }


    public ReservaInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}