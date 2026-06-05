package excepciones;

public class AutenticacionException extends Exception{

    public AutenticacionException(){

    }

    public AutenticacionException(String mensaje){
        super(mensaje);
    }

    public AutenticacionException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
