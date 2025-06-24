package src;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import static src.UIStyle.*;

public class TableFrame extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField sqlStatementField;
    JButton executeButton;
    JTextField user;
    JPasswordField password;
    JTextField url;
    private final MySQLDA mySQLDA;

    private JPanel mainPanel;
    private JPanelImage imagePanel;
    private JScrollPane tableScrollPane;

    public TableFrame() {
        initComponents();
        configLayout();
        configEvents();
        mySQLDA = new MySQLDA(user.getText(), new String(password.getPassword()), url.getText());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Manejo de bases de datos");
        setSize(800, 800);
        setVisible(true);
    }//TableFrame()

    private void initComponents() {
        model = new DefaultTableModel();
        table = new JTable(model);
        tableScrollPane = new JScrollPane(table);

        sqlStatementField = new JTextField("SELECT * FROM vewPersonasUsuarios");
        executeButton = new JButton("Ejecutar SQL");
        user = new JTextField("root", 15);
        password = new JPasswordField("", 15);
        url = new JTextField("jdbc:mysql://localhost:3306/usuariosdb", 15);

        imagePanel = new JPanelImage("image/Boca.jpg");
        imagePanel.setLayout(new OverlayLayout(imagePanel));
        imagePanel.add(tableScrollPane);
        mainPanel = imagePanel;

        tableStyle();
        buttonStyle(executeButton);
        setTransparency();
    }//initComponents()

    private void configLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(createConnectionPanel(), BorderLayout.NORTH);
        controlPanel.add(createSqlPanel(), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);
    }//configLayout()

    private void configEvents() {
        executeButton.addActionListener(e -> {
            String sql = sqlStatementField.getText().trim();
            if (sql.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una sentencia SQL.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            executeSql(sql);
        });
    }//configEvents()

    private void executeSql(String sql) {
        mySQLDA.setUser(user.getText());
        mySQLDA.setPassword(new String(password.getPassword()));
        mySQLDA.setURL(url.getText());

        try {
            if (sql.toLowerCase().startsWith("select")) { // en caso de que sea un select
                List<String[]> data = mySQLDA.getQueryList(sql, List.of());
                updateTable(data);
                imagePanel.setOpacity(0.75f);
            } else {// en caso de que sea otra sentencia sql
                int affected = mySQLDA.executeQuery(sql, List.of());
                JOptionPane.showMessageDialog(this, "Filas afectadas: " + affected, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                model.setRowCount(0);
                model.setColumnCount(0);
                imagePanel.setOpacity(1f);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la sentencia SQL:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//executeSql()

    private void updateTable(List<String[]> data) {
        model.setRowCount(0);
        if (!data.isEmpty()) {
            model.setColumnIdentifiers(data.get(0));
            for (int i = 1; i < data.size(); i++) {
                model.addRow(data.get(i));
            }
        }
    }//updateTable()

    private void tableStyle() {
        JTableHeader header = table.getTableHeader();
        header.setBackground(UIStyle.BOCA_BLUE);
        header.setForeground(UIStyle.TEXT_COLOR_ON_BLUE);
        header.setFont(UIStyle.HEADER_FONT);
        header.setOpaque(true);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(UIStyle.TEXT_COLOR_ON_YELLOW);
                c.setFont(UIStyle.HEADER_FONT);
                if (!isSelected) {
                    c.setBackground(TRANSLUCENT_WHITE);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
    }//tableStyle()

    private void buttonStyle(JButton btn) {
        btn.setBackground(UIStyle.BOCA_BLUE);
        btn.setForeground(UIStyle.TEXT_COLOR_ON_BLUE);
        btn.setFont(UIStyle.BUTTON_FONT);
        btn.setFocusPainted(false);
    }//buttonStyle()

    private void setTransparency() {
        table.setOpaque(false);
        tableScrollPane.setOpaque(false);
        tableScrollPane.getViewport().setOpaque(false);
    }//setTransparency()

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyle.BOCA_BLUE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIStyle.TEXT_COLOR_ON_BLUE), "Conexión a Base de Datos"));
        ((TitledBorder) panel.getBorder()).setTitleColor(UIStyle.TEXT_COLOR_ON_BLUE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        user.setBackground(UIStyle.BOCA_YELLOW);
        user.setForeground(UIStyle.TEXT_COLOR_ON_YELLOW);
        user.setFont(LABEL_FONT);

        password.setBackground(UIStyle.BOCA_YELLOW);
        password.setForeground(UIStyle.TEXT_COLOR_ON_YELLOW);
        password.setFont(LABEL_FONT);

        url.setBackground(UIStyle.BOCA_YELLOW);
        url.setForeground(UIStyle.TEXT_COLOR_ON_YELLOW);
        url.setFont(LABEL_FONT);

        addLabeledField(panel, gbc, 0, "Usuario:", user);
        addLabeledField(panel, gbc, 1, "Contraseña:", password);
        addLabeledField(panel, gbc, 2, "Conexión:", url);

        return panel;
    }//createConnectionPanel()

    private void addLabeledField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;

        JLabel label = new JLabel(labelText, SwingConstants.RIGHT);
        label.setForeground(UIStyle.TEXT_COLOR_ON_BLUE);
        label.setFont(LABEL_FONT);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }//addLabeledField()

    private JPanel createSqlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyle.BOCA_BLUE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIStyle.TEXT_COLOR_ON_BLUE), "Sentencia SQL"));
        ((TitledBorder) panel.getBorder()).setTitleColor(UIStyle.TEXT_COLOR_ON_BLUE);

        sqlStatementField.setBackground(UIStyle.BOCA_YELLOW);
        sqlStatementField.setForeground(UIStyle.TEXT_COLOR_ON_YELLOW);
        sqlStatementField.setPreferredSize(new Dimension(650, 25));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(sqlStatementField, BorderLayout.CENTER);
        inputPanel.add(executeButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.CENTER);
        return panel;
    }//createSqlPanel()
}//class tableframe

