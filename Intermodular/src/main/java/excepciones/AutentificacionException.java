package excepciones;
//Esta excepción es para los errores de inicio de sesión (chequeada)
public class AutentificacionException extends Exception{

    public AutentificacionException(){

    }

    public AutentificacionException(String mensaje){
        super(mensaje);
    }

    public AutentificacionException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
