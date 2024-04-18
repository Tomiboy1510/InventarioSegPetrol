package gui;

import entity.DynamicProductoColumn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class DynamicColumnForm extends JFrame {

    private final JTextField nameField;
    private final JTextField expressionField;

    public DynamicColumnForm(ProductoTableModel tableModel) {
        setTitle("Añadir columna");
        setMinimumSize(new Dimension(200, 0));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10,10,0,10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Nombre");
        nameLabel.setBorder(new EmptyBorder(5,5,5,5));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField = new JTextField(20);
        nameField.setBorder(new EmptyBorder(5,5,5,5));

        JLabel expressionLabel = new JLabel("Expresión");
        expressionLabel.setBorder(new EmptyBorder(5,5,5,5));
        expressionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        expressionField = new JTextField(20);
        expressionField.setBorder(new EmptyBorder(5,5,5,5));

        JButton saveButton = new JButton("Añadir columna");
        saveButton.setBorder(new EmptyBorder(5,5,5,5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(5,5,5,5));
        buttonPanel.add(saveButton);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(expressionLabel);
        panel.add(expressionField);
        panel.add(buttonPanel);

        getContentPane().add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });

        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DynamicProductoColumn col = new DynamicProductoColumn(nameField.getText(), expressionField.getText());
                tableModel.addColumn(col);
                dispose();
            }
        });
    }

    private boolean validFields() {
        if (nameField.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Nombre inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        return true;
    }
}
