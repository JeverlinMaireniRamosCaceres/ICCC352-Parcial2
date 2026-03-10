package servicios;

import entidades.*;
import servicios.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> calcularEstadisticas(int idEvento) {

        Evento evento = find(idEvento);
        if (evento == null) return null;

        List<Inscripcion> inscripciones = InscripcionServices.getInstancia().buscarPorEvento(evento);

        // ----- metricas -----

        long inscritos  = inscripciones.size();
        long asistentes = 0;
        for (Inscripcion i : inscripciones) {
            if (i.isAsistio()) asistentes++;
        }

        double pctAsistencia = 0.0;
        if (inscritos > 0) {
            pctAsistencia = Math.round(asistentes * 1000.0 / inscritos) / 10.0;
        }

        double pctOcupacion = 0.0;
        if (evento.getCupoMaximo() > 0) {
            pctOcupacion = Math.round(inscritos * 1000.0 / evento.getCupoMaximo()) / 10.0;
        }

        // ----- Inscripciones por dia --------------------------

        Map<String, Long> porDia = new java.util.TreeMap<>();
        for (Inscripcion i : inscripciones) {
            String dia = "Sin fecha";
            if (i.getFechaInscripcion() != null) {
                dia = i.getFechaInscripcion().toString();
            }
            porDia.merge(dia, 1L, Long::sum);
        }

        // ------ Asistencia por hora ----------

        Map<String, Long> porHora = new java.util.TreeMap<>();
        for (Inscripcion i : inscripciones) {
            if (i.isAsistio() && i.getHoraAsistencia() != null) {
                String hora = String.format("%02d:00", i.getHoraAsistencia().getHour());
                porHora.merge(hora, 1L, Long::sum);
            }
        }

        // ---- Informacion del evento --------

        String fechaStr = "";
        if (evento.getFecha() != null) {
            fechaStr = evento.getFecha().toString();
        }

        String horaStr = "";
        if (evento.getHora() != null) {
            horaStr = evento.getHora().toString();
        }

        String organizadorNombre = "Sin organizador";
        if (evento.getOrganizador() != null) {
            organizadorNombre = evento.getOrganizador().getNombre();
        }

        Map<String, Object> infoEvento = new HashMap<>();
        infoEvento.put("titulo",      evento.getTitulo());
        infoEvento.put("descripcion", evento.getDescripcion());
        infoEvento.put("fecha",       fechaStr);
        infoEvento.put("hora",        horaStr);
        infoEvento.put("lugar",       evento.getLugar());
        infoEvento.put("cupoMaximo",  evento.getCupoMaximo());
        infoEvento.put("organizador", organizadorNombre);

        // --------- Resultado final -----------

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("evento",               infoEvento);
        resultado.put("totalInscritos",       inscritos);
        resultado.put("totalAsistentes",      asistentes);
        resultado.put("porcentajeAsistencia", pctAsistencia);
        resultado.put("porcentajeOcupacion",  pctOcupacion);
        resultado.put("inscripcionesPorDia",  porDia);
        resultado.put("asistenciaPorHora",    porHora);

        return resultado;
    }
}