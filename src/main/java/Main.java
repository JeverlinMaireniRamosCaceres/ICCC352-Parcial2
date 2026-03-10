import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
// import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.rendering.template.JavalinThymeleaf;
import servicios.*;
import entidades.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // Levantar H2 como servidor
        BootStrapServices.getInstancia().init();
        UsuarioServices.getInstancia();

        // Configuracion de archivos estaticos y Thymeleaf
        var app = Javalin.create(config -> {
            // HTTP configuration
            config.http.asyncTimeout = 10_000L;
            config.http.generateEtags = true;

            // Router configuration
            config.router.ignoreTrailingSlashes = true;
            config.router.caseInsensitiveRoutes = true;

            // Static files
            config.staticFiles.add("/publico", Location.CLASSPATH);

            // renderer thymeleaf
            config.fileRenderer(new JavalinThymeleaf());

            // Jetty configuration
            config.jetty.port = 7000;

            // ---------- ENDPOINTS ---------

            // Redireccion login
            config.routes.get("/login", ctx -> {
                ctx.render("templates/login.html");
            });

            // Cerrar sesion
            config.routes.get("/logout", ctx -> {
                ctx.req().getSession().invalidate();
                ctx.redirect("/");
            });

            // Iniciar sesion (login)
            config.routes.post("/login", ctx -> {

                String email = ctx.formParam("email");
                String contrasena = ctx.formParam("contrasena");

                Usuario usuario = UsuarioServices.getInstancia()
                        .buscarPorEmail(email);

                if(usuario != null && usuario.getContrasena().equals(contrasena)){

                    if(usuario.isBloqueado()){
                        ctx.result("Usuario bloqueado");
                        return;
                    }

                    ctx.sessionAttribute("usuario", usuario);

                    if(usuario.getRol().equals("ADMIN")){
                        ctx.redirect("/");
                    }else{
                        ctx.redirect("/");
                    }

                }else{
                    ctx.redirect("/login");
                }

            });


            // Redireccion registro
            config.routes.get("/registro", ctx -> {
                ctx.render("templates/registro.html");
            });

            // Registrarse
            config.routes.post("/registro", ctx -> {

                String nombre = ctx.formParam("nombre");
                String email = ctx.formParam("email");
                String contrasena = ctx.formParam("contrasena");
                String confirmar = ctx.formParam("confirmar");

                // validar contraseña
                if(!contrasena.equals(confirmar)){
                    ctx.result("Las contraseñas no coinciden");
                    return;
                }

                // verificar si el email ya existe
                Usuario existente = UsuarioServices.getInstancia().buscarPorEmail(email);

                if(existente != null){
                    ctx.result("El correo ya está registrado");
                    return;
                }

                Usuario usuario = new Usuario();

                usuario.setNombre(nombre);
                usuario.setEmail(email);
                usuario.setContrasena(contrasena);
                usuario.setRol("PARTICIPANTE");
                usuario.setBloqueado(false);

                UsuarioServices.getInstancia().crear(usuario);

                ctx.redirect("/login");

            });

            // Redirigir index
            config.routes.get("/", ctx -> {

                int page = 1;
                int size = 6;

                String pageParam = ctx.queryParam("page");
                try {
                    if (pageParam != null) {
                        page = Integer.parseInt(pageParam);
                    }
                } catch (NumberFormatException e) {
                    page = 1;
                }

                var todosEventos = EventoServices.getInstancia().findAll();

                var eventosFiltrados = todosEventos.stream()
                        .filter(e -> e.getEstado().equalsIgnoreCase("PUBLICADO")
                                || e.getEstado().equalsIgnoreCase("CANCELADO"))
                        .toList();

                int totalEventos = eventosFiltrados.size();
                int totalPaginas = (int) Math.ceil((double) totalEventos / size);

                if (totalPaginas == 0) totalPaginas = 1;
                if (page < 1) page = 1;
                if (page > totalPaginas) page = totalPaginas;

                int desde = (page - 1) * size;
                int hasta = Math.min(desde + size, totalEventos);

                var eventosPagina = eventosFiltrados.subList(desde, hasta);

                Map<String, Object> model = new HashMap<>();
                model.put("eventos", eventosPagina);
                model.put("currentPage", page);
                model.put("totalPages", totalPaginas);

                Usuario usuarioLogueado = ctx.sessionAttribute("usuario");
                model.put("usuarioLogueado", usuarioLogueado);

                ctx.render("templates/index.html", model);
            });

            // Redirigir detalle evento
            config.routes.get("/evento/{id}", ctx -> {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Evento evento = EventoServices.getInstancia().find(id);

                if (evento == null) {
                    ctx.status(404).result("Evento no encontrado");
                    return;
                }

                Usuario usuarioLogueado = ctx.sessionAttribute("usuario");

                boolean yaInscrito = false;

                long cantidadInscritos = InscripcionServices.getInstancia().contarPorEvento(evento);
                boolean cuposDisponibles = cantidadInscritos < evento.getCupoMaximo();

                if (usuarioLogueado != null) {
                    yaInscrito = InscripcionServices.getInstancia()
                            .buscarPorUsuarioYEvento(usuarioLogueado, evento) != null;
                }

                Map<String, Object> model = new HashMap<>();
                model.put("evento", evento);
                model.put("usuarioLogueado", usuarioLogueado);
                model.put("yaInscrito", yaInscrito);
                model.put("cantidadInscritos", cantidadInscritos);
                model.put("cuposDisponibles", cuposDisponibles);

                ctx.render("templates/detalleEvento.html", model);
            });

            // Redirigir admin
            config.routes.get("/admin", ctx -> {
                Usuario usuario = ctx.sessionAttribute("usuario");
                // si no ha iniciado sesión
                if(usuario == null){
                    ctx.redirect("/login");
                    return;
                }
                // si no es administrador
                if(!usuario.getRol().equals("ADMIN")){
                    ctx.redirect("/");
                    return;
                }
                ctx.render("templates/admin.html");
            });

            // Redirigir usuario (admin)
            config.routes.get("/usuarios", ctx -> {
                Usuario usuario = ctx.sessionAttribute("usuario");
                if(usuario == null){
                    ctx.redirect("/login");
                    return;
                }
                if(!usuario.getRol().equals("ADMIN")){
                    ctx.redirect("/");
                    return;
                }
                var listaUsuarios = UsuarioServices.getInstancia().findAll();
                ctx.attribute("usuarios", listaUsuarios);
                ctx.render("templates/usuario.html");
            });

            // Redirigir evento (admin)
            config.routes.get("/eventos", ctx -> {
                Usuario usuario = ctx.sessionAttribute("usuario");

                Map<String, Object> model = new HashMap<>();
                model.put("usuarioLogueado", usuario);

                if (usuario != null && usuario.getRol().equals("ORGANIZADOR")) {
                    // mostrar solo los eventos de ese organizador
                    List<Evento> eventosOrganizador = EventoServices.getInstancia().findAll()
                            .stream()
                            .filter(e -> e.getOrganizador() != null)
                            .filter(e -> e.getOrganizador().getIdUsuario() == usuario.getIdUsuario())
                            .toList();
                    model.put("eventos", eventosOrganizador);
                } else {
                    model.put("eventos", EventoServices.getInstancia().findAll());
                }

                ctx.render("templates/eventos.html", model);
            });

            // Redirigir crear evento (admin)
            config.routes.get("/eventos/nuevo", ctx -> {
                Map<String, Object> model = new HashMap<>();
                model.put("modoEdicion", false);
                model.put("evento", null);
                ctx.render("templates/crearEvento.html",model);
            });

            // Para crear evento
            config.routes.post("/eventos/crear", ctx -> {
                Evento evento = new Evento();

                Usuario usuario = ctx.sessionAttribute("usuario");

                evento.setTitulo(ctx.formParam("titulo"));
                evento.setLugar(ctx.formParam("lugar"));
                evento.setDescripcion(ctx.formParam("descripcion"));
                evento.setFecha(LocalDate.parse(ctx.formParam("fecha")));
                evento.setHora(LocalTime.parse(ctx.formParam("hora")));
                evento.setCupoMaximo(Integer.parseInt(ctx.formParam("cupoMaximo")));
                evento.setEstado(ctx.formParam("estado"));
                evento.setOrganizador(usuario);

                EventoServices.getInstancia().crear(evento);

                ctx.redirect("/eventos");
            });

            // Redirigir editar evento
            config.routes.get("/eventos/{id}/editar", ctx -> {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Evento evento = EventoServices.getInstancia().find(id);

                if (evento == null) {
                    ctx.status(404).result("Evento no encontrado");
                    return;
                }

                Map<String, Object> model = new HashMap<>();
                model.put("evento", evento);
                model.put("modoEdicion", true);

                ctx.render("templates/crearEvento.html", model);
            });

            // Editar evento
            config.routes.post("/eventos/{id}/editar", ctx -> {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Evento evento = EventoServices.getInstancia().find(id);

                if (evento == null) {
                    ctx.status(404).result("Evento no encontrado");
                    return;
                }

                evento.setTitulo(ctx.formParam("titulo"));
                evento.setLugar(ctx.formParam("lugar"));
                evento.setFecha(LocalDate.parse(ctx.formParam("fecha")));
                evento.setHora(LocalTime.parse(ctx.formParam("hora")));
                evento.setCupoMaximo(Integer.parseInt(ctx.formParam("cupoMaximo")));
                evento.setEstado(ctx.formParam("estado"));
                evento.setDescripcion(ctx.formParam("descripcion"));

                EventoServices.getInstancia().editar(evento);

                ctx.redirect("/eventos");
            });

            // Cancelar evento
            config.routes.post("/eventos/{id}/cancelar", ctx -> {

                int id = Integer.parseInt(ctx.pathParam("id"));
                Evento evento = EventoServices.getInstancia().find(id);

                if (evento == null) {
                    ctx.status(404).result("Evento no encontrado");
                    return;
                }

                evento.setEstado("CANCELADO");

                EventoServices.getInstancia().editar(evento);

                ctx.redirect("/eventos");
            });

            // Inscribirse al evento
            config.routes.post("/eventos/{id}/inscribirse", ctx -> {
                Integer idEvento = Integer.parseInt(ctx.pathParam("id"));

                Usuario usuario = ctx.sessionAttribute("usuario");
                Map<String, Object> respuesta = new HashMap<>();

                if (usuario == null) {
                    ctx.status(401);
                    respuesta.put("ok", false);
                    respuesta.put("mensaje", "Debes iniciar sesión para inscribirte.");
                    ctx.json(respuesta);
                    return;
                }

                Evento evento = EventoServices.getInstancia().find(idEvento);
                if (evento == null) {
                    ctx.status(404);
                    respuesta.put("ok", false);
                    respuesta.put("mensaje", "Evento no encontrado.");
                    ctx.json(respuesta);
                    return;
                }

                long cantidadInscritos = InscripcionServices.getInstancia().contarPorEvento(evento);
                if (cantidadInscritos >= evento.getCupoMaximo()) {
                    ctx.status(400);
                    respuesta.put("ok", false);
                    respuesta.put("mensaje", "Cupos agotados para este evento.");
                    ctx.json(respuesta);
                    return;
                }

                Inscripcion inscripcion = InscripcionServices.getInstancia().crearInscripcion(usuario, evento);

                if (inscripcion == null) {
                    ctx.status(400);
                    respuesta.put("ok", false);
                    respuesta.put("mensaje", "Ya estás inscrito en este evento.");
                    ctx.json(respuesta);
                    return;
                }

                respuesta.put("ok", true);
                respuesta.put("mensaje", "Inscripción realizada correctamente.");
                respuesta.put("codigoQr", inscripcion.getCodigoQr());
                respuesta.put("rutaQr", inscripcion.getRutaQr());

                ctx.json(respuesta);
            });

            // Bloquear usuario
            config.routes.post("/usuarios/bloquear/{id}", ctx -> {
                Usuario admin = ctx.sessionAttribute("usuario");
                if(admin == null || !admin.getRol().equals("ADMIN")){
                    ctx.redirect("/");
                    return;
                }
                int id = Integer.parseInt(ctx.pathParam("id"));
                Usuario usuario = UsuarioServices.getInstancia().find(id);
                if(usuario != null){

                    usuario.setBloqueado(!usuario.isBloqueado());

                    UsuarioServices.getInstancia().editar(usuario);
                }
                ctx.redirect("/usuarios");
            });

            // Redirigir a los eventos del usuario
            config.routes.get("/misEventos", ctx -> {
                Usuario usuario = ctx.sessionAttribute("usuario");

                if (usuario == null) {
                    ctx.redirect("/login");
                    return;
                }

                List<Inscripcion> inscripciones = InscripcionServices.getInstancia().buscarPorUsuario(usuario);

                Map<String, Object> modelo = new HashMap<>();
                modelo.put("usuarioLogueado", usuario);
                modelo.put("inscripciones", inscripciones);

                ctx.render("templates/misEventos.html", modelo);
            });

            // Redirigir a la pagina de escanear un evento
            config.routes.get("/scanner/evento/{id}", ctx -> {

                Usuario usuario = ctx.sessionAttribute("usuario");

                if (usuario == null) {
                    ctx.redirect("/login");
                    return;
                }

                int idEvento = Integer.parseInt(ctx.pathParam("id"));
                Evento evento = EventoServices.getInstancia().find(idEvento);

                if (evento == null) {
                    ctx.status(404).result("Evento no encontrado");
                    return;
                }

                Map<String, Object> model = new HashMap<>();
                model.put("evento", evento);
                model.put("usuarioLogueado", usuario);

                ctx.render("templates/scanner.html", model);
            });

            // Registrar asistencia del escaneo
            config.routes.post("/scanner/asistencia", ctx -> {

                Map<String, Object> respuesta = new HashMap<>();
                Map<String, String> body = ctx.bodyAsClass(Map.class);

                String codigo = body.get("codigoQr");
                String idEventoTexto = body.get("idEvento");

                if (codigo == null || idEventoTexto == null) {
                    respuesta.put("ok", false);
                    respuesta.put("mensaje", "Datos incompletos");
                    ctx.json(respuesta);
                    return;
                }

                int idEvento = Integer.parseInt(idEventoTexto);

                Inscripcion inscripcion = InscripcionServices
                        .getInstancia()
                        .buscarPorCodigoQr(codigo);

                if (inscripcion == null) {
                    respuesta.put("ok", false);
                    respuesta.put("mensaje", "QR no válido");
                    ctx.json(respuesta);
                    return;
                }

                if (inscripcion.getEvento().getIdEvento() != idEvento) {
                    respuesta.put("ok", false);
                    respuesta.put("mensaje", "Este QR no pertenece al evento seleccionado");
                    ctx.json(respuesta);
                    return;
                }

                if (inscripcion.isAsistio()) {
                    respuesta.put("ok", false);
                    respuesta.put("mensaje", "La asistencia ya fue registrada");
                    ctx.json(respuesta);
                    return;
                }

                inscripcion.setAsistio(true);
                inscripcion.setHoraAsistencia(LocalTime.now());

                InscripcionServices.getInstancia().editar(inscripcion);

                respuesta.put("ok", true);
                respuesta.put("nombre", inscripcion.getUsuario().getNombre());

                ctx.json(respuesta);
            });

            // Para mostrar la lista de eventos del organizador
            config.routes.get("/scanner/eventos", ctx -> {

                Usuario usuario = ctx.sessionAttribute("usuario");

                if (usuario == null) {
                    ctx.redirect("/login");
                    return;
                }

                List<Evento> eventosOrganizador = EventoServices.getInstancia().findAll()
                        .stream()
                        .filter(e -> e.getOrganizador() != null)
                        .filter(e -> e.getOrganizador().getIdUsuario() == usuario.getIdUsuario())
                        .toList();

                Map<String, Object> model = new HashMap<>();
                model.put("eventos", eventosOrganizador);
                model.put("usuarioLogueado", usuario);

                ctx.render("templates/scannerEvento.html", model);
            });

            // ----- CRUD USUARIO ------------

            // Eliminar usuario
            config.routes.post("/usuarios/eliminar/{id}", ctx -> {
                Usuario admin = ctx.sessionAttribute("usuario");
                if (admin == null || !admin.getRol().equals("ADMIN")) {
                    ctx.redirect("/");
                    return;
                }
                int id = Integer.parseInt(ctx.pathParam("id"));
                Usuario usuario = UsuarioServices.getInstancia().find(id);

                // no permitir eliminar al admin
                if (usuario != null && !usuario.getRol().equals("ADMIN")) {
                    UsuarioServices.getInstancia().eliminar(id);
                }
                ctx.redirect("/usuarios");
            });

            // Crear usuario (Admin)
            config.routes.post("/usuarios/crear", ctx -> {
                Usuario admin = ctx.sessionAttribute("usuario");
                if (admin == null || !admin.getRol().equals("ADMIN")) {
                    ctx.redirect("/");
                    return;
                }
                Usuario usuario = new Usuario();
                usuario.setNombre(ctx.formParam("nombre"));
                usuario.setEmail(ctx.formParam("email"));
                usuario.setContrasena(ctx.formParam("contrasena"));
                usuario.setRol(ctx.formParam("rol"));
                usuario.setBloqueado(false);

                UsuarioServices.getInstancia().crear(usuario);
                ctx.redirect("/usuarios");
            });

            // Editar usuario (Vista, Admin)
            config.routes.get("/usuarios/editar/{id}", ctx -> {
                Usuario admin = ctx.sessionAttribute("usuario");
                if (admin == null || !admin.getRol().equals("ADMIN")) {
                    ctx.redirect("/");
                    return;
                }
                int id = Integer.parseInt(ctx.pathParam("id"));
                Usuario usuario = UsuarioServices.getInstancia().find(id);
                ctx.attribute("usuarioEditar", usuario);
                ctx.attribute("mostrarModalEditar", true);
                var listaUsuarios = UsuarioServices.getInstancia().findAll();
                ctx.attribute("usuarios", listaUsuarios);
                ctx.render("templates/usuario.html");
            });

            // Modificar datos del usuario (POST, Admin)
            config.routes.post("/usuarios/editar/{id}", ctx -> {
                Usuario admin = ctx.sessionAttribute("usuario");
                if (admin == null || !admin.getRol().equals("ADMIN")) {
                    ctx.redirect("/");
                    return;
                }

                int id = Integer.parseInt(ctx.pathParam("id"));
                Usuario usuario = UsuarioServices.getInstancia().find(id);
                usuario.setNombre(ctx.formParam("nombre"));
                usuario.setEmail(ctx.formParam("email"));
                usuario.setRol(ctx.formParam("rol"));

                UsuarioServices.getInstancia().editar(usuario);
                ctx.redirect("/usuarios");
            });


        }).start();

    }
}
