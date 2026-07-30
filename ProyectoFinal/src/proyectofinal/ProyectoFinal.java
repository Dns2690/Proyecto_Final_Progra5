package proyectofinal;

import connection.ConnectionDB;
import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import vista.FrmLogin;

/**
 * Main class of the project. It opens the login window.
 *
 * @author denis
 */
public class ProyectoFinal {

    public static void main(String[] args) {

        // set the Nimbus look and feel so the screens look better
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

        // test the connection before opening any window
        ConnectionDB conexionDB = new ConnectionDB();
        Connection con = conexionDB.getConexion();
        if (con == null) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos.\nRevise que MySQL este encendido.",
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
            return;
        }
        conexionDB.closeConexion();

        // open the login window
        java.awt.EventQueue.invokeLater(() -> {
            FrmLogin login = new FrmLogin();
            login.setVisible(true);
        });
    }
}
