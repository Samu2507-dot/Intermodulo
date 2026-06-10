package controladores;

import servicios.UsuarioServicio;
import excepciones.AutenticacionException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import java.io.IOException;

/**
 * Servlet que actúa como puente entre el formulario HTML y la lógica de negocio.
 */
@WebServlet("/Intermodular/src/main/java/controladores/LoginServlet") // Esta es la URL que buscará el formulario HTML
public class LoginServlet extends HttpServlet {

    private UsuarioServicio usuarioServicio;

    @Override
    public void init() throws ServletException {
        try {
            EntityManager em = Persistence.createEntityManagerFactory("RoomlyPU").createEntityManager();
            this.usuarioServicio = new UsuarioServicio(em);
        } catch (Exception e) {
            throw new ServletException("Error crítico al inicializar JPA en el Servlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Capturar lo que el usuario ha escrito en los inputs del HTML
        String txtUsuario = request.getParameter("usuario");
        String txtPass = request.getParameter("passPlana");

        try {
            // 2. Llamar a tu servicio (el que busca en los 3 roles y comprueba BCrypt)
            Object usuarioAutenticado = usuarioServicio.login(txtUsuario, txtPass);

            // 3. Login correcto
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", usuarioAutenticado);

            // 4. Redirigir a la pantalla principal del sistema
            // (Cambia "dashboard.html" por el nombre real de tu página interior)
            response.sendRedirect("InterfazDentroUsuario.html");

        } catch (AutenticacionException e) {
            // 5. Login incorrecto: Volver al login enviando un parámetro de error por la URL
            response.sendRedirect("login.html?error=true");
        }
    }
}