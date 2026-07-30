package session;

import dao.AdministradorDAO;
import dao.TipoUsuarioDAO;
import dao.UsuarioDAO;
import model.Administrador;
import model.TipoUsuario;
import model.Usuario;

/**
 * Singleton class that holds the session of the user or administrator currently logged in.
 * It provides methods to log in, log out and read the session data.
 */
public final class SesionActual {

    private static Usuario usuario;
    private static TipoUsuario tipoUsuario;
    private static Administrador administrador;

    private SesionActual() {
    }

    /**
     * Logs an employee in, delegating to {@code UsuarioDAO.login}, which hashes the password with MD5.
     * On success it replaces any previous session.
     */
    public static boolean iniciarSesionEmpleado(String codigo, String contrasena) {
        Usuario u = new UsuarioDAO().login(codigo, contrasena);
        if (u == null) {
            return false;
        }
        cerrarSesion();
        usuario = u;
        tipoUsuario = new TipoUsuarioDAO().findById(u.getId_tipo());
        return true;
    }

    /**
     * Logs an administrator in, delegating to {@code AdministradorDAO.login}, which hashes the password with MD5.
     * On success it replaces any previous session.
     *
     * @return true when the login succeeded, false when it did not
     */
    public static boolean iniciarSesionAdmin(String usuarioAdmin, String contrasena) {
        Administrador a = new AdministradorDAO().login(usuarioAdmin, contrasena);
        if (a == null) {
            return false;
        }
        cerrarSesion();
        administrador = a;
        return true;
    }

    /** Closes the current session (logout). */
    public static void cerrarSesion() {
        usuario = null;
        tipoUsuario = null;
        administrador = null;
    }

    /** @return true when a session is open */
    public static boolean haySesion() {
        return usuario != null || administrador != null;
    }

    /** @return true when the current user is an administrator */
    public static boolean esAdmin() {
        return administrador != null;
    }

    /** @return the logged in employee, or null when there is no session or it is an administrator */
    public static Usuario getUsuario() {
        return usuario;
    }

    /** @return the user type of the logged in employee, or null when there is no session or it is an administrator */
    public static TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    /** @return the logged in administrator, or null when there is no session or it is an employee */
    public static Administrador getAdministrador() {
        return administrador;
    }

    /**
     * @return the code of the logged in employee (e.g. SAL001), or the administrator user name, or null when there is no session
     */
    public static String getCodigo() {
        if (usuario != null) {
            return usuario.getCodigo();
        }
        if (administrador != null) {
            return administrador.getUsuario();
        }
        return null;
    }

    /** @return the name of the logged in employee or administrator, or null when there is no session */
    public static String getNombre() {
        if (usuario != null) {
            return usuario.getNombre();
        }
        if (administrador != null) {
            return administrador.getNombre();
        }
        return null;
    }

    /**
     * @return the role name of the logged in user (e.g. "Salonero", "Cocinero", "Administrador"), or null when there is no session
     */
    public static String getRol() {
        if (administrador != null) {
            return "Administrador";
        }
        if (tipoUsuario != null) {
            return tipoUsuario.getNombre();
        }
        return null;
    }

    /**
     * @return the role prefix of the logged in user (e.g. "SAL", "COS", "BAR"), or null when there is no session or it is an administrator
     */
    public static String getPrefijo() {
        return tipoUsuario != null ? tipoUsuario.getPrefijo() : null;
    }
}
