package gui;

import entity.*;
import entity.BaseProductoColumn;
import entity.ProductoColumn;
import utils.ExchangeRates;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProductoTableModel extends AbstractTableModel {

    public static final int COL_ID = 0;
    public static final int COL_NAME = 1;
    public static final int COL_DESC = 2;
    public static final int COL_UNIT_PRICE = 3;
    public static final int COL_UNIT_WEIGHT = 4;
    public static final int COL_STOCK = 5;

    public static final int DYNAMIC_COLUMNS_STARTING_INDEX = 9;

    private boolean ascendingOrder = true;

    private List<Producto> productos;
    private final List<ProductoColumn> columns;

    // Dummy Producto for determining data types of columns
    private final Producto dummy = new Producto(-1, "", "", BigDecimal.ZERO, BigDecimal.ZERO, 0);

    public ProductoTableModel(List<Producto> productos, List<DynamicProductoColumn> dynamicColumns) {
        this.productos = productos;
        this.columns = new ArrayList<>();

        // I'm using negative IDs to ensure that they are distinct from dynamic columns' IDs which start from 1
        // Atomic columns
        columns.add(new BaseProductoColumn(-1, "ID", Producto::getId));
        columns.add(new BaseProductoColumn(-2, "Nombre", (Producto::getNombre)));
        columns.add(new BaseProductoColumn(-3, "Descripción", (Producto::getDescripcion)));
        columns.add(new BaseProductoColumn(-4, "Precio unitario (USD)", (Producto::getPrecioUnit)));
        columns.add(new BaseProductoColumn(-5, "Peso unitario (KG)", (Producto::getPesoUnit)));
        columns.add(new BaseProductoColumn(-6, "Stock", Producto::getStock));

        // Generated columns
        columns.add(new BaseProductoColumn(-7, "Precio unitario (ARS)",
                p -> p.getPrecioUnit().multiply(ExchangeRates.getDolar())
        ));
        columns.add(new BaseProductoColumn(-8, "Precio por kilo (USD)",
                p -> p.getPesoUnit() == null ? null :
                        p.getPesoUnit().multiply(p.getPrecioUnit())
        ));
        columns.add(new BaseProductoColumn(-9, "Precio por kilo (ARS)",
                p -> p.getPesoUnit() == null ? null :
                        p.getPesoUnit().multiply(p.getPrecioUnit()).multiply(ExchangeRates.getDolar())
        ));

        // Dynamic columns
        columns.addAll(dynamicColumns);
    }

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getValue(productos.get(rowIndex), this);
    }

    public void sortByColumn(int columnIndex) {
        @SuppressWarnings("unchecked")
        Comparator<Producto> comparator = Comparator.comparing(
                (Producto p) -> (Comparable<Object>) columns.get(columnIndex).getValue(p, this),
                Comparator.nullsFirst(Comparator.naturalOrder())
        );

        if (comparator != null) {
            if (ascendingOrder)
                comparator = comparator.reversed();
            productos.sort(comparator);
            ascendingOrder = ! ascendingOrder;
            fireTableDataChanged();
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).getName();
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        fireTableDataChanged();
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void addColumn(DynamicProductoColumn column) {
        columns.add(column);
        fireTableChanged(null);
    }

    public void removeColumn(int columnIndex) {
        if (columns.get(columnIndex) instanceof BaseProductoColumn)
            return; // Base columns should not be removed

        // Remove column and those other columns that depend on it

    }

    public Producto getDummy() {
        return dummy;
    }

    public List<ProductoColumn> getColumns() {
        return columns;
    }

    public ProductoColumn getColumnById(int id) {
        for (ProductoColumn c : columns) {
            if (c.getId() == id)
                return c;
        }
        return null;
    }
}