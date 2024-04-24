package gui;

import entity.Producto;
import persistence.ProductoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class UpdatePriceForm extends JFrame {

    private final JTextField percentageField;

    public UpdatePriceForm(JTable table, MainWindow mainWindow, ProductoDAO productoDAO) {
        setTitle("Actualizar precio");
        setMinimumSize(new Dimension(200, 0));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10,10,0,10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Porcentaje de aumento");
        label.setBorder(new EmptyBorder(5,5,5,5));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        percentageField = new JTextField(20);
        percentageField.setBorder(new EmptyBorder(5,5,5,5));

        JButton saveButton = new JButton("Guardar");
        saveButton.setBorder(new EmptyBorder(5,5,5,5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(5,5,5,5));
        buttonPanel.add(saveButton);

        panel.add(label);
        panel.add(percentageField);
        panel.add(buttonPanel);

        getContentPane().add(panel, BorderLayout.CENTER);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });

        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BigDecimal percentage = getPercentage();
                if (percentage == null)
                    return;

                int[] selectedRows = table.getSelectedRows();
                ProductoTableModel tableModel = ((ProductoTableModel) table.getModel());

                for (int row : selectedRows) {
                    BigDecimal price = (BigDecimal) tableModel.getValueAt(row, tableModel.indexOf(ProductoTableModel.COL_PRICE).getAsInt());

                    Producto p = new Producto(
                            (Integer) tableModel.getValueAt(row, tableModel.indexOf(ProductoTableModel.COL_ID).getAsInt()),
                            (String) tableModel.getValueAt(row, tableModel.indexOf(ProductoTableModel.COL_NAME).getAsInt()),
                            (String) tableModel.getValueAt(row, tableModel.indexOf(ProductoTableModel.COL_DESC).getAsInt()),
                            price.add(price.multiply(percentage.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_EVEN))),
                            (BigDecimal) tableModel.getValueAt(row, tableModel.indexOf(ProductoTableModel.COL_WEIGHT).getAsInt()),
                            (Integer) tableModel.getValueAt(row, tableModel.indexOf(ProductoTableModel.COL_STOCK).getAsInt())
                    );

                    productoDAO.update(p);
                }
                mainWindow.refresh();
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private BigDecimal getPercentage() {
        try {
            return new BigDecimal(percentageField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Porcentaje inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }
}
