package gui;

import persistence.ProductoDAO;
import entity.Producto;
import utils.ExchangeRates;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private final ProductoDAO productoDAO;
    private JTextField exchangeRateField;
    private JTable productosTable;

    private ProductoForm form = null;

    public MainWindow(ProductoDAO productoDAO) {
        setTitle("SegPetrol - Inventario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        this.productoDAO = productoDAO;

        initComponents();

        // Unfocus all other components
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void initComponents() {
        // TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());

        // Nested panel for elements aligned to the left
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Nuevo");
        JButton deleteButton = new JButton("Eliminar");
        JButton modifyButton = new JButton("Modificar");

        addButton.setFocusable(false);
        deleteButton.setFocusable(false);
        modifyButton.setFocusable(false);

        topLeftPanel.add(addButton);
        topLeftPanel.add(deleteButton);
        topLeftPanel.add(modifyButton);

        // Add left aligned buttons to top panel
        topPanel.add(topLeftPanel, BorderLayout.WEST);

        // Nested panel for elements aligned to the right
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        topRightPanel.add(new JLabel("Tipo de cambio"));

        JButton obtainExchangeRateButton = new JButton("Sincronizar");
        obtainExchangeRateButton.setFocusable(false);
        obtainExchangeRateButton.setToolTipText("Dólar Oficial BNA");

        exchangeRateField = new JTextField(ExchangeRates.getDolar().toString());
        topRightPanel.add(exchangeRateField);
        topRightPanel.add(obtainExchangeRateButton);

        // Add right aligned label and text field to top panel
        topPanel.add(topRightPanel, BorderLayout.EAST);

        // Add top panel to frame
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // TABLE AND SCROLL PANEL
        productosTable = new JTable(new ProductoTableModel(productoDAO.getAll(), new ArrayList<>()));

        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        productosTable.setDefaultRenderer(Object.class, centerRenderer);

        productosTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(productosTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Nested panel for elements aligned to the left in bottom panel
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addColumnButton = new JButton("Añadir columna");
        addColumnButton.setFocusable(false);
        bottomLeftPanel.add(addColumnButton);

        // Add left aligned button to bottom panel
        bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);

        // Nested panel for "elements aligned to the right in bottom panel
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        bottomRightPanel.add(new JLabel("Buscar por nombre"));
        JTextField searchByNameField = new JTextField();
        bottomRightPanel.add(searchByNameField);

        // Add right aligned button to bottom panel
        bottomPanel.add(bottomRightPanel, BorderLayout.EAST);

        // Add bottom panel to frame
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        // ADD LISTENERS TO BUTTONS (AND JTEXTFIELD)
        productosTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = productosTable.getTableHeader().columnAtPoint(e.getPoint());
                ((ProductoTableModel) productosTable.getModel()).sortByColumn(columnIndex);
            }
        });

        // REQUEST EXCHANGE RATE FROM API
        obtainExchangeRateButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExchangeRates.requestDolarOficial();
                if (ExchangeRates.getDolar() != null) {
                    exchangeRateField.setText(ExchangeRates.getDolar().toString());
                    productosTable.repaint();
                }
                else
                    JOptionPane.showMessageDialog(
                            null,
                            "No se pudo obtener el tipo de cambio, ingréselo manualmente",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
            }
        });

        // REVERT TO PREVIOUS VALUE IF AN INVALID ONE WAS ENTERED AS EXCHANGE RATE
        exchangeRateField.addFocusListener(new FocusAdapter() {
            private String previous = exchangeRateField.getText();
            @Override
            public void focusLost(FocusEvent e) {
                String newText = exchangeRateField.getText();
                try {
                    ExchangeRates.setDolar(new BigDecimal(newText));
                    previous = newText;
                    productosTable.repaint();
                } catch (NumberFormatException ex) {
                    exchangeRateField.setText(previous);
                }
            }
        });

        // LOSE FOCUS AFTER ENTERING EXCHANGE RATE
        exchangeRateField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.requestFocusInWindow();
            }
        });

        // SEARCH BY NAME
        searchByNameField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rows = productosTable.getModel().getRowCount();
                if (rows == 0)
                    return;

                String name = searchByNameField.getText().toLowerCase();
                int rowsVisited = 0;
                int s = productosTable.getSelectedRow();

                for (int i = (s + 1) % rows; ; i = (i + 1) % rows) {
                    if (productosTable.getModel().getValueAt(i, ProductoTableModel.COL_NAME).toString().toLowerCase().contains(name)) {
                        productosTable.setRowSelectionInterval(i, i);
                        productosTable.scrollRectToVisible(productosTable.getCellRect(productosTable.getSelectedRow(), 0,true));
                        return;
                    }
                    rowsVisited ++;
                    if (rowsVisited == rows)
                        return;
                }
            }
        });

        // DELETE PRODUCT
        deleteButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (form != null) {
                    form.dispose();
                }

                if (productosTable.getSelectedRow() == -1)
                    return;

                int id = (int) productosTable.getModel().getValueAt(productosTable.getSelectedRow(), ProductoTableModel.COL_ID);

                String[] options = {"Sí", "No"};
                int choice = JOptionPane.showOptionDialog(
                        null,
                        "Eliminar " + productosTable.getModel().getValueAt(productosTable.getSelectedRow(), ProductoTableModel.COL_NAME) + " (ID "+ id +")?",
                        "Eliminar",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice == JOptionPane.YES_OPTION) {
                    productoDAO.delete(id);
                    refresh();
                }
            }
        });

        // ADD PRODUCT
        addButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (form != null) {
                    form.dispose();
                }

                form = new ProductoForm(MainWindow.this, "Nuevo producto", productoDAO);
                form.setFields(null);
                form.setVisible(true);
            }
        });

        // MODIFY PRODUCT
        modifyButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (form != null) {
                    form.dispose();
                }

                int row = productosTable.getSelectedRow();
                if (row == -1)
                    return;

                form = new ProductoForm(
                        MainWindow.this,
                        "Modificar producto #" + productosTable.getModel().getValueAt(row, ProductoTableModel.COL_ID),
                        productoDAO
                );
                Producto p = new Producto(
                        (Integer) productosTable.getModel().getValueAt(row, ProductoTableModel.COL_ID),
                        (String) productosTable.getModel().getValueAt(row, ProductoTableModel.COL_NAME),
                        (String) productosTable.getModel().getValueAt(row, ProductoTableModel.COL_DESC),
                        (BigDecimal) productosTable.getModel().getValueAt(row, ProductoTableModel.COL_UNIT_PRICE),
                        (BigDecimal) productosTable.getModel().getValueAt(row, ProductoTableModel.COL_UNIT_WEIGHT),
                        (Integer) productosTable.getModel().getValueAt(row, ProductoTableModel.COL_STOCK)
                );
                form.setFields(p);
                form.setVisible(true);
            }
        });

        // ADD DYNAMIC COLUMN
        addColumnButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DynamicColumnForm columnForm = new DynamicColumnForm((ProductoTableModel) productosTable.getModel());
                columnForm.setVisible(true);
            }
        });

        // DELETE DYNAMIC COLUMNS
        productosTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = productosTable.columnAtPoint(e.getPoint());
                    if (index <= ProductoTableModel.COL_PRICE_PER_KG_ARS)
                        return;

                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item = new JMenuItem("Eliminar columna");
                    item.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((ProductoTableModel) productosTable.getModel()).removeColumn(index);
                        }
                    });
                    menu.add(item);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public void refresh() {
        ((ProductoTableModel) productosTable.getModel()).setProductos(productoDAO.getAll());
    }
}
