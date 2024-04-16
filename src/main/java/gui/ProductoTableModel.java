package gui;

import entity.DynamicColumn;
import entity.Producto;
import utils.ExchangeRates;

import javax.swing.table.AbstractTableModel;
import java.util.Comparator;
import java.util.List;

public class ProductoTableModel extends AbstractTableModel {

    public static final int COL_ID = 0;
    public static final int COL_NAME = 1;
    public static final int COL_DESC = 2;
    public static final int COL_UNIT_PRICE = 3;
    public static final int COL_UNIT_WEIGHT = 4;
    public static final int COL_STOCK = 5;
    public static final int COL_UNIT_PRICE_ARS = 6;
    public static final int COL_PRICE_PER_KG = 7;
    public static final int COL_PRICE_PER_KG_ARS = 8;

    private List<Producto> productos;
    private final List<DynamicColumn> dynamicColumns;

    private final String[] baseColumnNames = {
            "ID",
            "Nombre",
            "Descripción",
            "Precio unitario (USD)",
            "Peso unitario (KG)",
            "Stock",
            "Precio unitario (ARS)",
            "Precio por kilo (USD)",
            "Precio por kilo (ARS)"
    };

    public ProductoTableModel(List<Producto> productos, List<DynamicColumn> dynamicColumns) {
        this.productos = productos;
        this.dynamicColumns = dynamicColumns;
    }

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return baseColumnNames.length + dynamicColumns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Producto producto = productos.get(rowIndex);
        if (columnIndex < baseColumnNames.length) {
            return switch (columnIndex) {
                case COL_ID -> producto.getId();
                case COL_NAME -> producto.getNombre();
                case COL_DESC -> producto.getDescripcion();
                case COL_UNIT_PRICE -> producto.getPrecioUnit();
                case COL_UNIT_WEIGHT -> producto.getPesoUnit();
                case COL_STOCK -> producto.getStock();
                // Generated columns
                case COL_UNIT_PRICE_ARS -> producto.getPrecioUnit()
                        .multiply(ExchangeRates.getDolar());
                case COL_PRICE_PER_KG -> producto.getPesoUnit() == null ? null :
                        producto.getPesoUnit()
                                .multiply(producto.getPrecioUnit());
                case COL_PRICE_PER_KG_ARS -> producto.getPesoUnit() == null ? null :
                        producto.getPesoUnit()
                                .multiply(producto.getPrecioUnit())
                                .multiply(ExchangeRates.getDolar());
                default -> null;
            };
        } else {
            return dynamicColumns.get(columnIndex - baseColumnNames.length).getExpression();
        }
    }

    public void sortByColumn(int columnIndex) {
        Comparator<Producto> comparator = switch (columnIndex) {
            case COL_ID -> Comparator.comparingInt(Producto::getId);
            case COL_NAME -> Comparator.comparing(Producto::getNombre);
            case COL_UNIT_PRICE -> Comparator.comparing(Producto::getPrecioUnit);
            default -> null;
        };

        if (comparator != null) {
            productos.sort(comparator);
            fireTableDataChanged();
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex < baseColumnNames.length) {
            return baseColumnNames[columnIndex];
        } else {
            return dynamicColumns.get(columnIndex - baseColumnNames.length).getName();
        }
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        fireTableDataChanged();
    }

    public void addColumn(DynamicColumn column) {
        dynamicColumns.add(column);
        fireTableChanged(null);
    }

    public void removeColumn(int columnIndex) {
        if (columnIndex < baseColumnNames.length)
            return; // Should not happen
        dynamicColumns.remove(columnIndex - baseColumnNames.length);
        fireTableChanged(null);
    }

    public List<DynamicColumn> getDynamicColumns() {
        return dynamicColumns;
    }
}
