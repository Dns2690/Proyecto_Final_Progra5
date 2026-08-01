package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Draws the invoice popups so they look like a printed receipt instead of a plain message.
 * Everything is written with a monospaced font, which is what keeps the columns lined up.
 * The frames build the text with these pieces and then call {@link #mostrar}.
 */
public final class Tiquete {

    // how many characters wide the paper is
    private static final int ANCHO = 46;

    // a few extra pixels so the last character never touches the border
    private static final int HOLGURA = 10;

    // past this height the receipt gets a scroll bar
    private static final int ALTO_MAXIMO = 480;

    private Tiquete() {
    }

    /** Title of the receipt, with the restaurant on top and the invoice number below. */
    public static String cabecera(String tipo, String numero) {
        return repetir('=') + "\n"
                + centrado("RESTAURANTE UISIL") + "\n"
                + centrado("San Jose, Costa Rica") + "\n"
                + centrado("Ced. Juridica 3-101-000000") + "\n"
                + repetir('=') + "\n"
                + centrado(tipo) + "\n"
                + centrado(numero) + "\n"
                + repetir('-') + "\n";
    }

    /** One line of the header block, like "Fecha:   01/08/2026 16:11". */
    public static String dato(String etiqueta, String valor) {
        return String.format("%-9s %s%n", etiqueta + ":", valor);
    }

    /** The titles of the item columns. */
    public static String titulosItems() {
        return repetir('-') + "\n"
                + String.format("%-4s %-26s %13s%n", "CANT", "DESCRIPCION", "IMPORTE CRC")
                + repetir('-') + "\n";
    }

    /** One item line: quantity, description and amount, with the amount to the right. */
    public static String item(int cantidad, String descripcion, BigDecimal importe) {
        return String.format("%-4d %-26.26s %13s%n", cantidad, descripcion, monto(importe));
    }

    /** A totals line, pushed against the right margin. */
    public static String total(String etiqueta, BigDecimal valor) {
        return String.format("%" + ANCHO + "s%n", etiqueta + ":  " + monto(valor));
    }

    /** Closing message of the receipt. */
    public static String pie(String mensaje) {
        return repetir('=') + "\n" + centrado(mensaje) + "\n" + repetir('=') + "\n";
    }

    /** A separating line. */
    public static String separador() {
        return repetir('-') + "\n";
    }

    /** Shows the receipt inside a dialog, on white paper with its border. */
    public static void mostrar(Component padre, String titulo, String tiquete) {
        Font fuente = new Font(Font.MONOSPACED, Font.PLAIN, 13);

        JTextArea papel = new JTextArea(tiquete.stripTrailing());
        papel.setEditable(false);
        papel.setFont(fuente);
        papel.setLineWrap(false);
        papel.setBackground(Color.WHITE);
        papel.setForeground(new Color(30, 30, 30));
        papel.setMargin(new Insets(14, 16, 14, 16));
        papel.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        papel.setCaretPosition(0);

        // The width is measured by hand: Swing was leaving the dialog narrower than the paper and
        // the receipt got cut off at the edges. The height it does get right, so it is left alone.
        FontMetrics medidas = papel.getFontMetrics(fuente);
        int anchoTexto = 0;
        for (String renglon : papel.getText().split("\n", -1)) {
            anchoTexto = Math.max(anchoTexto, medidas.stringWidth(renglon));
        }

        Insets margen = papel.getMargin();
        Dimension natural = papel.getPreferredSize();
        int ancho = Math.max(anchoTexto + margen.left + margen.right + HOLGURA, natural.width);
        int alto = natural.height + HOLGURA;
        papel.setPreferredSize(new Dimension(ancho, alto));

        // a receipt taller than the screen gets a scroll bar instead of growing forever
        if (alto > ALTO_MAXIMO) {
            JScrollPane scroll = new JScrollPane(papel);
            scroll.setPreferredSize(new Dimension(ancho + 22, ALTO_MAXIMO));
            scroll.setBorder(null);
            JOptionPane.showMessageDialog(padre, scroll, titulo, JOptionPane.PLAIN_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(padre, papel, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    // ------------------ pieces used above ------------------

    /** Amount with a thousands separator and two decimals, as it goes on an invoice. */
    private static String monto(BigDecimal valor) {
        return valor == null ? "0.00" : String.format("%,.2f", valor);
    }

    /** Centers the text on the width of the paper. */
    private static String centrado(String texto) {
        if (texto.length() >= ANCHO) {
            return texto;
        }
        int izquierda = (ANCHO - texto.length()) / 2;
        return " ".repeat(izquierda) + texto;
    }

    /** A full line of the same character. */
    private static String repetir(char c) {
        return String.valueOf(c).repeat(ANCHO);
    }
}
