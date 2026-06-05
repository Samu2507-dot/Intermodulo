package servicios;

import entidades.Alojamiento;
import jakarta.persistence.EntityManager;

public class AnfitrionServicio {

    //Esta clase definira la lógica de las acciones que podrá realizar el anfitrión


    private EntityManager em;

    public AnfitrionServicio(EntityManager em){
        this.em = em;
    }



}
