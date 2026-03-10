package servicios;

import entidades.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminServices {

    private static AdminServices instancia;

    private AdminServices(){
        super();
    }

    public static AdminServices getInstancia(){
        if(instancia == null){
            instancia = new AdminServices();
        }
        return instancia;
    }

    public Map<String, Object> calcularResumen() {

        long totalEventos = EventoServices.getInstancia().findAll().size();

        long eventosProximos = 0;
        for (var evento : EventoServices.getInstancia().findAll()) {
            if (evento.getFecha() != null && evento.getFecha().isAfter(LocalDate.now())) {
                eventosProximos++;
            }
        }

        long totalUsuarios = UsuarioServices.getInstancia().findAll().size();

        LocalDate hace30dias = LocalDate.now().minusDays(30);
        long inscripciones30dias = 0;
        List<Inscripcion> todasInscripciones = InscripcionServices.getInstancia().findAll();
        for (Inscripcion i : todasInscripciones) {
            if (i.getFechaInscripcion() != null && i.getFechaInscripcion().isAfter(hace30dias)) {
                inscripciones30dias++;
            }
        }

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalEventos",        totalEventos);
        resumen.put("eventosProximos",     eventosProximos);
        resumen.put("totalUsuarios",       totalUsuarios);
        resumen.put("inscripciones30dias", inscripciones30dias);

        return resumen;
    }


}
