package session;

import dao.AdministradorDAO;
import dao.TipoUsuarioDAO;
import dao.UsuarioDAO;
import model.Administrador;
import model.TipoUsuario;
import model.Usuario;

/**
 * Static holder for the currently logged-in session, shared by every view.
 * Supports the two login paths of the app: employees ({@code usuario} table,
 * roles Salonero/Cocinero/Bartender/Cajero) and administrators
 * ({@code administrador} table). Only one session exists at a time; each
 * role's JFrame reads the logged-in identity from here.
 */
public final class SesionActual {

    private static Usuario usuario;
    private static TipoUsuario tipoUsuario;
    private static Administrador administrador;

    private SesionActual() {
    }

    /**
     * Attempts an employee login (delegates to {@code UsuarioDAO.login},
     * which hashes the password with MD5 and requires an active account).
     * On success the previous session (if any) is replaced and the
     * employee's role ({@code tipo_usuario}) is loaded too.
     *
     * @return true if the credentials are valid
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
     * Attempts an administrator login (delegates to
     * {@code AdministradorDAO.login}, which hashes the password with MD5).
     * On success the previous session (if any) is replaced.
     *
     * @return true if the credentials are valid
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

    /** Clears the current session (logout). */
    public static void cerrarSesion() {
        usuario = null;
        tipoUsuario = null;
        administrador = null;
    }

    /** @return true if someone (employee or admin) is logged in */
    public static boolean haySesion() {
        return usuario != null || administrador != null;
    }

    /** @return true if the current session belongs to an administrator */
    public static boolean esAdmin() {
        return administrador != null;
    }

    /** @return the logged-in employee, or null if none / admin session */
    public static Usuario getUsuario() {
        return usuario;
    }

    /** @return the logged-in employee's role, or null if none / admin session */
    public static TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    /** @return the logged-in admin, or null if none / employee session */
    public static Administrador getAdministrador() {
        return administrador;
    }

    /**
     * @return the identifier of whoever is logged in: employee code
     *         (e.g. SAL001) or admin username; null if no session
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

    /** @return display name of whoever is logged in, or null if no session */
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
     * @return role name for the current session: "Administrador" for admins,
     *         the {@code tipo_usuario.nombre} (e.g. "Salonero") for
     *         employees; null if no session
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
     * @return the employee role prefix (e.g. SAL, COS, BAR, CAJ), or null
     *         for admin sessions / no session
     */
    public static String getPrefijo() {
        return tipoUsuario != null ? tipoUsuario.getPrefijo() : null;
    }
}
