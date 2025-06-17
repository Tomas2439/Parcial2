package src;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        TableFrame miTabla = new TableFrame();
        SwingUtilities.invokeLater(() -> miTabla.setVisible(true));
        return;
    } // main()
}
