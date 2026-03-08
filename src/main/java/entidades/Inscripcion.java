package entidades;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "inscripcion")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idInscripcion")
    private int idInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventoId")
    private Evento evento;

    private LocalDate fechaInscripcion;

    private LocalTime horaAsistencia;

    private boolean asistio;

    private String codigoQr;

    private String rutaQr;

    private String estado;

    public Inscripcion(){}

    public Inscripcion(int idInscripcion, Usuario usuario, Evento evento, LocalDate fechaInscripcion, LocalTime horaAsistencia, boolean asistio, String codigoQr, String rutaQr, String estado) {
        this.idInscripcion = idInscripcion;
        this.usuario = usuario;
        this.evento = evento;
        this.fechaInscripcion = fechaInscripcion;
        this.horaAsistencia = horaAsistencia;
        this.asistio = asistio;
        this.codigoQr = codigoQr;
        this.rutaQr = rutaQr;
        this.estado = estado;
    }

    // getters y setters

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public LocalTime getHoraAsistencia() {
        return horaAsistencia;
    }

    public void setHoraAsistencia(LocalTime horaAsistencia) {
        this.horaAsistencia = horaAsistencia;
    }

    public boolean isAsistio() {
        return asistio;
    }

    public void setAsistio(boolean asistio) {
        this.asistio = asistio;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public String getRutaQr() {
        return rutaQr;
    }

    public void setRutaQr(String rutaQr) {
        this.rutaQr = rutaQr;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}