package gui;

import entity.*;
import entity.BaseProductoColumn;
import entity.ProductoColumn;
import utils.ExchangeRates;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class ProductoTableModel extends AbstractTableModel {

    private boolean ascendingOrder = true;
    private int sortingColumnIndex;
    private Comparator<Producto> comparator;

    public static final String COL_ID = "ID";
    public static final String COL_NAME = "Nombre";
    public static final String COL_DESC = "Descripción";
    public static final String COL_PRICE = "Precio unitario (USD)";
    public static final String COL_WEIGHT = "Peso unitario (KG)";
    public static final String COL_STOCK = "Stock";

    private List<Producto> productos;
    private final List<ProductoColumn> baseColumns;
    private List<ProductoColumn> columns;

    public ProductoTableModel(List<Producto> productos, List<DynamicProductoColumn> dynamicColumns) {
        this.productos = productos;
        this.baseColumns = new ArrayList<>();

        // I'm using negative IDs to ensure that they are distinct from dynamic columns' IDs which start from 1
        // Atomic columns
        baseColumns.add(new BaseProductoColumn(-1, COL_ID, Producto::getId));
        baseColumns.add(new BaseProductoColumn(-2, COL_NAME, (Producto::getNombre)));
        baseColumns.add(new BaseProductoColumn(-3, COL_DESC, (Producto::getDescripcion)));
        baseColumns.add(new BaseProductoColumn(-4, COL_PRICE, (Producto::getPrecioUnit)));
        baseColumns.add(new BaseProductoColumn(-5, COL_WEIGHT, (Producto::getPesoUnit)));
        baseColumns.add(new BaseProductoColumn(-6, COL_STOCK, Producto::getStock));

        // Generated baseColums
        baseColumns.add(new BaseProductoColumn(-7, "Precio unitario (ARS)",
                p -> p.getPrecioUnit().multiply(ExchangeRates.getDolar())
        ));
        baseColumns.add(new BaseProductoColumn(-8, "Precio por kilo (USD)",
                p -> p.getPesoUnit() == null ? null :
                        p.getPesoUnit().multiply(p.getPrecioUnit())
        ));
        baseColumns.add(new BaseProductoColumn(-9, "Precio por kilo (ARS)",
                p -> p.getPesoUnit() == null ? null :
                        p.getPesoUnit().multiply(p.getPrecioUnit()).multiply(ExchangeRates.getDolar())
        ));

        columns = new ArrayList<>();
        columns.addAll(baseColumns);

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
        //noinspection unchecked
        comparator = Comparator.comparing(
                (Producto p) -> (Comparable<Object>) columns.get(columnIndex).getValue(p, this),
                Comparator.nullsFirst(Comparator.naturalOrder())
        );

        if (comparator == null)
            return;

        if (ascendingOrder)
            comparator = comparator.reversed();

        sortingColumnIndex = columnIndex;
        productos.sort(comparator);
        ascendingOrder = ! ascendingOrder;

        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).getName();
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        fireTableDataChanged();
    }

    public boolean isBaseColumn(int columnIndex) {
        return (columns.get(columnIndex) instanceof BaseProductoColumn);
    }

    public void removeColumn(int columnIndex) {
        if (isBaseColumn(columnIndex))
            return; // Base columns should not be removed

        removeColumnById(columns.get(columnIndex).getId());

        if (columnIndex == sortingColumnIndex) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            int idColumnIndex = IntStream.range(0, columns.size())
                    .filter(i -> columns.get(i).getId() == -1)
                    .findFirst()
                    .getAsInt();

            sortByColumn(idColumnIndex);
        }
    }

    private void removeColumnById(int id) {
        // TODO REESCRIBIR COMPLETAMENTE PARA QUE ELIMINE LAS COLUMNAS PERSISTIDAS
        // QUIZA NO DEBERÍA ESTAR DENTRO DE ESTA CLASE
        /*
        // Get dependencies
        List<Integer> idsForRemoval = new ArrayList<>();
        columns.stream()
                .filter(col -> col instanceof DynamicProductoColumn)
                .filter(col -> ((DynamicProductoColumn) col).getExpression().contains("COL" + id))
                .filter(col -> col.getId() != id)
                .forEach(col -> idsForRemoval.add(col.getId()));

        // Remove dependencies recursively
        idsForRemoval.forEach(this::removeColumnById);

        // Remove column
        columns.removeIf((pc -> pc.getId() == id));

        fireTableChanged(null);
        */
    }

    public List<ProductoColumn> getColumns() {
        return columns;
    }

    public ProductoColumn getColumnById(int id) {
        return columns.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public OptionalInt indexOf(String name) {
        return IntStream.range(0, columns.size())
                .filter(i -> columns.get(i).getName().equals(name))
                .findFirst();
    }

    public void sort() {
        if (comparator == null)
            return;

        productos.sort(comparator);
    }

    public void setDynamicColumns(List<DynamicProductoColumn> dynamicColumns) {
        columns = new ArrayList<>();
        columns.addAll(baseColumns);
        columns.addAll(dynamicColumns);
        fireTableChanged(null);
    }
}