package src;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TableFrame extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField sqlStatementField;
    JButton executeButton;
    JTextField user;
    JPasswordField password;
    JTextField url;
    private final MySQLDA mySQLDA;

    //Color de fondo en Azul Boca
    private static final Color BOCA_BLUE = new Color(16, 63, 121);
    //Color de fonde en Dorado Boca
    private static final Color BOCA_YELLOW = new Color(243, 178, 41);
    // Color de texto para que sea legible sobre el azul
    private static final Color TEXT_COLOR_ON_BLUE = Color.WHITE;
    // Color de texto para que sea legible sobre el amarillo
    private static final Color TEXT_COLOR_ON_YELLOW = Color.BLACK;

    public TableFrame() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Mostrar Tablas");
        setSize(800, 600);
        setBackground(Color.BLACK);
        setVisible(true);

        JPanel mainControlPanel = new JPanel(new BorderLayout());
        mainControlPanel.add(createConnectionPanel(), BorderLayout.NORTH);
        mainControlPanel.add(createSqlPanel(), BorderLayout.CENTER);
        add(mainControlPanel, BorderLayout.NORTH);

        mySQLDA = new MySQLDA(user.getText(), new String(password.getPassword()), url.getText());

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeSqlStatement(user.getText(), new String(password.getPassword()), url.getText());
            }
        });
    }//TableFrame()

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BOCA_BLUE);

        Color titleColor = TEXT_COLOR_ON_BLUE; // Define el color que quieres (ej. azul)
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Conexión a Base de Datos");
        titledBorder.setTitleColor(titleColor); // Establece el color del título
        panel.setBorder(titledBorder);//Insercion del border title

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        user = new JTextField("root", 15);
        user.setBackground(BOCA_YELLOW);
        user.setForeground(TEXT_COLOR_ON_YELLOW);

        password = new JPasswordField("", 15);
        password.setBackground(BOCA_YELLOW);
        password.setForeground(TEXT_COLOR_ON_YELLOW);

        url = new JTextField("jdbc:mysql://localhost:3306/usuariosdb", 15);
        url.setBackground(BOCA_YELLOW);
        url.setForeground(TEXT_COLOR_ON_YELLOW);

        addLabeledField(panel, gbc, 0, "Usuario:", user);
        addLabeledField(panel, gbc, 1, "Contraseña:", password);
        addLabeledField(panel, gbc, 2, "Conexión:", url);

        return panel;
    }//createConnectionPanel()

    private void addLabeledField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;

        JLabel customLabel = new JLabel(labelText, SwingConstants.RIGHT);
        customLabel.setForeground(TEXT_COLOR_ON_BLUE);
        panel.add(customLabel,gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);

    }//addLabeledField()

    private JPanel createSqlPanel() {
        JPanel sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.setBackground(BOCA_BLUE);

        Color titleColor = TEXT_COLOR_ON_BLUE; 
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Sentencia SQL");
        titledBorder.setTitleColor(titleColor);
        sqlPanel.setBorder(titledBorder);

        sqlStatementField = new JTextField("SELECT * FROM vewPersonasUsuarios");
        sqlStatementField.setBackground(BOCA_YELLOW);
        sqlStatementField.setForeground(TEXT_COLOR_ON_YELLOW);
        sqlStatementField.setPreferredSize(new Dimension(650, 25));

        executeButton = new JButton("Ejecutar SQL");
        executeButton.setPreferredSize(new Dimension(120, 20));
        

        JPanel sqlInputPanel = new JPanel(new BorderLayout());
        sqlInputPanel.add(sqlStatementField, BorderLayout.CENTER);
        sqlInputPanel.add(executeButton, BorderLayout.EAST);

        sqlPanel.add(sqlInputPanel, BorderLayout.CENTER);
        return sqlPanel;
    }//createSqlPanel()

    private void executeSqlStatement(String user, String password, String url) {
        mySQLDA.setUser(user);
        mySQLDA.setPassword(password);
        mySQLDA.setURL(url);
        String sql = sqlStatementField.getText().trim();

        if (sql.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una sentencia SQL.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Si empieza con SELECT (ignora mayúsculas/minúsculas), muestra tabla
            if (sql.toLowerCase().startsWith("select")) {
                List<String[]> resultData = mySQLDA.getQueryList(sql, List.of());
                updateTable(resultData);
            } else {
                int afectadas = mySQLDA.ejecutarSentencia(sql, List.of());
                JOptionPane.showMessageDialog(this, "Sentencia ejecutada correctamente.\nFilas afectadas: " + afectadas, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                model.setRowCount(0); // Limpia la tabla
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la sentencia SQL:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//executeSqlStatement()

    private void updateTable(List<String[]> misDatos) {
        model.setRowCount(0);
        if (!misDatos.isEmpty()) {
            model.setColumnIdentifiers(misDatos.get(0));
            for (int i = 1; i < misDatos.size(); i++) {
                model.addRow(misDatos.get(i));
            }
        }
    }//updateTable()
}

