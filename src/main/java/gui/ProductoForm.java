package gui;

import persistence.ProductoDAO;
import entity.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

public class ProductoForm extends JFrame {

    private final JTextField nameField;
    private final JTextField descriptionField;
    private final JTextField unitPriceField;
    private final JTextField unitWeightField;
    private final JTextField stockField;

    private int id;

    public ProductoForm(MainWindow mainWindow, String title, ProductoDAO productoDAO) {
        setTitle(title);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel nameLabel = new JLabel("Nombre");
        nameLabel.setBorder(new EmptyBorder(5,5,5,5));
        nameField = new JTextField(20);
        nameField.setBorder(new EmptyBorder(5,5,5,5));
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(nameField, BorderLayout.CENTER);

        JLabel descriptionLabel = new JLabel("Descripción");
        descriptionLabel.setBorder(new EmptyBorder(5,5,5,5));
        descriptionField = new JTextField(20);
        descriptionField.setBorder(new EmptyBorder(5,5,5,5));
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionPanel.add(descriptionField, BorderLayout.CENTER);

        JLabel unitPriceLabel = new JLabel("Precio unitario");
        unitPriceLabel.setBorder(new EmptyBorder(5,5,5,5));
        unitPriceField = new JTextField(20);
        unitPriceField.setBorder(new EmptyBorder(5,5,5,5));
        JPanel unitPricePanel = new JPanel(new BorderLayout());
        unitPricePanel.add(unitPriceLabel, BorderLayout.NORTH);
        unitPricePanel.add(unitPriceField, BorderLayout.CENTER);

        JLabel unitWeightLabel = new JLabel("Peso unitario");
        unitWeightLabel.setBorder(new EmptyBorder(5,5,5,5));
        unitWeightField = new JTextField(20);
        unitWeightField.setBorder(new EmptyBorder(5,5,5,5));
        JPanel unitWeightPanel = new JPanel(new BorderLayout());
        unitWeightPanel.add(unitWeightLabel, BorderLayout.NORTH);
        unitWeightPanel.add(unitWeightField, BorderLayout.CENTER);

        JLabel stockLabel = new JLabel("Stock");
        stockLabel.setBorder(new EmptyBorder(5,5,5,5));
        stockField = new JTextField(20);
        stockField.setBorder(new EmptyBorder(5,5,5,5));
        JPanel stockPanel = new JPanel(new BorderLayout());
        stockPanel.add(stockLabel, BorderLayout.NORTH);
        stockPanel.add(stockField, BorderLayout.CENTER);

        JButton saveButton = new JButton("Guardar");
        saveButton.setBorder(new EmptyBorder(5,5,5,5));
        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (! validFields())
                    return;

                if (id == -1) {
                    productoDAO.save(new Producto(
                            nameField.getText(),
                            descriptionField.getText(),
                            new BigDecimal(unitPriceField.getText()),
                            unitWeightField.getText().isBlank() ? null : new BigDecimal(unitWeightField.getText()),
                            stockField.getText().isBlank() ? 0 : Integer.parseInt(stockField.getText())
                    ));
                } else {
                    productoDAO.update(new Producto(
                            id,
                            nameField.getText(),
                            descriptionField.getText(),
                            new BigDecimal(unitPriceField.getText()),
                            unitWeightField.getText().isBlank() ? null : new BigDecimal(unitWeightField.getText()),
                            stockField.getText().isBlank() ? 0 : Integer.parseInt(stockField.getText())
                    ));
                }
                mainWindow.refresh();
                dispose();
            }
        });

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10,10,0,10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(namePanel);
        panel.add(descriptionPanel);
        panel.add(unitPricePanel);
        panel.add(unitWeightPanel);
        panel.add(stockPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(5,5,5,5));
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        getContentPane().add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void setFields(Producto p) {
        if (p != null) {
            id = p.getId();
            nameField.setText(p.getNombre());
            descriptionField.setText(p.getDescripcion());
            unitPriceField.setText(p.getPrecioUnit().toString());
            unitWeightField.setText(p.getPesoUnit() != null ? p.getPesoUnit().toString() : "");
            stockField.setText(Integer.toString(p.getStock()));
        } else {
            id = -1;
            nameField.setText("");
            descriptionField.setText("");
            unitPriceField.setText("");
            unitWeightField.setText("");
            stockField.setText("");
        }
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

        try {
            BigDecimal price = new BigDecimal(unitPriceField.getText());
            if (price.signum() == -1)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Precio unitario inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (! unitWeightField.getText().isBlank()) {
            try {
                BigDecimal weight = new BigDecimal(unitWeightField.getText());
                if (weight.signum() == -1)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Peso unitario inválido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
        }

        if (! stockField.getText().isBlank()) {
            try {
                int stock = Integer.parseInt(stockField.getText());
                if (stock < 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Stock inválido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
        }

        return true;
    }
}
