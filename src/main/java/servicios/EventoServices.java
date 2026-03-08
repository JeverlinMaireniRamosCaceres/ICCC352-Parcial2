package servicios;

import entidades.Evento;

public class EventoServices extends GestionDb<Evento>{

    private static EventoServices instancia;

    private EventoServices(){
        super(Evento.class);
    }

    public static EventoServices getInstancia(){
        if(instancia == null){
            instancia = new EventoServices();
        }
        return instancia;
    }
}