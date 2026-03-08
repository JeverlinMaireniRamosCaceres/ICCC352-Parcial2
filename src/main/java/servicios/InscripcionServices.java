package servicios;

import entidades.Evento;
import entidades.Inscripcion;
import entidades.Usuario;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class InscripcionServices extends GestionDb<Inscripcion>{

    private static InscripcionServices instancia;

    private InscripcionServices(){
        super(Inscripcion.class);
    }

    public static InscripcionServices getInstancia(){
        if(instancia == null){
            instancia = new InscripcionServices();
        }
        return instancia;
    }

    public List<Inscripcion> buscarPorUsuario(Usuario usuario) {
        EntityManager em = getEntityManager();

        try {
            return em.createQuery(
                            "select i from Inscripcion i " +
                                    "join fetch i.evento " +
                                    "where i.usuario = :usuario " +
                                    "order by i.fechaInscripcion desc",
                            Inscripcion.class
                    ).setParameter("usuario", usuario)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Inscripcion buscarPorUsuarioYEvento(Usuario usuario, Evento evento) {
        EntityManager em = getEntityManager();

        try {
            return em.createQuery(
                            "select i from Inscripcion i where i.usuario = :usuario and i.evento = :evento",
                            Inscripcion.class)
                    .setParameter("usuario", usuario)
                    .setParameter("evento", evento)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    public long contarPorEvento(Evento evento) {
        return findAll().stream()
                .filter(i -> i.getEvento().getIdEvento() == evento.getIdEvento())
                .count();
    }

    public Inscripcion crearInscripcion(Usuario usuario, Evento evento) {

        Inscripcion existente = buscarPorUsuarioYEvento(usuario, evento);
        if (existente != null) {
            return null;
        }

        Inscripcion inscripcion = new Inscripcion();

        String codigo = UUID.randomUUID().toString();
        String rutaQr = GeneradorQr.generarQR(codigo);

        inscripcion.setUsuario(usuario);
        inscripcion.setEvento(evento);
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setAsistio(false);
        inscripcion.setEstado("ACTIVA");
        inscripcion.setCodigoQr(codigo);
        inscripcion.setRutaQr(rutaQr);

        crear(inscripcion);

        return inscripcion;
    }

    public Inscripcion buscarPorCodigoQr(String codigo) {

        EntityManager em = getEntityManager();

        try {

            return em.createQuery(
                            "select i from Inscripcion i " +
                                    "join fetch i.usuario " +
                                    "where i.codigoQr = :codigo",
                            Inscripcion.class
                    )
                    .setParameter("codigo", codigo)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }



}