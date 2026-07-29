package proyectofinal;

import connection.ConnectionDB;
import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import vista.FrmLogin;

/**
 * Clase principal del proyecto. Abre la ventana de login.
 *
 * @author denis
 */
public class ProyectoFinal {

    public static void main(String[] args) {

        // le pongo el look and feel Nimbus para que no se vea tan feo
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar Nimbus: " + e.getMessage());
        }

        // pruebo la conexion antes de abrir la ventana
        ConnectionDB conexionDB = new ConnectionDB();
        Connection con = conexionDB.getConexion();
        if (con == null) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos.\nRevise que MySQL este encendido.",
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
            return;
        }
        conexionDB.closeConexion();

        // abro el login
        java.awt.EventQueue.invokeLater(() -> {
            FrmLogin login = new FrmLogin();
            login.setVisible(true);
        });
    }
}
