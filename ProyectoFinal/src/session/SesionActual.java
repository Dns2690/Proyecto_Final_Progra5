package session;

import dao.AdministradorDAO;
import dao.TipoUsuarioDAO;
import dao.UsuarioDAO;
import model.Administrador;
import model.TipoUsuario;
import model.Usuario;

/**
 * Clase singleton que mantiene la sesión actual del usuario o administrador logueado.
 * Proporciona métodos para iniciar/cerrar sesión y obtener información de la sesión.   
 */
public final class SesionActual {

    private static Usuario usuario;
    private static TipoUsuario tipoUsuario;
    private static Administrador administrador;

    private SesionActual() {
    }

    /**
     * inicia sesión de un empleado (delegando a {@code UsuarioDAO.login}, que hashea la contraseña con MD5).
     * En caso de éxito, reemplaza la sesión previa (si la hubiera).
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
     * inicia sesión de un administrador (delegando a {@code AdministradorDAO.login}, que hashea la contraseña con MD5).
     * En caso de éxito, reemplaza la sesión previa (si la hubiera).
     *
     * @return true si el login fue exitoso, false si no
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

    /** Cierra la sesión actual (logout). */
    public static void cerrarSesion() {
        usuario = null;
        tipoUsuario = null;
        administrador = null;
    }

    /** @return true si hay una sesión iniciada */
    public static boolean haySesion() {
        return usuario != null || administrador != null;
    }

    /** @return true si el usuario actual es un administrador */
    public static boolean esAdmin() {
        return administrador != null;
    }

    /** @return el empleado logueado, o null si no hay sesión / es administrador */
    public static Usuario getUsuario() {
        return usuario;
    }

    /** @return el tipo de usuario del empleado logueado, o null si no hay sesión / es administrador */
    public static TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    /** @return el administrador logueado, o null si no hay sesión / es empleado */
    public static Administrador getAdministrador() {
        return administrador;
    }

    /**
     * @return el código del empleado logueado (ej. SAL001), o el nombre de usuario del administrador logueado, o null si no hay sesión
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

    /** @return el nombre del empleado logueado, o el nombre del administrador logueado, o null si no hay sesión */
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
     * @return nombre del rol del usuario logueado (ej. "Salonero", "Cocinero", "Administrador"), o null si no hay sesión
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
     * @return el prefijo del rol del usuario logueado (ej. "SAL", "COS", "BAR"), o null si no hay sesión o es administrador
     */
    public static String getPrefijo() {
        return tipoUsuario != null ? tipoUsuario.getPrefijo() : null;
    }
}
