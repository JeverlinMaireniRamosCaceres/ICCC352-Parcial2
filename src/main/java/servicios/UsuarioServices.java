package servicios;

import entidades.Usuario;
import jakarta.persistence.EntityManager;

public class UsuarioServices extends GestionDb<Usuario>{

    private static UsuarioServices instancia;

    private UsuarioServices(){
        super(Usuario.class);
    }

    public static UsuarioServices getInstancia(){
        if(instancia == null){
            instancia = new UsuarioServices();
        }
        return instancia;
    }

    public Usuario buscarPorEmail(String email){

        EntityManager em = getEntityManager();

        try{
            return em.createQuery(
                            "select u from Usuario u where u.email = :email",
                            Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();

        }catch(Exception e){
            return null;
        }
    }

}